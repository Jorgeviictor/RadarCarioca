package com.radarcarioca.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.model.FuelType
import com.radarcarioca.ui.DashboardUiState
import com.radarcarioca.ui.theme.RadarCariocaTheme
import com.radarcarioca.ui.theme.RadarColors
import com.radarcarioca.util.ScreenshotPurgeManager
import java.util.Calendar

// ═══════════════════════════════════════════════════════════════════
// ONBOARDING SCREEN
// ═══════════════════════════════════════════════════════════════════

@Composable
fun OnboardingScreen(
    config: DriverConfig,
    onConfigChange: (DriverConfig) -> Unit,
    onAccessibilityClick: () -> Unit,
    onOverlayClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onComplete: () -> Unit,
    isAccessibilityEnabled: Boolean,
    isOverlayEnabled: Boolean,
    isBatteryOptimizationIgnored: Boolean = false
) {
    var step by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardStep(
            icon = "👁", title = "Serviço de Acessibilidade",
            desc = "Permite ler automaticamente as ofertas da Uber e 99 sem digitar nada.\n\nO Radar lerá apenas o destino e o valor — nenhum dado pessoal de passageiros é acessado.",
            isDone = isAccessibilityEnabled,
            actionLabel = "Abrir Configurações de Acessibilidade"
        ),
        OnboardStep(
            icon = "🪟", title = "Janela Flutuante (Overlay)",
            desc = "Exibe o semáforo de decisão sobre o app da Uber ou 99 enquanto você dirige.",
            isDone = isOverlayEnabled,
            actionLabel = "Permitir Aparecer sobre Outros Apps"
        ),
        OnboardStep(
            icon = "🔋", title = "Otimização de Bateria",
            desc = "Adiciona o Radar à lista de exceções de otimização de bateria, garantindo que ele não seja encerrado durante o turno.",
            isDone = isBatteryOptimizationIgnored,
            actionLabel = "Adicionar Exceção de Bateria"
        ),
        OnboardStep(
            icon = "⚙", title = "Seus Custos",
            desc = "Configure uma vez. O Radar calcula o lucro real de cada corrida automaticamente.",
            isDone = true,
            actionLabel = null
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(RadarColors.GlassWhite, RoundedCornerShape(20.dp))
                .border(1.dp, RadarColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Indicador de progresso
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                steps.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(if (i == step) 24.dp else 7.dp)
                            .background(
                                if (i <= step) RadarColors.Gold else RadarColors.SurfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Text(steps[step].icon, fontSize = 48.sp)
            Text(steps[step].title, style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 20.sp), color = RadarColors.Gold, fontWeight = FontWeight.Bold)
            Text(steps[step].desc, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                color = RadarColors.TextSecondary, lineHeight = 20.sp)

            // Config form no último passo
            if (step == 3) {
                ConfigForm(config = config, onConfigChange = onConfigChange)
            } else if (steps[step].isDone) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RadarColors.SafeGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, RadarColors.SafeGreen, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("✓ Permissão concedida", color = RadarColors.SafeGreenGlow,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                Button(
                    onClick = when (step) {
                        0 -> onAccessibilityClick
                        1 -> onOverlayClick
                        2 -> onBatteryClick
                        else -> ({})
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RadarColors.NavyMid),
                    border = BorderStroke(1.dp, RadarColors.GoldDim)
                ) {
                    Text(steps[step].actionLabel ?: "", color = RadarColors.Gold,
                        fontWeight = FontWeight.Bold, letterSpacing = 0.05.sp)
                }
            }

            Button(
                onClick = { if (step < steps.lastIndex) step++ else onComplete() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RadarColors.Gold.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, RadarColors.Gold)
            ) {
                Text(
                    if (step < steps.lastIndex) "PRÓXIMO →" else "INICIAR RADAR ▶",
                    color = RadarColors.Gold, fontWeight = FontWeight.Bold, letterSpacing = 0.1.sp
                )
            }
        }
    }
}

private data class OnboardStep(
    val icon: String, val title: String, val desc: String,
    val isDone: Boolean, val actionLabel: String?
)

// ═══════════════════════════════════════════════════════════════════
// SETTINGS SCREEN
// ═══════════════════════════════════════════════════════════════════

