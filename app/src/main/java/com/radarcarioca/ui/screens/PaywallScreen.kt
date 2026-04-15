package com.radarcarioca.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radarcarioca.data.model.SubscriptionPlans
import com.radarcarioca.domain.model.SubscriptionPlan
import com.radarcarioca.domain.model.SubscriptionState
import com.radarcarioca.ui.theme.RadarCariocaTheme
import com.radarcarioca.ui.theme.RadarColors

@Composable
fun PaywallScreen(
    subscriptionState: SubscriptionState,
    onSelectPlan: (SubscriptionPlan) -> Unit,
    onStartTrial: () -> Unit,
    onRestorePurchase: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(SubscriptionPlans.TRIMESTRAL) }

    val gridColor = remember { Color(0xFFC9A84C).copy(alpha = 0.03f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep)
            .drawBehind {
                val step = 40.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
                    y += step
                }
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mini logo
            MiniRadarLogo()

            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = "Radar Carioca Pro",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RadarColors.GoldPrimary,
                letterSpacing = 1.sp
            )

            // Subtítulo de trial ou expirado
            when (subscriptionState) {
                is SubscriptionState.InTrial -> {
                    Spacer(modifier = Modifier.height(6.dp))
                    TrialBadge("${subscriptionState.daysRemaining} dias grátis restantes")
                }
                is SubscriptionState.Expired -> {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seu período gratuito encerrou.\nAssine para continuar.",
                        fontSize = 14.sp,
                        color = RadarColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
                is SubscriptionState.TrialAvailable -> {
                    Spacer(modifier = Modifier.height(6.dp))
                    TrialBadge("14 dias GRÁTIS para começar")
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Benefícios
            BenefitsList()

            Spacer(modifier = Modifier.height(24.dp))

            // Planos
            Text(
                text = "Escolha seu plano",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubscriptionPlans.ALL.forEach { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = selectedPlan.sku == plan.sku,
                    onClick = { selectedPlan = plan }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão principal
            val btnLabel = when (subscriptionState) {
                is SubscriptionState.TrialAvailable -> "Começar 14 dias grátis"
                is SubscriptionState.InTrial        -> "Assinar ${selectedPlan.name}"
                else                                -> "Assinar ${selectedPlan.name} — ${selectedPlan.totalPrice}"
            }

            GoldButton(
                text = btnLabel,
                onClick = {
                    if (subscriptionState is SubscriptionState.TrialAvailable) {
                        onStartTrial()
                    } else {
                        onSelectPlan(selectedPlan)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Restaurar compra
            TextButton(onClick = onRestorePurchase) {
                Text(
                    "Restaurar compra",
                    color = RadarColors.TextMuted,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cancele quando quiser. Sem fidelidade.",
                fontSize = 11.sp,
                color = RadarColors.TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Componentes ────────────────────────────────────────────────────────────

@Composable
private fun TrialBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(RadarColors.GreenGlow.copy(alpha = 0.15f))
            .border(1.dp, RadarColors.GreenStatus, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 5.dp)
    ) {
        Text(
            text = "✓  $text",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = RadarColors.GreenStatus
        )
    }
}

@Composable
private fun BenefitsList() {
    val items = listOf(
        "🛡️  Alertas de segurança em tempo real",
        "💰  Cálculo de R$/KM automático",
        "📊  Histórico completo do seu turno",
        "🗺️  Mapa de facções do Rio integrado",
        "⚡  Overlay sobre o app da Uber/99"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RadarColors.GlassWhite)
            .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { benefit ->
            Text(
                text = benefit,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected && plan.badge == "MAIS POPULAR" -> RadarColors.GoldPrimary
        isSelected                                 -> RadarColors.GreenStatus
        else                                       -> RadarColors.GlassBorder
    }

    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(
            colors = listOf(
                RadarColors.NavyMid,
                RadarColors.NavyMid.copy(alpha = 0.7f)
            )
        )
    } else {
        Brush.horizontalGradient(colors = listOf(RadarColors.GlassWhite, RadarColors.GlassWhite))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado esquerdo: nome + savings
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else RadarColors.TextSecondary
                    )
                    plan.badge?.let { badge ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (badge == "MAIS POPULAR") RadarColors.GoldPrimary
                                    else RadarColors.GreenStatus
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RadarColors.NavyDeep
                            )
                        }
                    }
                }
                plan.savings?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = RadarColors.GreenStatus
                    )
                }
            }

            // Lado direito: preço
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.pricePerMonth,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) RadarColors.GoldPrimary else RadarColors.TextSecondary
                )
                if (plan.durationMonths > 1) {
                    Text(
                        text = "Total ${plan.totalPrice}",
                        fontSize = 11.sp,
                        color = RadarColors.TextMuted
                    )
                }
            }
        }

        // Indicador de seleção
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
            ) {
                Text("✓", color = borderColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GoldButton(text: String, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "shimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        RadarColors.GoldDim,
                        RadarColors.GoldPrimary,
                        RadarColors.GoldLight.copy(alpha = 0.6f + shimmer * 0.4f),
                        RadarColors.GoldPrimary
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RadarColors.NavyDeep,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun MiniRadarLogo() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(RadarColors.NavyMid)
            .border(2.dp, RadarColors.GoldPrimary, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("📡", fontSize = 32.sp, textAlign = TextAlign.Center)
    }
}

// ─── Previews ────────────────────────────────────────────────────────────────

@Preview(name = "Paywall — Trial Disponível", showBackground = true)
@Composable
private fun PaywallTrialAvailablePreview() {
    RadarCariocaTheme {
        PaywallScreen(
            subscriptionState = SubscriptionState.TrialAvailable,
            onSelectPlan = {}, onStartTrial = {}, onRestorePurchase = {}
        )
    }
}

@Preview(name = "Paywall — Em Trial (8 dias restantes)", showBackground = true)
@Composable
private fun PaywallInTrialPreview() {
    RadarCariocaTheme {
        PaywallScreen(
            subscriptionState = SubscriptionState.InTrial(daysRemaining = 8),
            onSelectPlan = {}, onStartTrial = {}, onRestorePurchase = {}
        )
    }
}

@Preview(name = "Paywall — Expirado", showBackground = true)
@Composable
private fun PaywallExpiredPreview() {
    RadarCariocaTheme {
        PaywallScreen(
            subscriptionState = SubscriptionState.Expired,
            onSelectPlan = {}, onStartTrial = {}, onRestorePurchase = {}
        )
    }
}
