package com.radarcarioca.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.radarcarioca.data.model.RideOffer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

private const val TAG = "RadarAccessibility"
private const val PKG_UBER = "com.ubercab.driver"
private const val PKG_99   = "com.app99.driver"
private const val DEBOUNCE_MS = 600L
private const val MAX_NODES   = 300
private const val MAX_DEPTH   = 10

// ─── View IDs conhecidos por versão — fallback para busca recursiva ──────────
// Atenção: esses IDs mudam a cada update do app. Use Layout Inspector para atualizar.
private val UBER_FARE_IDS = listOf(
    "com.ubercab.driver:id/fare_amount",
    "com.ubercab.driver:id/trip_fare",
    "com.ubercab.driver:id/request_fare"
)
private val UBER_DEST_IDS = listOf(
    "com.ubercab.driver:id/destination_name",
    "com.ubercab.driver:id/waypoint_address",
    "com.ubercab.driver:id/trip_destination"
)
private val UBER_DIST_IDS = listOf(
    "com.ubercab.driver:id/trip_distance",
    "com.ubercab.driver:id/distance_away"
)
private val UBER_TIME_IDS = listOf(
    "com.ubercab.driver:id/trip_duration",
    "com.ubercab.driver:id/estimated_time",
    "com.ubercab.driver:id/trip_time_minutes"
)
private val APP99_FARE_IDS = listOf(
    "com.app99.driver:id/tvPrice",
    "com.app99.driver:id/ride_price",
    "com.app99.driver:id/fare_value"
)
private val APP99_DEST_IDS = listOf(
    "com.app99.driver:id/tvDestination",
    "com.app99.driver:id/destination_text",
    "com.app99.driver:id/ride_destination"
)
private val APP99_TIME_IDS = listOf(
    "com.app99.driver:id/tvDuration",
    "com.app99.driver:id/trip_time",
    "com.app99.driver:id/ride_duration"
)

// ─── Regex de alta precisão ───────────────────────────────────────────────────
private val REGEX_FARE     = Regex("""(?:R\$|BRL)\s*(\d{1,3}(?:[.,]\d{3})*)[,.](\d{2})""")
private val REGEX_FARE_INT = Regex("""(?:R\$|BRL)\s*(\d{1,3})(?!\d)""")
private val REGEX_DIST_KM  = Regex("""(\d+)[,.](\d*)\s*km""", RegexOption.IGNORE_CASE)
private val REGEX_DIST_M   = Regex("""(\d+)\s*m(?:\s|$)""")
// Parseia "12 min", "12min", "~12 min", "aprox. 15 min" → inteiro
private val REGEX_TIME_MIN = Regex("""(?:~|aprox\.?\s*)?(\d{1,3})\s*min""", RegexOption.IGNORE_CASE)
private val REGEX_ADDRESS  = Regex(
    """(?:Rua|R\.|Av\.|Avenida|Travessa|Tv\.|Pra[çc]a|Pc\.|Estrada|Est\.|Rodovia|Alameda|Al\.|Largo|Lg\.|Beco|Bc\.|Comunidade|Vila|Morro|Complexo)\s+.{5,}""",
    RegexOption.IGNORE_CASE
)

