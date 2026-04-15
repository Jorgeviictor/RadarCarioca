package com.radarcarioca.overlay

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.radarcarioca.MainActivity
import com.radarcarioca.data.local.GeoFeatureDao
import com.radarcarioca.data.local.RideHistoryDao
import com.radarcarioca.data.mapper.toDomain
import com.radarcarioca.data.model.GeoFeature
import com.radarcarioca.data.model.RideRecord
import kotlinx.coroutines.flow.map
import com.radarcarioca.ui.theme.RadarCariocaTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FloatingButtonManager"

const val NAV_EXTRA     = "navigate_to"
const val NAV_MAP       = "map"
const val NAV_SETTINGS  = "settings"
const val NAV_HISTORY   = "history"

@Singleton
class FloatingButtonManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rideHistoryDao: RideHistoryDao,
    private val geoFeatureDao: GeoFeatureDao
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var floatingView: View? = null
    private val windowParams: WindowManager.LayoutParams = buildLayoutParams()

    private var posX = 0
    private var posY = 200

    fun show() {
        if (floatingView != null) return
        if (!Settings.canDrawOverlays(context)) {
            Log.w(TAG, "Permissão SYSTEM_ALERT_WINDOW não concedida — botão flutuante não será exibido")
            return
        }
        try {
            val view = createComposeView()
            windowParams.x = posX
            windowParams.y = posY
            windowManager.addView(view, windowParams)
            floatingView = view
            Log.i(TAG, "Botão flutuante exibido")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao exibir botão flutuante: ${e.message}")
        }
    }

    fun hide() {
        floatingView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        floatingView = null
    }

    fun isShowing() = floatingView != null

    private fun createComposeView(): ComposeView {
        val lifecycleOwner = FloatingLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        return ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            })
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            setContent {
                RadarCariocaTheme {
                    SpeedDialButton(
                        onDrag = { dx, dy ->
                            posX += dx.toInt()
                            posY += dy.toInt()
                            windowParams.x = posX
                            windowParams.y = posY
                            try { windowManager.updateViewLayout(this@apply, windowParams) } catch (_: Exception) {}
                        },
                        onNavigate = { dest -> openMainActivity(dest) },
                        rideHistoryDao = rideHistoryDao,
                        geoFeatureDao = geoFeatureDao
                    )
                }
            }
        }.also {
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    private fun openMainActivity(destination: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NAV_EXTRA, destination)
        }
        context.startActivity(intent)
    }

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        }
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = posX
            y = posY
        }
    }
}

// ─── Cores ────────────────────────────────────────────────────────────────────

private val GoldColor  = Color(0xFFD4AF37)
private val GoldDim    = Color(0xFF9A7D1A)
private val BlackBg    = Color(0xFF000000)
private val CardBg     = Color(0xE8060D1F)   // glassmorphism idêntico ao OverlayCard

// ─── SpeedDialButton ─────────────────────────────────────────────────────────

