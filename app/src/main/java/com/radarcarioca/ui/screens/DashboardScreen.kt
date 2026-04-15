package com.radarcarioca.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.ui.DashboardUiState
import com.radarcarioca.ui.theme.RadarCariocaTheme
import com.radarcarioca.ui.theme.RadarColors

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onToggleRadar: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateStats: () -> Unit,
    onNavigateAdminPanel: () -> Unit = {},
    onRefreshPermissions: () -> Unit,
    onPermissionErrorConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Consome o evento de erro de permissão exibindo um Snackbar
    LaunchedEffect(uiState.permissionErrorEvent) {
        if (uiState.permissionErrorEvent) {
            snackbarHostState.showSnackbar(
                message = "Ative Acessibilidade e Overlay antes de ligar o Radar",
                duration = SnackbarDuration.Short
            )
            onPermissionErrorConsumed()
        }
    }

    // gridColor é estável — calculado uma única vez, não causa recomposição
    val gridColor = remember { RadarColors.Gold.copy(alpha = 0.04f) }

    Scaffold(
        containerColor = RadarColors.NavyDeep,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = RadarColors.DangerRedGlow.copy(alpha = 0.92f),
                    contentColor = RadarColors.TextPrimary
                )
            }
        }
    ) { innerPadding ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(RadarColors.NavyDeep)
            // drawBehind executa apenas na fase de desenho, sem nó de composição extra,
            // evitando recomposições desnecessárias quando uiState muda.
            .drawBehind {
                val gridSize = 40.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += gridSize
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += gridSize
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(isActive = uiState.isRadarActive)
            GeoJsonBadge(featuresCount = uiState.geoFeaturesLoaded)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                RadarActivationButton(
                    isActive = uiState.isRadarActive,
                    onClick = onToggleRadar,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                TurnStatsRow(uiState = uiState)
                ServicesStatusCard(
                    uiState = uiState,
                    onRefresh = onRefreshPermissions,
                    onOpenAccessibility = {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    onOpenOverlay = {
                        context.startActivity(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).also {
                                it.data = android.net.Uri.parse("package:${context.packageName}")
                            }
                        )
                    },
                    onOpenBattery = {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).also {
                                it.data = android.net.Uri.parse("package:${context.packageName}")
                            }
                        )
                    }
                )
                QuickConfigBar(config = uiState.config)
                Spacer(Modifier.height(80.dp))
            }
        }

        BottomNavBar(
            onDashboard = {},
            onStats = onNavigateStats,
            onSettings = onNavigateSettings,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // FAB exclusivo do Admin Master
        if (uiState.userRole == com.radarcarioca.domain.model.UserRole.ADMIN_MASTER) {
            androidx.compose.material3.FloatingActionButton(
                onClick = onNavigateAdminPanel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 72.dp, end = 16.dp),
                containerColor = com.radarcarioca.ui.theme.RadarColors.Gold.copy(alpha = 0.15f),
                contentColor = com.radarcarioca.ui.theme.RadarColors.Gold
            ) {
                Text("A", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            }
        }
    }
    } // fim Scaffold
}

@Composable
private fun AppHeader(isActive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.NavyDeep.copy(alpha = 0.95f))
            .border(BorderStroke(Dp.Hairline, RadarColors.Gold.copy(alpha = 0.2f)), RoundedCornerShape(0.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("◈", fontSize = 26.sp, color = RadarColors.Gold)
            Column {
                Text("RADAR CARIOCA", style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp), color = RadarColors.Gold)
                Text("Copiloto Inteligente • Rio de Janeiro", style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val dotColor = if (isActive) RadarColors.SafeGreenLight else RadarColors.TextMuted
            val infiniteTransition = rememberInfiniteTransition(label = "headerDot")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                label = "glowAlpha"
            )
            Box(modifier = Modifier.size(9.dp).background(
                if (isActive) dotColor.copy(alpha = glowAlpha) else dotColor, CircleShape))
            Text(
                if (isActive) "ATIVO" else "INATIVO",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isActive) RadarColors.SafeGreenGlow else RadarColors.TextMuted
            )
        }
    }
}

@Composable
private fun GeoJsonBadge(featuresCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.SafeGreen.copy(alpha = 0.12f))
            .border(BorderStroke(Dp.Hairline, RadarColors.SafeGreen.copy(alpha = 0.3f)), RoundedCornerShape(0.dp))
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("●", fontSize = 7.sp, color = RadarColors.SafeGreenLight)
        Text(
            text = if (featuresCount > 0)
                "$featuresCount features mapeadas • GeoJSON Rio 2026 • Offline ✓"
            else
                "GeoJSON carregando... • coloque mapa_faccoes_rj.geojson em assets/",
            style = MaterialTheme.typography.labelSmall,
            color = RadarColors.SafeGreenGlow
        )
    }
}

@Composable
private fun RadarActivationButton(isActive: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "rotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isActive) {
            Box(modifier = Modifier.size(185.dp).rotate(rotation).border(1.dp, RadarColors.Gold.copy(alpha = 0.3f), CircleShape))
        }
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(if (isActive) RadarColors.NavyMid else RadarColors.NavyDeep)
                .border(2.dp, if (isActive) RadarColors.Gold else RadarColors.GoldDim, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (isActive) "⏹" else "▶", fontSize = 38.sp, color = RadarColors.Gold)
                Text(
                    if (isActive) "DESATIVAR" else "ATIVAR RADAR",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.1.sp),
                    color = RadarColors.Gold
                )
            }
        }
    }
}

