package com.radarcarioca.data.model

data class OverlayCardSettings(
    val showProfitPerKm: Boolean = true,
    val showProfitPerHour: Boolean = true,
    val showRating: Boolean = true,
    val showLucroHora: Boolean = false,
    val showLucroPercent: Boolean = false,
    val showLucro: Boolean = false,
    val showPerMinute: Boolean = false,
    val position: CardPosition = CardPosition.CENTER,
    val theme: CardTheme = CardTheme.DARK,
    val opacity: Float = 1f,
    val fontSize: Int = 15,
    // Duração do cartão em minutos (0 = permanente)
    val cardDurationMinutes: Int = 4,
    // Avançado
    val textNotificationEnabled: Boolean = true,
    val autoScreenshotEnabled: Boolean = false,
    val passengerMessageEnabled: Boolean = false,
    // Ajustes do Copiloto (botão flutuante)
    val floatingButtonSize: Float = 56f,
    val floatingButtonOpacity: Float = 1f
)

enum class CardPosition { LEFT, CENTER, RIGHT }
enum class CardTheme { DARK, LIGHT, GREEN }