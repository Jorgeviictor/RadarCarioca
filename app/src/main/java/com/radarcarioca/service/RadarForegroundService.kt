package com.radarcarioca.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.radarcarioca.MainActivity
import com.radarcarioca.R
import com.radarcarioca.core.DataResult
import com.radarcarioca.data.local.DriverPreferences
import com.radarcarioca.domain.usecase.ProcessRideOfferUseCase
import com.radarcarioca.domain.usecase.RecordRideDecisionUseCase
import com.radarcarioca.geo.GeoSecurityManager
import com.radarcarioca.overlay.FloatingButtonManager
import com.radarcarioca.overlay.OverlayManager
import com.radarcarioca.util.ScreenshotPurgeManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "RadarForegroundService"
const val NOTIF_ID = 1001

/**
 * ═══════════════════════════════════════════════════════════════════
 * FOREGROUND SERVICE — O Watcher Ininterrupto
 * ═══════════════════════════════════════════════════════════════════
 *
 * Garante que o Radar não seja encerrado pelo Android durante o turno.
 * START_STICKY: reinicia automaticamente se o processo for morto.
 *
 * Responsabilidades (apenas orquestração — sem lógica de negócio):
 * 1. Inicializar o GeoSecurityManager (carregar GeoJSON)
 * 2. Escutar o SharedFlow do AccessibilityService
 * 3. Delegar análise de cada oferta ao [ProcessRideOfferUseCase]
 * 4. Exibir resultado via [OverlayManager]
 * 5. Delegar registro de decisões ao [RecordRideDecisionUseCase]
 */
@AndroidEntryPoint
class RadarForegroundService : Service() {

    @Inject lateinit var processRideOfferUseCase: ProcessRideOfferUseCase
    @Inject lateinit var recordRideDecisionUseCase: RecordRideDecisionUseCase
    @Inject lateinit var geoSecurityManager: GeoSecurityManager
    @Inject lateinit var overlayManager: OverlayManager
    @Inject lateinit var floatingButtonManager: FloatingButtonManager
    @Inject lateinit var screenshotPurgeManager: ScreenshotPurgeManager
    @Inject lateinit var driverPreferences: DriverPreferences

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        var isRunning = false

        fun start(context: Context) {
            val intent = Intent(context, RadarForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, RadarForegroundService::class.java))
        }
    }

    // ─── CICLO DE VIDA ────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Foreground Service criado")
        startForeground(NOTIF_ID, buildNotification())
        initializeGeoEngine()
        startListeningForOffers()
        floatingButtonManager.show()
        runScreenshotPurge()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        Log.i(TAG, "Radar iniciado — Watcher ativo")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
        overlayManager.hideOverlay()
        floatingButtonManager.hide()
        Log.i(TAG, "Foreground Service encerrado")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ─── INICIALIZAÇÃO DO GEO ENGINE ──────────────────────────────────

    private fun initializeGeoEngine() {
        serviceScope.launch(Dispatchers.IO) {
            try {
                geoSecurityManager.initialize()
                val count = geoSecurityManager.getCachedFeatureCount()
                Log.i(TAG, "GeoJSON carregado: $count features")
                updateNotification("Radar ativo — $count zonas mapeadas")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inicializar GeoEngine: ${e.message}")
            }
        }
    }

    // ─── LISTENER DE OFERTAS ──────────────────────────────────────────

    /**
     * Escuta o SharedFlow do AccessibilityService.
     * Cada nova oferta delega a análise completa ao [ProcessRideOfferUseCase].
     * [collectLatest] garante que uma nova oferta cancela a análise da anterior
     * — evita exibir resultado de corrida já passada.
     */
    private fun startListeningForOffers() {
        serviceScope.launch {
            RadarAccessibilityService.offerFlow.collectLatest { offer ->
                Log.i(TAG, "Nova oferta recebida: ${offer.destinationText}")
                when (val result = processRideOfferUseCase(offer)) {
                    is DataResult.Success -> withContext(Dispatchers.Main) {
                        overlayManager.showAnalysis(result.data)
                    }
                    is DataResult.Error -> Log.e(
                        TAG,
                        "Erro ao processar oferta '${offer.destinationText}': ${result.exception.message}"
                    )
                    is DataResult.Loading -> Unit // não esperado neste contexto
                }
            }
        }
    }

    /**
     * Chamado pela UI quando o motorista aceita ou recusa a corrida.
     * A lógica de construção do [RideRecord] foi movida para [RecordRideDecisionUseCase].
     */
    fun recordDecision(wasAccepted: Boolean) {
        val analysis = overlayManager.currentAnalysis ?: return
        serviceScope.launch(Dispatchers.IO) {
            val result = recordRideDecisionUseCase(analysis, wasAccepted)
            if (result is DataResult.Error) {
                Log.e(TAG, "Erro ao registrar decisão: ${result.exception.message}")
            }
        }
        overlayManager.hideOverlay()
    }

    // ─── NOTIFICAÇÃO PERSISTENTE ──────────────────────────────────────

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        getSystemService(NotificationManager::class.java).notify(NOTIF_ID, notification)
    }

    private fun runScreenshotPurge() {
        serviceScope.launch(Dispatchers.IO) {
            try {
                val retentionDays = driverPreferences.driverConfig.first().screenshotRetentionDays
                val removed = screenshotPurgeManager.purge(retentionDays)
                if (removed > 0) Log.i(TAG, "Auto-purge: $removed print(s) removido(s)")
            } catch (e: Exception) {
                Log.e(TAG, "Erro no auto-purge: ${e.message}")
            }
        }
    }
}