@Composable
private fun TurnStatsRow(uiState: DashboardUiState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            Triple("✓", "R$ %.2f".format(uiState.todayEarnings), "GANHO"),
            Triple("✓", "${uiState.todayAccepted}", "ACEITAS"),
            Triple("✗", "${uiState.todayRejected}", "RECUSADAS"),
            Triple("⚠", "${uiState.todayAlerts}", "ALERTAS"),
        ).forEachIndexed { i, (icon, value, label) ->
            val color = when (i) {
                0 -> RadarColors.Gold
                1 -> RadarColors.SafeGreenGlow
                2 -> RadarColors.DangerRedGlow
                else -> RadarColors.RiskPurpleGlow
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(RadarColors.GlassWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(icon, fontSize = 14.sp, color = color)
                Text(value, style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold), color = color)
                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = RadarColors.TextMuted)
            }
        }
    }
}

@Composable
fun ServicesStatusCard(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onOpenBattery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.GlassWhite, RoundedCornerShape(12.dp))
            .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("SERVIÇOS DO SISTEMA", style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
            TextButton(onClick = onRefresh, contentPadding = PaddingValues(0.dp)) {
                Text("↻ Atualizar", style = MaterialTheme.typography.labelSmall, color = RadarColors.Gold, fontSize = 10.sp)
            }
        }
        ServiceItem("👁", "Accessibility Service", "Leitura automática Uber + 99", uiState.isAccessibilityEnabled, if (!uiState.isAccessibilityEnabled) "Ativar" else null, onOpenAccessibility)
        ServiceItem("🪟", "System Alert Window", "Overlay flutuante sobre apps", uiState.isOverlayEnabled, if (!uiState.isOverlayEnabled) "Permitir" else null, onOpenOverlay)
        ServiceItem("⚙", "Foreground Service", "Watcher ativo em background", uiState.isRadarActive, null) {}
        ServiceItem("🗺", "GeoJSON Engine",
            if (uiState.geoFeaturesLoaded > 0) "${uiState.geoFeaturesLoaded} zonas carregadas • Offline ✓" else "Aguardando arquivo GeoJSON",
            uiState.geoFeaturesLoaded > 0, null) {}
    }
}

@Composable
private fun ServiceItem(icon: String, name: String, desc: String, isActive: Boolean, actionLabel: String?, onAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.NavyDeep.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(icon, fontSize = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp), color = RadarColors.TextPrimary)
            Text(desc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RadarColors.TextMuted)
        }
        if (actionLabel != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Text(actionLabel, fontSize = 10.sp, color = RadarColors.Gold, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(modifier = Modifier.size(8.dp).background(
                if (isActive) RadarColors.SafeGreenLight else RadarColors.SurfaceVariant, CircleShape))
        }
    }
}

@Composable
private fun QuickConfigBar(config: DriverConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.GlassWhite, RoundedCornerShape(10.dp))
            .border(1.dp, RadarColors.Gold.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ConfigItem("META R$/KM", "R$ %.2f".format(config.targetProfitPerKm))
        VerticalDivider(modifier = Modifier.height(28.dp).width(1.dp), color = RadarColors.GlassBorder)
        ConfigItem("COMBUSTÍVEL", "R$ %.2f/L".format(config.fuelPricePerLiter))
        VerticalDivider(modifier = Modifier.height(28.dp).width(1.dp), color = RadarColors.GlassBorder)
        ConfigItem("CONSUMO", "${config.kmPerLiter} KM/L")
    }
}

@Composable
private fun ConfigItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = RadarColors.TextMuted)
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = RadarColors.Gold)
    }
}

@Composable
fun BottomNavBar(onDashboard: () -> Unit, onStats: () -> Unit, onSettings: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(RadarColors.NavyDeep.copy(alpha = 0.97f))
            .border(BorderStroke(Dp.Hairline, RadarColors.Gold.copy(alpha = 0.2f)), RoundedCornerShape(0.dp))
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf(Triple("◈", "Radar", onDashboard), Triple("📊", "Histórico", onStats), Triple("⚙", "Config", onSettings))
            .forEach { (icon, label, action) ->
                Column(
                    modifier = Modifier.clickable { action() }.padding(vertical = 12.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(icon, fontSize = 20.sp, color = RadarColors.Gold)
                    Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RadarColors.TextMuted)
                }
            }
    }
}

// ─── Previews ────────────────────────────────────────────────────────────────

private val previewUiStateActive = DashboardUiState(
    isRadarActive = true,
    isAccessibilityEnabled = true,
    isOverlayEnabled = true,
    geoFeaturesLoaded = 347,
    isOnboardingDone = true,
    config = DriverConfig(),
    todayEarnings = 187.50,
    todayAccepted = 12,
    todayRejected = 3,
    todayAlerts = 1,
    hasAccess = true
)

private val previewUiStateInactive = DashboardUiState(
    isRadarActive = false,
    isAccessibilityEnabled = false,
    isOverlayEnabled = false,
    geoFeaturesLoaded = 0
)

@Preview(name = "Dashboard — Radar Ativo", showBackground = true)
@Composable
private fun DashboardActivePreview() {
    RadarCariocaTheme {
        DashboardScreen(
            uiState = previewUiStateActive,
            onToggleRadar = {}, onNavigateSettings = {},
            onNavigateStats = {}, onRefreshPermissions = {}
        )
    }
}

@Preview(name = "Dashboard — Radar Inativo / Setup", showBackground = true)
@Composable
private fun DashboardInactivePreview() {
    RadarCariocaTheme {
        DashboardScreen(
            uiState = previewUiStateInactive,
            onToggleRadar = {}, onNavigateSettings = {},
            onNavigateStats = {}, onRefreshPermissions = {}
        )
    }
}