@Composable
private fun SpeedDialButton(
    onDrag: (Float, Float) -> Unit,
    onNavigate: (String) -> Unit,
    rideHistoryDao: RideHistoryDao,
    geoFeatureDao: GeoFeatureDao
) {
    var expanded by remember { mutableStateOf(false) }

    // Zonas de perigo — carregadas uma vez na primeira abertura do card
    var geoFeatures by remember { mutableStateOf<List<GeoFeature>>(emptyList()) }
    LaunchedEffect(expanded) {
        if (expanded && geoFeatures.isEmpty()) {
            geoFeatures = geoFeatureDao.getAllFeatures().map { it.toDomain() }
        }
    }

    // Últimas corridas — stream do banco, mapeado para domain entity
    val recentRidesFlow = remember { rideHistoryDao.getRecentRides().map { list -> list.map { it.toDomain() } } }
    val recentRides by recentRidesFlow.collectAsState(initial = emptyList())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        // Mini-card aparece ACIMA do botão RC
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit  = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            MiniDashboardCard(
                recentRides = recentRides.take(3),
                geoFeatures = geoFeatures,
                onNavigate  = { dest -> expanded = false; onNavigate(dest) },
                onClose     = { expanded = false }
            )
        }

        // Botão RC (arrastável)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A1A), BlackBg)
                    )
                )
                .border(2.dp, if (expanded) GoldColor else GoldDim, CircleShape)
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    detectDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDrag += kotlin.math.abs(dragAmount.x) + kotlin.math.abs(dragAmount.y)
                            onDrag(dragAmount.x, dragAmount.y)
                        },
                        onDragEnd = {
                            if (totalDrag < 10f) expanded = !expanded
                        }
                    )
                }
        ) {
            Text(
                text          = if (expanded) "✕" else "RC",
                color         = GoldColor,
                fontSize      = if (expanded) 18.sp else 16.sp,
                fontWeight    = FontWeight.Black,
                textAlign     = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─── Mini Dashboard Card ─────────────────────────────────────────────────────

@Composable
private fun MiniDashboardCard(
    recentRides: List<RideRecord>,
    geoFeatures: List<GeoFeature>,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(284.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, GoldDim, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Cabeçalho: logo + ícone de configurações ──────────────────────
        Row(
            modifier                = Modifier.fillMaxWidth(),
            horizontalArrangement  = Arrangement.SpaceBetween,
            verticalAlignment      = Alignment.CenterVertically
        ) {
            Text(
                "🗺  RADAR CARIOCA",
                color         = GoldColor,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            // Ícone ⚙ configurações
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(GoldDim.copy(alpha = 0.18f))
                    .border(1.dp, GoldDim, CircleShape)
                    .clickable { onNavigate(NAV_SETTINGS) },
                contentAlignment = Alignment.Center
            ) {
                Text("⚙", fontSize = 15.sp)
            }
        }

        // ── Mini Mapa com zonas de risco ──────────────────────────────────
        MiniMapSection(geoFeatures = geoFeatures)

        // ── Últimas corridas ──────────────────────────────────────────────
        Text(
            "ÚLTIMAS CORRIDAS",
            color         = GoldColor.copy(alpha = 0.65f),
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        if (recentRides.isNotEmpty()) {
            recentRides.forEach { ride -> RideSummaryRow(ride = ride) }
        } else {
            Box(
                modifier         = Modifier.fillMaxWidth().height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhuma corrida registrada ainda",
                    color    = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp
                )
            }
        }

        // ── Botões de navegação ───────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniNavButton("🗺  Mapa",     Modifier.weight(1f)) { onNavigate(NAV_MAP) }
            MiniNavButton("📋  Histórico", Modifier.weight(1f)) { onNavigate(NAV_HISTORY) }
        }
    }
}

// ─── Mini Mapa ───────────────────────────────────────────────────────────────

@Composable
private fun MiniMapSection(geoFeatures: List<GeoFeature>) {
    val ctx           = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapInitialized by remember { mutableStateOf(false) }

    val mapView = remember { MapView(ctx) }

    DisposableEffect(lifecycleOwner) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner)   = mapView.onPause()
            override fun onStop(owner: LifecycleOwner)    = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try { mapView.onPause(); mapView.onStop(); mapView.onDestroy() } catch (_: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0A1628))
    ) {
        AndroidView(
            factory = { mapView },
            update  = { mv ->
                if (geoFeatures.isNotEmpty() && !mapInitialized) {
                    mapInitialized = true
                    mv.getMapAsync { map ->
                        map.uiSettings.setAllGesturesEnabled(false)
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(-22.9068, -43.1729), 10f)
                        )
                        // Círculos vermelhos em cada zona de risco
                        geoFeatures
                            .filter { it.centerLat != 0.0 && it.centerLng != 0.0 }
                            .take(60)
                            .forEach { feature ->
                                map.addCircle(
                                    CircleOptions()
                                        .center(LatLng(feature.centerLat, feature.centerLng))
                                        .radius(feature.bufferKm * 1000.0)
                                        .fillColor(android.graphics.Color.argb(50,  255, 23, 68))
                                        .strokeColor(android.graphics.Color.argb(200, 255, 23, 68))
                                        .strokeWidth(1.5f)
                                )
                            }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay "Carregando..." quando ainda não temos features
        if (geoFeatures.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Carregando mapa…",
                    color    = GoldColor.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }

        // Rótulo no canto inferior esquerdo
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BlackBg.copy(alpha = 0.72f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                "Zonas de risco — RJ",
                color    = Color.White.copy(alpha = 0.65f),
                fontSize = 9.sp
            )
        }
    }
}

// ─── Linha de Resumo da Corrida ───────────────────────────────────────────────

@Composable
private fun RideSummaryRow(ride: RideRecord) {
    val profitColor = when {
        ride.hadSecurityAlert   -> Color(0xFFD500F9)  // roxo — alerta de risco
        ride.profitPerKm >= 2.5 -> Color(0xFF00E676)  // verde — excelente
        ride.profitPerKm >= 1.8 -> Color(0xFFFFD600)  // amarelo — limítrofe
        else                    -> Color(0xFFFF1744)  // vermelho — abaixo da meta
    }
    val statusColor = if (ride.wasAccepted) Color(0xFF00E676) else Color(0xFFFF1744)

    // Valores estimados a partir de R$/km (velocidade média urbana: 25 km/h)
    val perHour = ride.profitPerKm * 25.0
    val perMin  = perHour / 60.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment      = Alignment.CenterVertically,
        horizontalArrangement  = Arrangement.spacedBy(6.dp)
    ) {
        // Status aceita/recusada
        Text(
            if (ride.wasAccepted) "✓" else "✗",
            color      = statusColor,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold
        )

        // Destino
        Text(
            ride.destinationText,
            color    = Color.White.copy(alpha = 0.75f),
            fontSize = 10.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Métricas: R$/km + R$/h + R$/min
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "R$ %.2f/km".format(ride.profitPerKm),
                color      = profitColor,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "R$%.0f/h".format(perHour),
                    color    = Color.White.copy(alpha = 0.45f),
                    fontSize = 9.sp
                )
                Text(
                    "R$%.2f/min".format(perMin),
                    color    = Color.White.copy(alpha = 0.45f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

// ─── Botão de Navegação Mini ──────────────────────────────────────────────────

@Composable
private fun MiniNavButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GoldDim.copy(alpha = 0.14f))
            .border(1.dp, GoldDim.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color      = GoldColor,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center
        )
    }
}

// ─── LifecycleOwner para o botão flutuante ────────────────────────────────────

private class FloatingLifecycleOwner : SavedStateRegistryOwner {
    private val lifecycleRegistry            = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)

    fun performRestore(savedState: android.os.Bundle?) =
        savedStateRegistryController.performRestore(savedState)
}
