package com.radarcarioca.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object RadarColors {
    val NavyDeep       = Color(0xFF060D1F)
    val NavyMid        = Color(0xFF0D1B3E)
    val NavyLight      = Color(0xFF1A2F5A)
    val Surface        = Color(0xFF111827)
    val SurfaceVariant = Color(0xFF1F2937)

    val Gold            = Color(0xFFC9A84C)
    val GoldLight       = Color(0xFFE8C96A)
    val GoldDim         = Color(0xFF8B6F2E)

    val SafeGreen       = Color(0xFF1A6B2F)
    val SafeGreenLight  = Color(0xFF22C55E)
    val SafeGreenGlow   = Color(0xFF4ADE80)

    val CautionYellow      = Color(0xFF92400E)
    val CautionYellowLight = Color(0xFFEAB308)
    val CautionYellowGlow  = Color(0xFFFBBF24)

    val DangerRed       = Color(0xFF7F1D1D)
    val DangerRedLight  = Color(0xFFEF4444)
    val DangerRedGlow   = Color(0xFFF87171)

    // VERMELHO — Alerta máximo de segurança (substituiu roxo)
    val RiskPurple      = Color(0xFF7F1D1D)
    val RiskPurpleLight = Color(0xFFDC2626)
    val RiskPurpleGlow  = Color(0xFFFF1744)
    val RiskPurpleBright= Color(0xFFFF5252)

    // Aliases para compatibilidade
    val GoldPrimary    = Gold
    val GreenStatus    = SafeGreenLight
    val GreenGlow      = SafeGreenGlow

    val TextPrimary    = Color(0xFFE2E8F0)
    val TextSecondary  = Color(0xFF94A3B8)
    val TextMuted      = Color(0xFF64748B)

    val OverlayBg      = Color(0xF0060D1F)
    val GlassWhite     = Color(0x0AFFFFFF)
    val GlassBorder    = Color(0x1AFFFFFF)
}

val RadarTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = 0.02.sp,
        color = RadarColors.Gold
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.05.sp,
        color = RadarColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.03.sp,
        color = RadarColors.TextPrimary
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.08.sp,
        color = RadarColors.Gold
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        color = RadarColors.TextPrimary
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = RadarColors.TextSecondary
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = RadarColors.TextSecondary
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 0.12.sp,
        color = RadarColors.TextMuted
    )
)

private val RadarColorScheme = darkColorScheme(
    primary          = RadarColors.Gold,
    onPrimary        = RadarColors.NavyDeep,
    primaryContainer = RadarColors.NavyMid,
    secondary        = RadarColors.SafeGreenLight,
    onSecondary      = RadarColors.NavyDeep,
    background       = RadarColors.NavyDeep,
    onBackground     = RadarColors.TextPrimary,
    surface          = RadarColors.Surface,
    onSurface        = RadarColors.TextPrimary,
    surfaceVariant   = RadarColors.SurfaceVariant,
    onSurfaceVariant = RadarColors.TextSecondary,
    error            = RadarColors.DangerRedLight,
    outline          = RadarColors.GoldDim
)

@Composable
fun RadarCariocaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RadarColorScheme,
        typography = RadarTypography,
        content = content
    )
}