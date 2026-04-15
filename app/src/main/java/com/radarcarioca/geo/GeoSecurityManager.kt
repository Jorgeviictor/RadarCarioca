package com.radarcarioca.geo

import android.content.Context
import android.util.Log
import com.radarcarioca.data.model.AlertLevel
import com.radarcarioca.data.model.GeoFeature
import com.radarcarioca.data.model.SecurityResult
import com.radarcarioca.domain.repository.GeoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

private const val TAG = "GeoSecurityManager"

// Raio fixo de 500 m para zona amarela (Atenção), conforme regra de negócio
private const val WARNING_BUFFER_KM = 0.5

// Raio de perigo para features do tipo Point (ex: bocas de fumo mapeadas como ponto)
private const val POINT_DANGER_RADIUS_KM = 0.15   // 150 m → dentro → DANGER

/**
 * ═══════════════════════════════════════════════════════════════════
 * MOTOR DE INTELIGÊNCIA TERRITORIAL
 * ═══════════════════════════════════════════════════════════════════
 *
 * Responsável por:
 * 1. Carregar o GeoJSON do arquivo local (assets) para o Room Database
 * 2. Verificar se uma coordenada está dentro de área de risco
 * 3. Calcular distância entre ponto e features do GeoJSON
 *
 * Estratégia de performance:
 * - Bounding box query no Room (SQL) para filtrar features distantes
 * - Cálculo de distância Haversine apenas nas features próximas
 * - Meta: < 50ms para checkSafety()
 */
