package com.radarcarioca.financial

import com.radarcarioca.data.model.*
import com.radarcarioca.data.model.MetricSignalLevel.*
import com.radarcarioca.domain.model.DriverConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ═══════════════════════════════════════════════════════════════════
 * MOTOR DE CÁLCULO FINANCEIRO
 * ═══════════════════════════════════════════════════════════════════
 *
 * Calcula o lucro REAL de cada corrida levando em conta:
 * - Taxa da plataforma (Uber/99 — padrão 20%)
 * - Custo de combustível (corrida + deslocamento até o passageiro)
 * - O "custo oculto" do deadhead (motorista não recebe por esse trecho)
 *
 * Diferencial: inclui o deslocamento até o passageiro no custo,
 * exatamente como fazem os apps top de linha (DSW, etc.)
 */
@Singleton
class FinancialCalculator @Inject constructor() {

    /**
     * Calcula o resultado financeiro completo de uma oferta.
     *
     * @param offer Dados da oferta capturada pelo AccessibilityService
     * @param config Configurações do motorista (combustível, metas)
     */
    fun calculate(offer: RideOffer, config: DriverConfig): FinancialResult {
        // Distância total = corrida + deslocamento até o passageiro
        val totalDistanceKm = offer.rideDistanceKm + offer.deadheadDistanceKm

        // Custo de combustível para todo o percurso
        // Guard: kmPerLiter = 0 (campo zerado pelo usuário) → custo = 0 para não crashar
        val fuelCost = if (config.kmPerLiter > 0.0)
            (totalDistanceKm / config.kmPerLiter) * config.fuelPricePerLiter
        else 0.0

        // Desconto da plataforma sobre o valor bruto
        val platformCut = offer.fareValue * config.platformFeePercent

        // Receita líquida (após taxa da plataforma)
        val netRevenue = offer.fareValue - platformCut

        // Lucro líquido real
        val netProfit = netRevenue - fuelCost

        // R$ por KM rodado (apenas a corrida, não o deadhead)
        val profitPerKm = if (offer.rideDistanceKm > 0)
            netProfit / offer.rideDistanceKm else 0.0

        // Estimativa de tempo total em horas (velocidade média 30 KM/H no Rio)
        val estimatedHours = totalDistanceKm / 30.0

        // R$ por hora estimado
        val profitPerHour = if (estimatedHours > 0)
            netProfit / estimatedHours else 0.0

        // Margem de lucro em % sobre o valor bruto da corrida
        val profitMarginPercent = if (offer.fareValue > 0)
            (netProfit / offer.fareValue) * 100.0 else 0.0

        // Lucro por minuto
        val estimatedMinutesTotal = if (offer.estimatedMinutes > 0) offer.estimatedMinutes.toDouble()
            else estimatedHours * 60.0
        val profitPerMinute = if (estimatedMinutesTotal > 0)
            netProfit / estimatedMinutesTotal else 0.0

        return FinancialResult(
            netProfit = netProfit,
            profitPerKm = profitPerKm,
            profitPerHour = profitPerHour,
            fuelCost = fuelCost,
            platformCut = platformCut,
            netRevenue = netRevenue,
            totalDistanceKm = totalDistanceKm,
            netProfitPerHour = profitPerHour,
            profitMarginPercent = profitMarginPercent,
            profitPerMinute = profitPerMinute
        )
    }

    /**
     * Avalia o sinal de cada métrica financeira em relação às metas do motorista.
     *
     * Thresholds (regras de negócio — mantidos aqui, fora da camada de UI):
     * - GOOD    ≥ 110% da meta
     * - NEUTRAL  75% – 109% da meta (ou faixa fixa para R$/min)
     * - BAD     < 75% da meta
     */
    /**
     * Avalia cada métrica financeira contra os benchmarks definidos pelo motorista.
     *
     * Thresholds (regra de negócio):
     * - GOOD    ≥ 100% da meta (atingiu ou superou)
     * - NEUTRAL  80% – 99% da meta (margem de 20% → "aceitável, mas fique de olho")
     * - BAD     < 80% da meta (muito abaixo — recusar)
     *
     * A margem de 20% para NEUTRAL foi escolhida porque variações de tráfego
     * no Rio podem reduzir até 15% o ganho efetivo sem que a corrida seja ruim.
     * Uma margem menor (ex: 10%) geraria falsos negativos em hora de pico.
     */
    fun evaluateMetricSignals(result: FinancialResult, config: DriverConfig): MetricSignals =
        MetricSignals(
            profitPerKm = when {
                result.profitPerKm >= config.targetProfitPerKm           -> GOOD
                result.profitPerKm >= config.targetProfitPerKm * 0.80    -> NEUTRAL
                else -> BAD
            },
            profitPerMinute = when {
                result.profitPerMinute >= config.targetProfitPerMinute          -> GOOD
                result.profitPerMinute >= config.targetProfitPerMinute * 0.80   -> NEUTRAL
                else -> BAD
            },
            profitPerHour = when {
                result.profitPerHour >= config.targetProfitPerHour          -> GOOD
                result.profitPerHour >= config.targetProfitPerHour * 0.80   -> NEUTRAL
                else -> BAD
            }
        )

    /**
     * Determina o status do fundo do card baseado EXCLUSIVAMENTE na segurança geográfica.
     *
     * A avaliação financeira NÃO influencia a cor do fundo — ela é expressa apenas
     * pelos sinaleiros individuais (MetricSignals) de cada métrica.
     *
     * Isso garante que o motorista leia dois canais visuais independentes:
     *   - Fundo do card → "Esse destino é seguro?"
     *   - Sinaleiros    → "Essa corrida é lucrativa?"
     */
    fun determineOverlayStatus(security: SecurityResult): OverlayStatus = when (security) {
        is SecurityResult.Danger  -> OverlayStatus.RED     // Dentro da área → VERMELHO + vibração
        is SecurityResult.Warning -> OverlayStatus.YELLOW  // Buffer 500 m → AMARELO
        is SecurityResult.Safe    -> OverlayStatus.GREEN   // Destino seguro → VERDE
    }
}