@AndroidEntryPoint
class RadarAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var lastOfferHash  = ""
    private var lastEventTime  = 0L

    companion object {
        val rideOfferFlow = MutableSharedFlow<RideOffer>(replay = 0, extraBufferCapacity = 1)
        val offerFlow: SharedFlow<RideOffer> = rideOfferFlow
        var isServiceRunning = false
    }

    // ─── CICLO DE VIDA ────────────────────────────────────────────────

    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceRunning = true
        Log.i(TAG, "Service conectado — monitorando Uber ($PKG_UBER) e 99 ($PKG_99)")

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames  = arrayOf(PKG_UBER, PKG_99)
            feedbackType  = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 150
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        serviceScope.cancel()
        Log.i(TAG, "Service encerrado")
    }

    override fun onInterrupt() { Log.w(TAG, "Service interrompido pelo sistema") }

    // ─── EVENTO PRINCIPAL ─────────────────────────────────────────────

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val now = System.currentTimeMillis()
        if (now - lastEventTime < DEBOUNCE_MS) return
        lastEventTime = now

        val pkg = event.packageName?.toString() ?: return
        if (pkg != PKG_UBER && pkg != PKG_99) return

        // Coleta raízes de TODAS as janelas do app — o Uber renderiza a oferta
        // em uma janela overlay separada que rootInActiveWindow não enxerga
        val roots = mutableListOf<AccessibilityNodeInfo>()
        try {
            windows?.forEach { w ->
                if (w.root != null) roots.add(w.root)
            }
        } catch (_: Exception) {}
        // fallback: rootInActiveWindow
        if (roots.isEmpty()) {
            try { rootInActiveWindow?.let { roots.add(it) } } catch (_: Exception) {}
        }
        if (roots.isEmpty()) return

        serviceScope.launch {
            try {
                Log.i(TAG, "Evento [$pkg] tipo=${event.eventType} — janelas=${roots.size}")
                var offer: RideOffer? = null
                for (root in roots) {
                    offer = when (pkg) {
                        PKG_UBER -> extractOffer(root, "uber", UBER_FARE_IDS, UBER_DEST_IDS, UBER_DIST_IDS, UBER_TIME_IDS)
                        PKG_99   -> extractOffer(root, "99",   APP99_FARE_IDS, APP99_DEST_IDS, emptyList(),  APP99_TIME_IDS)
                        else     -> null
                    }
                    if (offer != null) break
                }

                offer?.let {
                    val hash = "${it.destinationText}_${it.fareValue}"
                    if (hash != lastOfferHash) {
                        lastOfferHash = hash
                        Log.i(TAG, "[${it.sourceApp}] Oferta: ${it.destinationText} | R$ ${it.fareValue} | ${it.rideDistanceKm}km")
                        rideOfferFlow.emit(it)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Erro ao processar evento: ${e.message}")
            }
        }
    }

    // ─── EXTRAÇÃO UNIFICADA ───────────────────────────────────────────

    private fun extractOffer(
        root: AccessibilityNodeInfo,
        sourceApp: String,
        fareIds: List<String>,
        destIds: List<String>,
        distIds: List<String>,
        timeIds: List<String> = emptyList()
    ): RideOffer? {
        // 1ª tentativa: busca por IDs conhecidos (rápida e precisa)
        val fareText  = findByIds(root, fareIds)
        val destText  = findByIds(root, destIds)
        val distText  = findByIds(root, distIds)
        val timeText  = findByIds(root, timeIds)

        // 2ª tentativa: varredura recursiva (resiliente a updates do app)
        // Lazy → só executa se algum campo ainda não foi encontrado pelos IDs
        val allTexts by lazy {
            mutableListOf<String>().also { collectTextNodes(root, it) }
        }

        val fare = fareText?.let { parseFare(it) }
            ?: allTexts.firstNotNullOfOrNull { parseFare(it) }?.takeIf { it in 1.0..500.0 }
            ?: run {
                Log.w(TAG, "[$sourceApp] Sem valor de tarifa. Textos encontrados (${allTexts.size}): ${allTexts.take(15)}")
                return null
            }

        val destination = destText?.takeIf { REGEX_ADDRESS.containsMatchIn(it) }
            ?: allTexts.firstOrNull { REGEX_ADDRESS.containsMatchIn(it) }
            ?: run {
                Log.w(TAG, "[$sourceApp] Sem endereço válido. Textos encontrados: ${allTexts.take(15)}")
                return null
            }

        val distance = distText?.let { parseDistanceKm(it) }
            ?: allTexts.firstNotNullOfOrNull { parseDistanceKm(it) }?.takeIf { it in 0.5..120.0 }
            ?: estimateDistance(fare)

        // Tempo estimado da corrida — fallback: estimativa por distância (30 km/h médio RJ)
        val estimatedMin = timeText?.let { parseTimeMinutes(it) }
            ?: allTexts.firstNotNullOfOrNull { parseTimeMinutes(it) }?.takeIf { it in 1..180 }
            ?: ((distance / 30.0) * 60).toInt().coerceAtLeast(1)

        val deadhead = allTexts
            .mapNotNull { parseDistanceKm(it) }
            .filter { it in 0.1..8.0 && it < distance }
            .minOrNull() ?: 2.0

        return RideOffer(
            destinationText      = destination.take(120).trim(),
            fareValue            = fare,
            rideDistanceKm       = distance,
            deadheadDistanceKm   = deadhead,
            estimatedMinutes     = estimatedMin,
            sourceApp            = sourceApp
        )
    }

    // ─── BUSCA POR IDs (findAccessibilityNodeInfosByViewId) ───────────

    /**
     * Busca um texto em uma lista de resource-IDs conhecidos.
     * Mais rápido que varredura completa quando os IDs são válidos.
     */
    private fun findByIds(root: AccessibilityNodeInfo, ids: List<String>): String? {
        for (id in ids) {
            try {
                val nodes = root.findAccessibilityNodeInfosByViewId(id)
                val text = nodes?.firstOrNull()?.let { node ->
                    (node.text?.toString() ?: node.contentDescription?.toString())
                        ?.trim()?.takeIf { it.isNotBlank() }
                }
                @Suppress("DEPRECATION")
                nodes?.forEach { it.recycle() }
                if (text != null) return text
            } catch (e: Exception) {
                Log.v(TAG, "ID $id não encontrado: ${e.message}")
            }
        }
        return null
    }

    // ─── VARREDURA RECURSIVA (fallback) ──────────────────────────────

    private fun collectTextNodes(
        node: AccessibilityNodeInfo?,
        result: MutableList<String>,
        depth: Int = 0
    ) {
        node ?: return
        if (depth > MAX_DEPTH || result.size > MAX_NODES) return

        // Anti-crash: node?.let para acessar seguramente
        node.let {
            val text = it.text?.toString()?.trim()
            val desc = it.contentDescription?.toString()?.trim()
            if (!text.isNullOrBlank() && text.length > 2) result.add(text)
            if (!desc.isNullOrBlank() && desc.length > 2 && desc != text) result.add(desc)

            for (i in 0 until it.childCount) {
                val child = try { it.getChild(i) } catch (e: Exception) { null }
                collectTextNodes(child, result, depth + 1)
                @Suppress("DEPRECATION")
                child?.recycle()
            }
        }
    }

    // ─── PARSERS ──────────────────────────────────────────────────────

    /** Parseia "R$ 23,50", "R$23.50", "BRL 23,50" → 23.50 */
    private fun parseFare(text: String): Double? {
        REGEX_FARE.find(text)?.let { m ->
            val intPart = m.groupValues[1].replace(".", "").replace(",", "")
            val decPart = m.groupValues[2].padEnd(2, '0')
            return "$intPart.$decPart".toDoubleOrNull()
        }
        REGEX_FARE_INT.find(text)?.let { m ->
            return m.groupValues[1].toDoubleOrNull()
        }
        return null
    }

    /** Parseia "12,5 km", "12.5km", "800 m" → km */
    private fun parseDistanceKm(text: String): Double? {
        REGEX_DIST_KM.find(text)?.let { m ->
            val dec = m.groupValues[2].ifEmpty { "0" }.take(2)
            return "${m.groupValues[1]}.$dec".toDoubleOrNull()
        }
        REGEX_DIST_M.find(text)?.let { m ->
            val meters = m.groupValues[1].toDoubleOrNull() ?: return null
            if (meters in 100.0..9999.0) return meters / 1000.0
        }
        return null
    }

    /**
     * Parseia "12 min", "~15 min", "aprox. 8 min" → Int (minutos).
     * Retorna null se o texto não contiver um tempo válido.
     */
    private fun parseTimeMinutes(text: String): Int? =
        REGEX_TIME_MIN.find(text)?.groupValues?.get(1)?.toIntOrNull()

    /** Estimativa de distância a partir do valor (fallback de último recurso) */
    private fun estimateDistance(fare: Double): Double = (fare / 3.8).coerceIn(1.0, 50.0)
}