@Singleton
class GeoSecurityManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geoRepository: GeoRepository
) {
    // Cache em memória para queries ultra-rápidas durante o turno
    private var featuresCache: List<GeoFeature> = emptyList()
    private var cacheLoaded = false

    // ─── INICIALIZAÇÃO ────────────────────────────────────────────────

    /**
     * Inicializa o motor: carrega do Room ou do arquivo de assets.
     * Deve ser chamado no RadarForegroundService.onCreate()
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        val count = geoRepository.count()
        if (count == 0) {
            Log.i(TAG, "Room vazio — carregando GeoJSON dos assets...")
            loadFromAssets()
        } else {
            Log.i(TAG, "Room com $count features — carregando cache...")
        }
        featuresCache = geoRepository.getAll()
        cacheLoaded = true
        Log.i(TAG, "GeoSecurityManager pronto: ${featuresCache.size} features em cache")
    }

    /**
     * Carrega o arquivo GeoJSON dos assets e persiste no Room Database.
     * Arquivo esperado: assets/Mapa Das Faccoes RJ.geojson
     */
    private suspend fun loadFromAssets() = withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open("mapa_faccoes_rj.geojson")
                .bufferedReader()
                .use { it.readText() }

            val features = parseGeoJson(json)
            geoRepository.insertAll(features)
            Log.i(TAG, "Inseridas ${features.size} features no Room Database")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar GeoJSON: ${e.message}")
        }
    }

    // ─── VERIFICAÇÃO DE SEGURANÇA ─────────────────────────────────────

    /**
     * Verifica se um ponto GPS está em área de risco.
     * Usa cache em memória para máxima velocidade.
     *
     * @param lat Latitude do destino
     * @param lng Longitude do destino
     * @param bufferMeters Raio de amortecimento (padrão: 300m)
     * @return SecurityResult.Safe ou SecurityResult.Danger
     */
    /**
     * Verifica a segurança de um destino com 3 estados:
     * - Safe    → destino fora de qualquer área de risco e fora do buffer
     * - Warning → destino dentro do buffer de 500 m de uma área perigosa → AMARELO
     * - Danger  → destino exatamente dentro de uma área de risco → ROXO + vibração
     *
     * Prioridade: Danger > Warning > Safe.
     * Em caso de múltiplas features próximas, retorna o Warning mais próximo.
     */
    fun checkSafety(lat: Double, lng: Double, bufferMeters: Int = 400): SecurityResult {
        if (!cacheLoaded || featuresCache.isEmpty()) {
            Log.w(TAG, "Cache não carregado — retornando Safe por padrão")
            return SecurityResult.Safe
        }

        // O bufferMeters do config é ignorado aqui — usamos WARNING_BUFFER_KM (500 m) fixo
        // conforme regra de negócio. O bufferMeters pode ser usado futuramente para customização.
        var closestWarning: SecurityResult.Warning? = null

        for (feature in featuresCache) {
            // Filtragem rápida por bounding box (±0.06° ≈ ~6,7 km) antes do cálculo preciso
            val dlat = abs(lat - feature.centerLat)
            val dlng = abs(lng - feature.centerLng)
            if (dlat > 0.06 || dlng > 0.06) continue

            val result = when (feature.geometryType) {
                "Point"      -> checkPoint(lat, lng, feature)
                "LineString" -> checkLineString(lat, lng, feature)
                "Polygon"    -> checkPolygon(lat, lng, feature)
                else         -> null
            }

            when (result) {
                is SecurityResult.Danger  -> return result   // Danger tem prioridade máxima
                is SecurityResult.Warning -> {
                    // Mantém o Warning mais próximo
                    if (closestWarning == null || result.distanceMeters < closestWarning.distanceMeters) {
                        closestWarning = result
                    }
                }
                else -> Unit
            }
        }

        return closestWarning ?: SecurityResult.Safe
    }

    // ─── VERIFICADORES POR TIPO DE GEOMETRY ──────────────────────────

    // ─── Ponto ────────────────────────────────────────────────────────
    // Dentro de POINT_DANGER_RADIUS_KM (150 m) → Danger
    // Dentro de WARNING_BUFFER_KM   (500 m) → Warning
    private fun checkPoint(lat: Double, lng: Double, feature: GeoFeature): SecurityResult? {
        val distKm = haversineKm(lat, lng, feature.centerLat, feature.centerLng)
        return when {
            distKm <= POINT_DANGER_RADIUS_KM -> SecurityResult.Danger(
                featureName    = feature.name,
                areaName       = feature.area,
                distanceMeters = (distKm * 1000).toInt(),
                alertLevel     = feature.alertLevel
            )
            distKm <= WARNING_BUFFER_KM -> SecurityResult.Warning(
                featureName    = feature.name,
                areaName       = feature.area,
                distanceMeters = (distKm * 1000).toInt()
            )
            else -> null
        }
    }

    // ─── LineString ───────────────────────────────────────────────────
    // Muito perto do traçado (< 100 m) → Danger
    // Dentro de 500 m → Warning
    private fun checkLineString(lat: Double, lng: Double, feature: GeoFeature): SecurityResult? {
        return try {
            val coords = parseCoordinatesArray(feature.coordinatesJson)
            var minDistKm = Double.MAX_VALUE
            for (i in 0 until coords.size - 1) {
                val (pLat, pLng) = coords[i]
                val (qLat, qLng) = coords[i + 1]
                val d = distanceToSegmentKm(lat, lng, pLat, pLng, qLat, qLng)
                if (d < minDistKm) minDistKm = d
            }
            when {
                minDistKm <= 0.1 -> SecurityResult.Danger(      // 100 m → dentro da linha de risco
                    featureName    = feature.name,
                    areaName       = feature.area,
                    distanceMeters = (minDistKm * 1000).toInt(),
                    alertLevel     = feature.alertLevel
                )
                minDistKm <= WARNING_BUFFER_KM -> SecurityResult.Warning(
                    featureName    = feature.name,
                    areaName       = feature.area,
                    distanceMeters = (minDistKm * 1000).toInt()
                )
                else -> null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao verificar LineString ${feature.name}: ${e.message}")
            null
        }
    }

    // ─── Polígono ─────────────────────────────────────────────────────
    // Dentro do polígono → Danger (distanceMeters = 0)
    // Fora mas dentro de 500 m da borda → Warning
    private fun checkPolygon(lat: Double, lng: Double, feature: GeoFeature): SecurityResult? {
        return try {
            val ring = parseCoordinatesArray(feature.coordinatesJson)
            if (pointInPolygon(lat, lng, ring)) {
                // Ponto DENTRO da área de risco → DANGER
                return SecurityResult.Danger(
                    featureName    = feature.name,
                    areaName       = feature.area,
                    distanceMeters = 0,
                    alertLevel     = feature.alertLevel
                )
            }

            // Fora do polígono — calcula distância até a borda mais próxima
            val distToBorderKm = minimumDistanceToPolygonEdgeKm(lat, lng, ring)
            if (distToBorderKm <= WARNING_BUFFER_KM) {
                SecurityResult.Warning(
                    featureName    = feature.name,
                    areaName       = feature.area,
                    distanceMeters = (distToBorderKm * 1000).toInt()
                )
            } else null
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao verificar Polygon ${feature.name}: ${e.message}")
            null
        }
    }

    /**
     * Distância mínima de um ponto até a borda de um polígono (em KM).
     * Itera sobre todos os segmentos do anel exterior e retorna a menor distância.
     * Complexidade: O(n) — aceitável para polígonos típicos de bairros (<200 vértices).
     */
    private fun minimumDistanceToPolygonEdgeKm(
        lat: Double, lng: Double,
        ring: List<Pair<Double, Double>>
    ): Double {
        var minDist = Double.MAX_VALUE
        for (i in ring.indices) {
            val (aLat, aLng) = ring[i]
            val (bLat, bLng) = ring[(i + 1) % ring.size]
            val d = distanceToSegmentKm(lat, lng, aLat, aLng, bLat, bLng)
            if (d < minDist) minDist = d
        }
        return minDist
    }

    // ─── ALGORITMOS GEOMÉTRICOS ───────────────────────────────────────

    /**
     * Distância Haversine entre dois pontos GPS em quilômetros.
     */
    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    /**
     * Distância de um ponto a um segmento de linha (para LineStrings).
     */
    private fun distanceToSegmentKm(
        pLat: Double, pLng: Double,
        aLat: Double, aLng: Double,
        bLat: Double, bLng: Double
    ): Double {
        val ab = haversineKm(aLat, aLng, bLat, bLng)
        if (ab == 0.0) return haversineKm(pLat, pLng, aLat, aLng)

        val t = maxOf(0.0, minOf(1.0,
            ((pLat - aLat) * (bLat - aLat) + (pLng - aLng) * (bLng - aLng)) /
                    ((bLat - aLat).pow(2) + (bLng - aLng).pow(2))
        ))
        val projLat = aLat + t * (bLat - aLat)
        val projLng = aLng + t * (bLng - aLng)
        return haversineKm(pLat, pLng, projLat, projLng)
    }

    /**
     * Ray casting algorithm: verifica se ponto está dentro de polígono.
     * Algoritmo padrão para GeoJSON Polygons.
     */
    private fun pointInPolygon(lat: Double, lng: Double, ring: List<Pair<Double, Double>>): Boolean {
        var inside = false
        var j = ring.size - 1
        for (i in ring.indices) {
            val (iLat, iLng) = ring[i]
            val (jLat, jLng) = ring[j]
            if ((iLng > lng) != (jLng > lng) &&
                lat < (jLat - iLat) * (lng - iLng) / (jLng - iLng) + iLat) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    // ─── PARSER GEOJSON ───────────────────────────────────────────────

    /**
     * Converte o GeoJSON bruto em lista de GeoFeature para o Room.
     */
    fun parseGeoJson(jsonString: String): List<GeoFeature> {
        val features = mutableListOf<GeoFeature>()
        val root = JSONObject(jsonString)
        val featuresArray = root.getJSONArray("features")

        for (i in 0 until featuresArray.length()) {
            try {
                val feature = featuresArray.getJSONObject(i)
                val props = feature.optJSONObject("properties") ?: JSONObject()
                val geometry = feature.getJSONObject("geometry")
                val type = geometry.getString("type")
                val coords = geometry.getJSONArray("coordinates")

                val name = props.optString("name", "Área de Risco")
                    .ifBlank { "Área de Risco" }
                val desc = props.optString("description", "")
                val area = extractAreaName(name, desc)

                val (centerLat, centerLng, coordsJson) = when (type) {
                    "Point" -> {
                        val lng = coords.getDouble(0)
                        val lat = coords.getDouble(1)
                        Triple(lat, lng, "[[${lng},${lat}]]")
                    }
                    "LineString" -> {
                        val center = calculateCentroid(coords)
                        Triple(center.first, center.second, coords.toString())
                    }
                    "Polygon" -> {
                        val ring = coords.getJSONArray(0)
                        val center = calculateCentroid(ring)
                        Triple(center.first, center.second, ring.toString())
                    }
                    else -> continue
                }

                features.add(GeoFeature(
                    name = name,
                    description = desc,
                    area = area,
                    geometryType = type,
                    coordinatesJson = coordsJson,
                    centerLat = centerLat,
                    centerLng = centerLng,
                    alertLevel = if (isCriticalZone(name)) AlertLevel.DANGER else AlertLevel.CAUTION
                ))
            } catch (e: Exception) {
                Log.w(TAG, "Feature $i ignorada: ${e.message}")
            }
        }
        return features
    }

    private fun calculateCentroid(coordsArray: JSONArray): Pair<Double, Double> {
        var sumLat = 0.0; var sumLng = 0.0; var count = 0
        for (i in 0 until coordsArray.length()) {
            try {
                val pt = coordsArray.getJSONArray(i)
                sumLng += pt.getDouble(0)
                sumLat += pt.getDouble(1)
                count++
            } catch (_: Exception) {}
        }
        return if (count > 0) Pair(sumLat / count, sumLng / count) else Pair(0.0, 0.0)
    }

    private fun parseCoordinatesArray(json: String): List<Pair<Double, Double>> {
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val pt = arr.getJSONArray(i)
            Pair(pt.getDouble(1), pt.getDouble(0)) // GeoJSON é [lng, lat]
        }
    }

    // Palavras-chave críticas do documento técnico
    private val CRITICAL_KEYWORDS = listOf(
        "chapadão", "vila ruth", "morro do amor", "nova holanda", "rubens vaz",
        "bom tempo", "suvaquinho", "castelar", "morro do rola", "barreira do vasco",
        "morro da palmeira", "vila rica", "jacarezinho", "complexo", "maré",
        "alemão", "rocinha", "vidigal", "dendê", "mandela"
    )

    private fun isCriticalZone(name: String): Boolean {
        val lower = name.lowercase()
        return CRITICAL_KEYWORDS.any { lower.contains(it) }
    }

    private fun extractAreaName(name: String, desc: String): String {
        val combined = "$name $desc".lowercase()
        return when {
            combined.contains("maré") -> "Complexo da Maré"
            combined.contains("chapadão") || combined.contains("acari") -> "Complexo do Chapadão"
            combined.contains("alemão") -> "Complexo do Alemão"
            combined.contains("mali") || combined.contains("vila ruth") -> "Complexo de Mali"
            combined.contains("baixada") || combined.contains("castelar") -> "Baixada Fluminense"
            combined.contains("santa cruz") || combined.contains("morro do rola") -> "Santa Cruz"
            combined.contains("campo grande") -> "Campo Grande"
            else -> ""
        }
    }

    fun isCacheLoaded() = cacheLoaded
    fun getCachedFeatureCount() = featuresCache.size
}
