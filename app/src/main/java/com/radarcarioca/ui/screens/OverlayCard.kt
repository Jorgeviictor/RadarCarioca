package com.radarcarioca.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radarcarioca.data.model.MetricSignalLevel
import com.radarcarioca.data.model.MetricSignals
import com.radarcarioca.data.model.OverlayCardSettings
import com.radarcarioca.data.model.OverlayStatus
import com.radarcarioca.data.model.RideAnalysis
import com.radarcarioca.data.model.RideOffer
import com.radarcarioca.data.model.FinancialResult
import com.radarcarioca.data.model.SecurityResult
import com.radarcarioca.ui.theme.RadarCariocaTheme
import kotlinx.coroutines.delay

// ─── Paleta de cores ──────────────────────────────────────────────────────────
// Fundo do card → segurança geográfica
private val COLOR_GREEN  = Color(0xFF00E676)
private val COLOR_YELLOW = Color(0xFFFFD600)
private val COLOR_RED    = Color(0xFFFF1744)
private val GOLD_BORDER  = Color(0xFFC9A84C)

// Fundos escuros por nível de risco geográfico
private val BG_GREEN  = Color(0xFF00140A)  // Verde escuro → destino seguro
private val BG_YELLOW = Color(0xFF141000)  // Amarelo escuro → zona de atenção
private val BG_RED    = Color(0xFF1A0000)  // Vermelho escuro → área de risco

// Sinaleiros financeiros → MetricSignalLevel (independente do fundo do card)
private val SIGNAL_GOOD    = Color(0xFF00E676)  // Verde
private val SIGNAL_NEUTRAL = Color(0xFFFFD600)  // Amarelo
private val SIGNAL_BAD     = Color(0xFFFF1744)  // Vermelho

// Mapeamento de nível de sinal financeiro → cor do sinaleiro
private fun MetricSignalLevel.toColor(): Color = when (this) {
    MetricSignalLevel.GOOD    -> SIGNAL_GOOD
    MetricSignalLevel.NEUTRAL -> SIGNAL_NEUTRAL
    MetricSignalLevel.BAD     -> SIGNAL_BAD
}

// ─── OverlayCard ─────────────────────────────────────────────────────────────

/**
 * Card de overlay exibido sobre o Uber/99 ao receber uma oferta de corrida.
 *
 * Melhorias implementadas:
 * 1. Fundo do card muda de cor integralmente conforme o risco geográfico
 * 2. Grid de métricas com sinaleiro independente (R$/KM · R$/Min · R$/Hora)
 * 3. TTL de 6 segundos com barra de progresso visual
 * 4. Clicável — abre Google Maps no destino da corrida
 */
