package com.radarcarioca.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radarcarioca.ui.theme.RadarColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Rotação do radar
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "radar"
    )

    // Pulso do escudo
    val shieldScale by infiniteTransition.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shield"
    )

    // Alpha do título
    var titleAlpha by remember { mutableFloatStateOf(0f) }
    val titleAlphaAnim by animateFloatAsState(
        targetValue = titleAlpha,
        animationSpec = tween(800),
        label = "titleAlpha"
    )

    LaunchedEffect(Unit) {
        delay(300)
        titleAlpha = 1f
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep),
        contentAlignment = Alignment.Center
    ) {
        // Grade decorativa de fundo
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRadarGrid()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo — escudo + radar animado
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer { scaleX = shieldScale; scaleY = shieldScale }
                ) {
                    drawShield(radarAngle)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto RADAR
            Text(
                text = "RADAR",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RadarColors.GoldPrimary.copy(alpha = titleAlphaAnim),
                letterSpacing = 8.sp
            )

            // Texto Carioca
            Text(
                text = "Carioca",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = titleAlphaAnim),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seu copiloto inteligente",
                fontSize = 13.sp,
                color = RadarColors.TextMuted.copy(alpha = titleAlphaAnim),
                letterSpacing = 1.sp
            )
        }
    }
}

private fun DrawScope.drawShield(radarAngle: Float) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val shieldW = size.width * 0.85f
    val shieldH = size.height * 0.90f

    // Sombra do escudo
    drawPath(
        path = shieldPath(cx, cy, shieldW + 6f, shieldH + 6f),
        color = Color(0xFFC9A84C).copy(alpha = 0.3f),
        style = Stroke(width = 3f)
    )

    // Corpo do escudo (azul naval)
    drawPath(
        path = shieldPath(cx, cy, shieldW, shieldH),
        color = Color(0xFF0D1B3E)
    )

    // Borda dourada do escudo
    drawPath(
        path = shieldPath(cx, cy, shieldW, shieldH),
        color = Color(0xFFC9A84C),
        style = Stroke(width = 5f)
    )

    // Círculos do radar
    val gold = Color(0xFFC9A84C)
    val radii = listOf(0.18f, 0.28f, 0.38f, 0.48f)
    radii.forEach { ratio ->
        drawCircle(
            color = gold.copy(alpha = 0.25f),
            radius = size.width * ratio,
            center = Offset(cx, cy * 0.90f),
            style = Stroke(width = 1.2f)
        )
    }

    // Linhas de grade (horizontal + vertical)
    drawLine(gold.copy(alpha = 0.15f), Offset(cx - size.width * 0.48f, cy * 0.90f), Offset(cx + size.width * 0.48f, cy * 0.90f), 1f)
    drawLine(gold.copy(alpha = 0.15f), Offset(cx, cy * 0.90f - size.width * 0.48f), Offset(cx, cy * 0.90f + size.width * 0.48f), 1f)

    // Linha giratória do radar (sweep)
    rotate(degrees = radarAngle, pivot = Offset(cx, cy * 0.90f)) {
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, Color(0xFF00E676).copy(alpha = 0.8f)),
                start = Offset(cx, cy * 0.90f),
                end = Offset(cx + size.width * 0.48f, cy * 0.90f)
            ),
            start = Offset(cx, cy * 0.90f),
            end = Offset(cx + size.width * 0.48f, cy * 0.90f),
            strokeWidth = 2.5f
        )
    }

    // Pontos de alerta vermelhos
    val alertDots = listOf(
        Offset(cx + size.width * 0.15f, cy * 0.65f),
        Offset(cx - size.width * 0.20f, cy * 0.80f),
        Offset(cx + size.width * 0.28f, cy * 0.85f)
    )
    alertDots.forEach { pos ->
        drawCircle(Color(0xFFFF1744), radius = 4f, center = pos)
        drawCircle(Color(0xFFFF1744).copy(alpha = 0.3f), radius = 8f, center = pos)
    }

    // Montanha / silhueta (símbolo do Rio)
    val mountainPath = Path().apply {
        moveTo(cx - size.width * 0.22f, cy * 1.05f)
        lineTo(cx - size.width * 0.08f, cy * 0.78f)
        lineTo(cx, cy * 0.88f)
        lineTo(cx + size.width * 0.10f, cy * 0.72f)
        lineTo(cx + size.width * 0.22f, cy * 1.05f)
        close()
    }
    drawPath(mountainPath, Color(0xFFC9A84C).copy(alpha = 0.85f))
    drawPath(mountainPath, Color(0xFFC9A84C), style = Stroke(width = 1.5f))
}

private fun shieldPath(cx: Float, cy: Float, w: Float, h: Float): Path {
    val left  = cx - w / 2f
    val right = cx + w / 2f
    val top   = cy - h * 0.48f
    val bot   = cy + h * 0.52f

    return Path().apply {
        moveTo(cx, top)
        cubicTo(right * 0.88f + left * 0.12f, top, right, top + h * 0.12f, right, cy - h * 0.05f)
        cubicTo(right, cy + h * 0.25f, cx + w * 0.25f, cy + h * 0.42f, cx, bot)
        cubicTo(cx - w * 0.25f, cy + h * 0.42f, left, cy + h * 0.25f, left, cy - h * 0.05f)
        cubicTo(left, top + h * 0.12f, cx * 0.12f + left * 0.88f + w * 0.12f, top, cx, top)
        close()
    }
}

private fun DrawScope.drawRadarGrid() {
    val gold = Color(0xFFC9A84C)
    val step = 40.dp.toPx()
    var x = 0f
    while (x < size.width) {
        drawLine(gold.copy(alpha = 0.03f), Offset(x, 0f), Offset(x, size.height), 1f)
        x += step
    }
    var y = 0f
    while (y < size.height) {
        drawLine(gold.copy(alpha = 0.03f), Offset(0f, y), Offset(size.width, y), 1f)
        y += step
    }
}
