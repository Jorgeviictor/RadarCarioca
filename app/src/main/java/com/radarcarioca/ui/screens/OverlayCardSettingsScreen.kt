package com.radarcarioca.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radarcarioca.data.model.CardPosition
import com.radarcarioca.data.model.CardTheme
import com.radarcarioca.data.model.OverlayCardSettings
import com.radarcarioca.ui.theme.RadarCariocaTheme
import com.radarcarioca.ui.theme.RadarColors

@Composable
fun OverlayCardSettingsScreen(
    settings: OverlayCardSettings,
    onSettingsChanged: (OverlayCardSettings) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RadarColors.NavyDeep.copy(alpha = 0.95f))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("←", color = RadarColors.Gold, fontSize = 20.sp)
            }
            Text("Aparência do Cartão",
                style = MaterialTheme.typography.titleMedium,
                color = RadarColors.Gold,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Preview do card
            SectionTitle("Pré-visualização")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RadarColors.NavyMid, RoundedCornerShape(12.dp))
                    .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                PreviewCard(settings = settings)
            }

            // Métricas visíveis
            SectionTitle("Indicadores visíveis")
            MetricToggleGroup(settings = settings, onSettingsChanged = onSettingsChanged)

            // Posição
            SectionTitle("Posição do Cartão")
            PositionSelector(settings = settings, onSettingsChanged = onSettingsChanged)

            // Tema
            SectionTitle("Tema do Cartão")
            ThemeSelector(settings = settings, onSettingsChanged = onSettingsChanged)

            // Opacidade
            SectionTitle("Opacidade   ${(settings.opacity * 100).toInt()}%")
            Slider(
                value = settings.opacity,
                onValueChange = { onSettingsChanged(settings.copy(opacity = it)) },
                valueRange = 0.3f..1f,
                colors = SliderDefaults.colors(thumbColor = RadarColors.Gold, activeTrackColor = RadarColors.Gold)
            )

            // Tamanho da fonte
            SectionTitle("Tamanho da fonte   ${settings.fontSize}")
            Slider(
                value = settings.fontSize.toFloat(),
                onValueChange = { onSettingsChanged(settings.copy(fontSize = it.toInt())) },
                valueRange = 12f..18f,
                steps = 5,
                colors = SliderDefaults.colors(thumbColor = RadarColors.Gold, activeTrackColor = RadarColors.Gold)
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PreviewCard(settings: OverlayCardSettings) {
    val bgColor = when (settings.theme) {
        CardTheme.DARK -> Color(0xFF0D1117)
        CardTheme.LIGHT -> Color(0xFFF5F5F5)
        CardTheme.GREEN -> Color(0xFF0A1F0A)
    }
    val textColor = if (settings.theme == CardTheme.LIGHT) Color(0xFF111111) else Color.White
    val accentColor = Color(0xFF00E676)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(settings.opacity)
            .background(bgColor.copy(alpha = 0.95f), RoundedCornerShape(10.dp))
            .border(2.dp, accentColor, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Av. Atlântica, Copacabana",
                fontSize = (settings.fontSize - 3).sp,
                color = textColor.copy(alpha = 0.6f))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (settings.showProfitPerKm) PreviewMetric("\$/Km", "2.80", accentColor, settings.fontSize)
                if (settings.showProfitPerHour) PreviewMetric("\$/Hora", "79.69", accentColor, settings.fontSize)
                if (settings.showRating) PreviewMetric("Nota", "4.95", accentColor, settings.fontSize)
                if (settings.showLucroHora) PreviewMetric("Lucro/H", "61.00", accentColor, settings.fontSize)
                if (settings.showLucroPercent) PreviewMetric("Lucro%", "72%", accentColor, settings.fontSize)
                if (settings.showLucro) PreviewMetric("Lucro", "18.50", accentColor, settings.fontSize)
                if (settings.showPerMinute) PreviewMetric("\$/Min", "1.32", accentColor, settings.fontSize)
            }
            Text("⏱ 32 min • 15.2 km",
                fontSize = (settings.fontSize - 4).sp,
                color = textColor.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun PreviewMetric(label: String, value: String, color: Color, fontSize: Int) {
    Column {
        Text(label, fontSize = (fontSize - 4).sp, color = Color.White.copy(alpha = 0.5f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("▎", fontSize = (fontSize - 2).sp, color = color)
            Text(value, fontSize = fontSize.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MetricToggleGroup(settings: OverlayCardSettings, onSettingsChanged: (OverlayCardSettings) -> Unit) {
    val metrics = listOf(
        Triple("\$/Km", settings.showProfitPerKm) { v: Boolean -> onSettingsChanged(settings.copy(showProfitPerKm = v)) },
        Triple("\$/Hora", settings.showProfitPerHour) { v: Boolean -> onSettingsChanged(settings.copy(showProfitPerHour = v)) },
        Triple("Nota", settings.showRating) { v: Boolean -> onSettingsChanged(settings.copy(showRating = v)) },
        Triple("Lucro/Hora", settings.showLucroHora) { v: Boolean -> onSettingsChanged(settings.copy(showLucroHora = v)) },
        Triple("Lucro %", settings.showLucroPercent) { v: Boolean -> onSettingsChanged(settings.copy(showLucroPercent = v)) },
        Triple("Lucro", settings.showLucro) { v: Boolean -> onSettingsChanged(settings.copy(showLucro = v)) },
        Triple("\$/Min", settings.showPerMinute) { v: Boolean -> onSettingsChanged(settings.copy(showPerMinute = v)) },
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.GlassWhite, RoundedCornerShape(12.dp))
            .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(12.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        metrics.forEach { (label, checked, onChange) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = RadarColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium)
                Checkbox(
                    checked = checked,
                    onCheckedChange = onChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = RadarColors.Gold,
                        uncheckedColor = RadarColors.TextMuted
                    )
                )
            }
        }
    }
}

@Composable
private fun PositionSelector(settings: OverlayCardSettings, onSettingsChanged: (OverlayCardSettings) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardPosition.values().forEach { pos ->
            val label = when (pos) {
                CardPosition.LEFT -> "Esquerda"
                CardPosition.CENTER -> "Centro"
                CardPosition.RIGHT -> "Direita"
            }
            val selected = settings.position == pos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected) RadarColors.Gold.copy(alpha = 0.15f) else RadarColors.GlassWhite,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        if (selected) 2.dp else 1.dp,
                        if (selected) RadarColors.Gold else RadarColors.GlassBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSettingsChanged(settings.copy(position = pos)) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (selected) RadarColors.Gold else RadarColors.TextMuted,
                    fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun ThemeSelector(settings: OverlayCardSettings, onSettingsChanged: (OverlayCardSettings) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val themes = listOf(
            CardTheme.DARK to "Escuro",
            CardTheme.LIGHT to "Claro",
            CardTheme.GREEN to "Verde"
        )
        themes.forEach { (theme, label) ->
            val selected = settings.theme == theme
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected) RadarColors.Gold.copy(alpha = 0.15f) else RadarColors.GlassWhite,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        if (selected) 2.dp else 1.dp,
                        if (selected) RadarColors.Gold else RadarColors.GlassBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSettingsChanged(settings.copy(theme = theme)) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (selected) RadarColors.Gold else RadarColors.TextMuted,
                    fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = RadarColors.TextMuted,
        fontWeight = FontWeight.SemiBold
    )
}

// ─── Previews ────────────────────────────────────────────────────────────────

@Preview(name = "OverlayCardSettings — Padrão", showBackground = true)
@Composable
private fun OverlayCardSettingsDefaultPreview() {
    RadarCariocaTheme {
        OverlayCardSettingsScreen(
            settings = OverlayCardSettings(),
            onSettingsChanged = {},
            onBack = {}
        )
    }
}

@Preview(name = "OverlayCardSettings — Tema Claro, Opacidade 60%", showBackground = true)
@Composable
private fun OverlayCardSettingsLightPreview() {
    RadarCariocaTheme {
        OverlayCardSettingsScreen(
            settings = OverlayCardSettings(
                theme = CardTheme.LIGHT,
                opacity = 0.6f,
                fontSize = 16,
                position = CardPosition.LEFT
            ),
            onSettingsChanged = {},
            onBack = {}
        )
    }
}