@Composable
fun OverlayCard(
    analysis: RideAnalysis,
    settings: OverlayCardSettings = OverlayCardSettings(),
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    // Fundo do card é determinado EXCLUSIVAMENTE pelo status geográfico
    val isRisk    = analysis.overlayStatus == OverlayStatus.RED
    val isWarning = analysis.overlayStatus == OverlayStatus.YELLOW

    // ── 3. TTL: 6 segundos com countdown animado ─────────────────────────────
    var progress by remember { mutableStateOf(1f) }
    LaunchedEffect(Unit) {
        val ttlMs  = 6_000L
        val stepMs = 50L
        var elapsed = 0L
        while (elapsed < ttlMs) {
            delay(stepMs)
            elapsed += stepMs
            progress = 1f - (elapsed.toFloat() / ttlMs)
        }
        onDismiss()
    }

    // ── Cores derivadas do status geográfico ──────────────────────────────────
    val statusColor = when (analysis.overlayStatus) {
        OverlayStatus.GREEN  -> COLOR_GREEN
        OverlayStatus.YELLOW -> COLOR_YELLOW
        OverlayStatus.RED    -> COLOR_RED
    }
    val cardBg = when (analysis.overlayStatus) {
        OverlayStatus.GREEN  -> BG_GREEN
        OverlayStatus.YELLOW -> BG_YELLOW
        OverlayStatus.RED    -> BG_RED
    }

    // Borda pulsante — mais rápida em área de risco, lenta em zona de atenção
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(when { isRisk -> 380; isWarning -> 620; else -> 980 }),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // ── 4. Intent Google Maps ─────────────────────────────────────────────────
    val onCardClick: () -> Unit = {
        val lat = analysis.offer.destinationLat
        val lng = analysis.offer.destinationLng
        if (lat != 0.0 && lng != 0.0) {
            val label = Uri.encode(analysis.offer.destinationText)
            val uri   = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
            val maps  = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            // Fallback caso Maps não esteja instalado
            val target = if (maps.resolveActivity(context.packageManager) != null) maps
                         else Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(target)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // ── 1. Fundo integral baseado no risco ──────────────────────
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            cardBg,
                            cardBg.copy(alpha = 0.92f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = if (isRisk) 2.5.dp else 1.8.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            statusColor.copy(alpha = borderAlpha),
                            GOLD_BORDER.copy(alpha = borderAlpha * 0.5f),
                            statusColor.copy(alpha = borderAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                // ── 4. Card clicável → Google Maps ──────────────────────────
                .clickable(onClick = onCardClick)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── Linha 1: Status + Destino ─────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusDot(color = statusColor, pulsing = isRisk, borderAlpha = borderAlpha)

                Column(modifier = Modifier.weight(1f)) {
                    // Banner de alerta geográfico — visível apenas em YELLOW e RED
                    when {
                        isRisk -> {
                            val areaName = (analysis.security as? SecurityResult.Danger)?.areaName
                                ?: "Área de Risco"
                            Text(
                                text = "⛔ ÁREA DE RISCO — $areaName",
                                fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                                color = COLOR_RED, letterSpacing = 0.5.sp
                            )
                        }
                        isWarning -> {
                            val w = analysis.security as SecurityResult.Warning
                            val label = w.areaName.ifBlank { w.featureName }
                            Text(
                                text = "⚠ ATENÇÃO — $label (${w.distanceMeters} m)",
                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = COLOR_YELLOW, letterSpacing = 0.3.sp
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = analysis.offer.destinationText,
                            fontSize = (settings.fontSize - 2).sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // Ícone Maps — indica que o card é clicável
                        Text(
                            text = "🗺",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }

            // ── Linha 2: Grid de Métricas (Sinaleiro Desacoplado) ─────────
            // As cores aqui são independentes do fundo do card (risco ≠ rentabilidade)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSquare(
                    label = "R$/KM",
                    value = "%.2f".format(analysis.financial.profitPerKm),
                    signalColor = analysis.metricSignals.profitPerKm.toColor(),
                    modifier = Modifier.weight(1f)
                )
                MetricSquare(
                    label = "R$/MIN",
                    value = "%.2f".format(analysis.financial.profitPerMinute),
                    signalColor = analysis.metricSignals.profitPerMinute.toColor(),
                    modifier = Modifier.weight(1f)
                )
                MetricSquare(
                    label = "R$/HORA",
                    value = "%.0f".format(analysis.financial.profitPerHour),
                    signalColor = analysis.metricSignals.profitPerHour.toColor(),
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Linha 3: Chips de info da corrida ─────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip("⏱ ${analysis.offer.estimatedMinutes} min")
                InfoChip("📍 ${"%.1f".format(analysis.offer.rideDistanceKm)} km")
                InfoChip("💰 R$ ${"%.2f".format(analysis.offer.fareValue)}")

                val pct = analysis.financial.profitMarginPercent
                if (pct != 0.0) {
                    InfoChip(
                        text = "%.0f%%".format(pct),
                        color = when {
                            pct >= 55 -> COLOR_GREEN
                            pct >= 35 -> COLOR_YELLOW
                            else      -> COLOR_RED
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Lucro total — destaque lateral
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "LUCRO", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f), letterSpacing = 0.8.sp)
                    Text(
                        text = "R$ ${"%.2f".format(analysis.financial.netProfit)}",
                        fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (analysis.financial.netProfit >= 0) COLOR_GREEN else COLOR_RED
                    )
                }
            }

            // ── 3. Barra de Countdown (TTL 6s) ────────────────────────────
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = statusColor.copy(alpha = 0.7f),
                trackColor = statusColor.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round
            )

            // ── Botões RECUSAR / ACEITAR ──────────────────────────────────
            // Decisão é SEMPRE manual. Os botões não mudam de cor com o risco
            // para não induzir aceitação ou recusa automática.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "✗  RECUSAR",
                    bgColor    = COLOR_RED.copy(alpha = 0.15f),
                    borderColor = COLOR_RED.copy(alpha = 0.5f),
                    textColor  = COLOR_RED,
                    modifier   = Modifier.weight(1f),
                    fontSize   = settings.fontSize - 1,
                    onClick    = onReject
                )
                ActionButton(
                    text = "✓  ACEITAR",
                    bgColor    = Color.White.copy(alpha = 0.06f),
                    borderColor = Color.White.copy(alpha = 0.20f),
                    textColor  = Color.White.copy(alpha = 0.80f),
                    modifier   = Modifier.weight(1f),
                    fontSize   = settings.fontSize - 1,
                    onClick    = onAccept
                )
            }
        }
    }
}

// ─── Componentes internos ────────────────────────────────────────────────────

/**
 * Quadrado de métrica com sinaleiro próprio.
 * A cor aqui é INDEPENDENTE do fundo do card (risco geográfico vs. rentabilidade).
 */
@Composable
private fun MetricSquare(
    label: String,
    value: String,
    signalColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = signalColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = signalColor.copy(alpha = 0.30f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Sinaleiro — ponto de cor no topo do quadrado
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(signalColor, RoundedCornerShape(50))
        )
        Text(
            text = "R$ $value",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = signalColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.40f),
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatusDot(color: Color, pulsing: Boolean, borderAlpha: Float) {
    Box(modifier = Modifier.size(if (pulsing) 14.dp else 12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color.copy(alpha = if (pulsing) borderAlpha * 0.4f else 0.18f), RoundedCornerShape(50))
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .align(Alignment.Center)
                .background(color, RoundedCornerShape(50))
        )
    }
}

@Composable
private fun InfoChip(text: String, color: Color = Color.White.copy(alpha = 0.5f)) {
    Text(text = text, fontSize = 11.sp, color = color, fontWeight = FontWeight.Normal)
}

@Composable
private fun ActionButton(
    text: String,
    bgColor: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier,
    fontSize: Int,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
    ) {
        Text(
            text = text, color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize.sp, letterSpacing = 0.5.sp
        )
    }
}

// ─── Previews ────────────────────────────────────────────────────────────────

private fun previewFinancial() = FinancialResult(
    netProfit = 18.50, profitPerKm = 2.80, profitPerHour = 79.70,
    profitPerMinute = 1.32, fuelCost = 4.20, platformCut = 6.00,
    netRevenue = 24.00, totalDistanceKm = 8.0, netProfitPerHour = 79.70,
    profitMarginPercent = 61.7
)

private fun previewAnalysis(status: OverlayStatus, security: SecurityResult = SecurityResult.Safe) =
    RideAnalysis(
        offer = RideOffer(
            destinationText = "Av. Atlântica, Copacabana",
            fareValue = 30.00, rideDistanceKm = 6.6, deadheadDistanceKm = 1.4,
            estimatedMinutes = 32, destinationLat = -22.9714, destinationLng = -43.1827
        ),
        security = security,
        financial = previewFinancial(),
        overlayStatus = status,
        metricSignals = MetricSignals(
            profitPerKm = MetricSignalLevel.GOOD,
            profitPerMinute = MetricSignalLevel.GOOD,
            profitPerHour = MetricSignalLevel.GOOD
        )
    )

@Preview(name = "OverlayCard — Verde (Destino Seguro)", showBackground = true, backgroundColor = 0xFF060D1F)
@Composable
private fun OverlayCardGreenPreview() {
    RadarCariocaTheme {
        OverlayCard(
            analysis = previewAnalysis(OverlayStatus.GREEN, SecurityResult.Safe),
            onAccept = {}, onReject = {}, onDismiss = {}
        )
    }
}

@Preview(name = "OverlayCard — Amarelo (Zona de Atenção 320m)", showBackground = true, backgroundColor = 0xFF060D1F)
@Composable
private fun OverlayCardYellowPreview() {
    RadarCariocaTheme {
        OverlayCard(
            analysis = previewAnalysis(
                status = OverlayStatus.YELLOW,
                security = SecurityResult.Warning("Complexo da Maré", "Zona Norte", 320)
            ),
            onAccept = {}, onReject = {}, onDismiss = {}
        )
    }
}

@Preview(name = "OverlayCard — Vermelho (Área de Risco)", showBackground = true, backgroundColor = 0xFF060D1F)
@Composable
private fun OverlayCardRedPreview() {
    RadarCariocaTheme {
        OverlayCard(
            analysis = previewAnalysis(
                status = OverlayStatus.RED,
                security = SecurityResult.Danger("Complexo do Alemão", "Zona Norte", 0)
            ),
            onAccept = {}, onReject = {}, onDismiss = {}
        )
    }
}