@Composable
fun SettingsScreen(
    config: DriverConfig,
    onConfigChange: (DriverConfig) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RadarColors.NavyDeep)
                .border(BorderStroke(Dp.Hairline, RadarColors.Gold.copy(alpha = 0.2f)),
                    RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Voltar", color = RadarColors.Gold, fontWeight = FontWeight.Bold)
            }
            Text("CONFIGURAÇÕES", style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
                color = RadarColors.Gold)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConfigForm(config = config, onConfigChange = onConfigChange, expanded = true)
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
fun ConfigForm(
    config: DriverConfig,
    onConfigChange: (DriverConfig) -> Unit,
    expanded: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (expanded) {
            Text("PARÂMETROS FINANCEIROS",
                style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
        }

        ConfigNumberField("Preço do Combustível (R$/L)", config.fuelPricePerLiter) {
            onConfigChange(config.copy(fuelPricePerLiter = it))
        }
        ConfigNumberField("Consumo do Veículo (KM/L)", config.kmPerLiter) {
            onConfigChange(config.copy(kmPerLiter = it))
        }

        if (expanded) {
            Text("BENCHMARKS DO SINALEIRO",
                style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
        }
        ConfigNumberField("Benchmark mínimo R$/KM", config.targetProfitPerKm) {
            onConfigChange(config.copy(targetProfitPerKm = it))
        }
        ConfigNumberField("Benchmark R$/Minuto", config.targetProfitPerMinute) {
            onConfigChange(config.copy(targetProfitPerMinute = it))
        }
        ConfigNumberField("Benchmark R$/Hora", config.targetProfitPerHour) {
            onConfigChange(config.copy(targetProfitPerHour = it))
        }

        if (expanded) {
            Text("CUSTOS DA PLATAFORMA",
                style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
        }
        ConfigNumberField("Taxa da Plataforma (ex: 0.20 = 20%)", config.platformFeePercent) {
            onConfigChange(config.copy(platformFeePercent = it))
        }

        if (expanded) {
            Text("COMBUSTÍVEL", style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FuelType.values().forEach { ft ->
                    FilterChip(
                        selected = config.fuelType == ft,
                        onClick = { onConfigChange(config.copy(fuelType = ft)) },
                        label = { Text(ft.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RadarColors.Gold.copy(alpha = 0.2f),
                            selectedLabelColor = RadarColors.Gold,
                            containerColor = RadarColors.SurfaceVariant,
                            labelColor = RadarColors.TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = config.fuelType == ft,
                            selectedBorderColor = RadarColors.Gold,
                            borderColor = RadarColors.GlassBorder,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }

            Text("SEGURANÇA", style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
            ConfigNumberField("Buffer de risco (metros)", config.bufferMeters.toDouble()) {
                onConfigChange(config.copy(bufferMeters = it.toInt()))
            }

            Text("ARMAZENAMENTO DE PRINTS", style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Manter prints por:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = RadarColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ScreenshotPurgeManager.ALLOWED_RETENTION_DAYS.forEach { days ->
                        FilterChip(
                            selected = config.screenshotRetentionDays == days,
                            onClick = { onConfigChange(config.copy(screenshotRetentionDays = days)) },
                            label = { Text("${days}d", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RadarColors.Gold.copy(alpha = 0.2f),
                                selectedLabelColor = RadarColors.Gold,
                                containerColor = RadarColors.SurfaceVariant,
                                labelColor = RadarColors.TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = config.screenshotRetentionDays == days,
                                selectedBorderColor = RadarColors.Gold,
                                borderColor = RadarColors.GlassBorder,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigNumberField(
    label: String,
    value: Double,
    onChange: (Double) -> Unit
) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    // isError: true quando o texto não é um número válido (ex: "5." ou "abc")
    val isError = text.isNotEmpty() && text.toDoubleOrNull() == null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = if (isError) RadarColors.DangerRedGlow else RadarColors.TextSecondary,
            modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                newText.toDoubleOrNull()?.let(onChange)
            },
            modifier = Modifier.width(100.dp),
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RadarColors.Gold),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) RadarColors.DangerRedGlow else RadarColors.Gold,
                unfocusedBorderColor = if (isError) RadarColors.DangerRedGlow else RadarColors.GoldDim.copy(alpha = 0.5f),
                focusedTextColor = RadarColors.Gold,
                unfocusedTextColor = RadarColors.Gold,
                errorBorderColor = RadarColors.DangerRedGlow,
                errorTextColor = RadarColors.Gold
            )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// STATS SCREEN
// ═══════════════════════════════════════════════════════════════════

@Composable
fun StatsScreen(
    uiState: DashboardUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RadarColors.NavyDeep)
                .border(BorderStroke(Dp.Hairline, RadarColors.Gold.copy(alpha = 0.2f)),
                    RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Voltar", color = RadarColors.Gold, fontWeight = FontWeight.Bold)
            }
            Text("ESTATÍSTICAS DO TURNO", style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp),
                color = RadarColors.Gold)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ganho destaque
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RadarColors.Gold.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .border(1.dp, RadarColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("GANHO LÍQUIDO HOJE",
                    style = MaterialTheme.typography.labelSmall, color = RadarColors.TextMuted)
                Text("R$ %.2f".format(uiState.todayEarnings),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 38.sp),
                    color = RadarColors.Gold)
            }

            // Grid de stats
            val total = uiState.todayAccepted + uiState.todayRejected
            val rate = if (total > 0) (uiState.todayAccepted * 100 / total) else 0

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBigCard("${uiState.todayAccepted}", "Aceitas",
                    RadarColors.SafeGreenGlow, Modifier.weight(1f))
                StatBigCard("${uiState.todayRejected}", "Recusadas",
                    RadarColors.DangerRedGlow, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBigCard("$rate%", "Taxa Aceite",
                    RadarColors.Gold, Modifier.weight(1f))
                StatBigCard("${uiState.todayAlerts}", "Alertas Risco",
                    RadarColors.RiskPurpleGlow, Modifier.weight(1f))
            }

            // Config resumo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RadarColors.GlassWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("CONFIGURAÇÃO ATUAL", style = MaterialTheme.typography.labelSmall,
                    color = RadarColors.TextMuted)
                listOf(
                    "Benchmark R$/KM"  to "R$ %.2f".format(uiState.config.targetProfitPerKm),
                    "Benchmark R$/min" to "R$ %.2f".format(uiState.config.targetProfitPerMinute),
                    "Benchmark R$/h"   to "R$ %.0f".format(uiState.config.targetProfitPerHour),
                    "Combustível"      to "R$ %.2f/L (${uiState.config.fuelType.name})".format(uiState.config.fuelPricePerLiter),
                    "Taxa plataforma"  to "${(uiState.config.platformFeePercent * 100).toInt()}%"
                ).forEach { (label, value) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = RadarColors.TextSecondary)
                        Text(value, style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = RadarColors.TextPrimary)
                    }
                }
            }
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun StatBigCard(value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(RadarColors.GlassWhite, RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(value, style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp), color = color)
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = RadarColors.TextMuted)
    }
}

// ─── Previews ────────────────────────────────────────────────────────────────

@Preview(name = "Onboarding — Passo 0 (Acessibilidade)", showBackground = true)
@Composable
private fun OnboardingStep0Preview() {
    RadarCariocaTheme {
        OnboardingScreen(
            config = DriverConfig(),
            onConfigChange = {},
            onAccessibilityClick = {},
            onOverlayClick = {},
            onBatteryClick = {},
            onComplete = {},
            isAccessibilityEnabled = false,
            isOverlayEnabled = false
        )
    }
}

@Preview(name = "Onboarding — Permissões Concedidas", showBackground = true)
@Composable
private fun OnboardingPermissionsGrantedPreview() {
    RadarCariocaTheme {
        OnboardingScreen(
            config = DriverConfig(),
            onConfigChange = {},
            onAccessibilityClick = {},
            onOverlayClick = {},
            onBatteryClick = {},
            onComplete = {},
            isAccessibilityEnabled = true,
            isOverlayEnabled = true
        )
    }
}

private val previewStatsState = DashboardUiState(
    todayEarnings = 312.75,
    todayAccepted = 18,
    todayRejected = 4,
    todayAlerts = 2,
    config = DriverConfig(targetProfitPerKm = 2.50, fuelPricePerLiter = 6.20, kmPerLiter = 12.0),
    hasAccess = true
)

@Preview(name = "StatsScreen", showBackground = true)
@Composable
private fun StatsScreenPreview() {
    RadarCariocaTheme {
        StatsScreen(uiState = previewStatsState, onBack = {})
    }
}

@Preview(name = "SettingsScreen", showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    RadarCariocaTheme {
        SettingsScreen(config = DriverConfig(), onConfigChange = {}, onBack = {})
    }
}
