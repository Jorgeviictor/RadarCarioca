package com.radarcarioca.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
// ESSES IMPORTS SÃO ESSENCIAIS PARA AS EXTENSÕES FUNCIONAREM:
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.radarcarioca.data.model.OverlayStatus
import com.radarcarioca.data.model.RideAnalysis
import com.radarcarioca.ui.screens.OverlayCard
import com.radarcarioca.ui.theme.RadarCariocaTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "OverlayManager"

@Singleton
class OverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    var currentAnalysis: RideAnalysis? = null
        private set

    private var onAcceptCallback: (() -> Unit)? = null
    private var onRejectCallback: (() -> Unit)? = null

    fun setDecisionCallbacks(onAccept: () -> Unit, onReject: () -> Unit) {
        onAcceptCallback = onAccept
        onRejectCallback = onReject
    }

    fun showAnalysis(analysis: RideAnalysis) {
        if (!Settings.canDrawOverlays(context)) {
            Log.w(TAG, "Permissão de overlay não concedida!")
            return
        }

        hideOverlay()
        currentAnalysis = analysis

        if (analysis.overlayStatus == OverlayStatus.RED) {
            vibrateDanger()
        }

        try {
            val composeView = createComposeOverlay(analysis)
            val params = buildWindowParams()
            windowManager.addView(composeView, params)
            overlayView = composeView
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao exibir overlay: ${e.message}")
        }
    }

    fun hideOverlay() {
        overlayView?.let { view ->
            try {
                // Notifica o ciclo de vida que a view será destruída
                (view as? ComposeView)?.let {
                    it.setViewTreeLifecycleOwner(null)
                }
                windowManager.removeView(view)
            } catch (_: Exception) {}
        }
        overlayView = null
        currentAnalysis = null
    }

    private fun createComposeOverlay(analysis: RideAnalysis): ComposeView {
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            // 1. Configura os Owners ANTES do setContent
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            })
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            // 2. Define o conteúdo
            setContent {
                RadarCariocaTheme {
                    OverlayCard(
                        analysis = analysis,
                        onAccept = {
                            onAcceptCallback?.invoke()
                            hideOverlay()
                        },
                        onReject = {
                            onRejectCallback?.invoke()
                            hideOverlay()
                        },
                        onDismiss = { hideOverlay() }
                    )
                }
            }
        }

        // Inicia o ciclo de vida para o Compose começar a desenhar
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        return composeView
    }

    private fun buildWindowParams(): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP // Geralmente alertas ficam melhor no topo ou centro
        }
    }

    private fun vibrateDanger() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300, 200, 500), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 300, 200, 300, 200, 500), -1)
            }
        } catch (_: Exception) {}
    }
}

// Classe auxiliar para gerenciar o estado e ciclo de vida no Overlay
class MyLifecycleOwner : SavedStateRegistryOwner {
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var savedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: android.os.Bundle?) {
        savedStateRegistryController.performRestore(savedState)
    }
}