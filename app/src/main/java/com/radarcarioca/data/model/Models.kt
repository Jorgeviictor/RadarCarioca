package com.radarcarioca.data.model

// ═══════════════════════════════════════════════════════════════════
// MODELOS DE SEGURANÇA GEOGRÁFICA
// ═══════════════════════════════════════════════════════════════════

/**
 * Entidade de domínio pura — sem dependência de Room ou qualquer framework.
 * Suporta Points, LineStrings e Polygons do GeoJSON de facções RJ.
 */
data class GeoFeature(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val area: String = "",                 // Ex: "Complexo da Maré"
    val geometryType: String,              // "Point", "LineString", "Polygon"
    val coordinatesJson: String,           // JSON serializado das coordenadas
    val centerLat: Double,                 // Centro calculado para queries rápidas
    val centerLng: Double,
    val bufferKm: Double = 0.2,           // 200m de amortecimento padrão
    val alertLevel: AlertLevel = AlertLevel.DANGER
)

enum class AlertLevel { DANGER, CAUTION }

/**
 * Resultado da verificação de segurança geográfica.
 */
sealed class SecurityResult {
    object Safe : SecurityResult()

    /** Destino dentro do raio de atenção (≤ 500 m da borda de área de risco) → Overlay AMARELO */
    data class Warning(
        val featureName: String,
        val areaName: String,
        val distanceMeters: Int       // distância até a borda da área perigosa
    ) : SecurityResult()

    /** Destino exatamente dentro da área de risco → Overlay ROXO + vibração */
    data class Danger(
        val featureName: String,
        val areaName: String,
        val distanceMeters: Int,      // 0 quando dentro de polígono
        val alertLevel: AlertLevel = AlertLevel.DANGER
    ) : SecurityResult()
}

// ═══════════════════════════════════════════════════════════════════
// MODELOS FINANCEIROS
// ═══════════════════════════════════════════════════════════════════

/**
 * Dados de uma oferta de corrida capturada pelo AccessibilityService.
 */
data class RideOffer(
    val destinationText: String,      // Texto bruto do destino (ex: "Rua X, 123")
    val fareValue: Double,            // Valor bruto da corrida (R$)
    val rideDistanceKm: Double,       // Distância da corrida (KM)
    val deadheadDistanceKm: Double,   // Distância até o passageiro (KM) — custo oculto!
    val estimatedMinutes: Int = 0,
    val destinationLat: Double = 0.0, // Preenchido após geocodificação
    val destinationLng: Double = 0.0,
    val sourceApp: String = "uber",   // "uber" | "99"
    val passengerRating: Double = 0.0 // Nota do passageiro (0 = não informado)
)

/**
 * Resultado completo do cálculo financeiro de uma corrida.
 */
data class FinancialResult(
    val netProfit: Double,              // Lucro líquido (R$)
    val profitPerKm: Double,            // R$/KM
    val profitPerHour: Double,          // R$/hora estimado (bruto)
    val fuelCost: Double,               // Custo total de combustível
    val platformCut: Double,            // Desconto da plataforma
    val netRevenue: Double,             // Receita líquida (após taxa da plataforma)
    val totalDistanceKm: Double,        // Corrida + deslocamento
    val netProfitPerHour: Double,       // Lucro líquido por hora
    val profitMarginPercent: Double,    // Margem de lucro em %
    val profitPerMinute: Double         // Lucro por minuto
)

// ═══════════════════════════════════════════════════════════════════
// MODELO DE ANÁLISE COMPLETA (Segurança + Financeiro)
// ═══════════════════════════════════════════════════════════════════

/**
 * Resultado final exibido no Overlay — combinação de segurança + finanças.
 */
data class RideAnalysis(
    val offer: RideOffer,
    val security: SecurityResult,
    val financial: FinancialResult,
    val overlayStatus: OverlayStatus,
    val metricSignals: MetricSignals,
    val analysisTimeMs: Long = 0     // Tempo de análise em ms (meta: < 1500ms)
)

/**
 * Status do fundo do card — baseado EXCLUSIVAMENTE em segurança geográfica.
 * A avaliação financeira é expressa apenas pelos [MetricSignals] (sinaleiros individuais).
 *
 * GREEN  → SecurityResult.Safe    (destino seguro)
 * YELLOW → SecurityResult.Warning (dentro do buffer de 500 m de área de risco)
 * RED    → SecurityResult.Danger  (destino dentro de área de risco)
 */
enum class OverlayStatus {
    GREEN,   // Destino Seguro
    YELLOW,  // Zona de Atenção (buffer 500 m)
    RED      // Área de Risco (dentro da zona — vibração ativa)
}

/** Avaliação de uma métrica financeira individual contra as metas do motorista. */
enum class MetricSignalLevel { GOOD, NEUTRAL, BAD }

/**
 * Sinaleiro independente por métrica — desacoplado do risco geográfico.
 * Calculado pelo [com.radarcarioca.financial.FinancialCalculator.evaluateMetricSignals]
 * e incluído em [RideAnalysis] para que a camada de UI apenas faça mapeamento de cor.
 */
data class MetricSignals(
    val profitPerKm: MetricSignalLevel,
    val profitPerMinute: MetricSignalLevel,
    val profitPerHour: MetricSignalLevel
)

// ═══════════════════════════════════════════════════════════════════
// DriverConfig e FuelType → movidos para domain.model.DriverConfig
// ═══════════════════════════════════════════════════════════════════

// ═══════════════════════════════════════════════════════════════════
// ESTATÍSTICAS DO TURNO
// ═══════════════════════════════════════════════════════════════════

/**
 * Entidade de domínio pura para histórico de corridas — sem anotações Room.
 */
data class RideRecord(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val destinationText: String,
    val fareValue: Double,
    val netProfit: Double,
    val profitPerKm: Double,
    val wasAccepted: Boolean,
    val hadSecurityAlert: Boolean,
    val securityZoneName: String = "",
    val sourceApp: String = "uber"
)
