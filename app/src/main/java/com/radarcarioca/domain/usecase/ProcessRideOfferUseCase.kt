package com.radarcarioca.domain.usecase

import android.util.Log
import com.radarcarioca.core.DataResult
import com.radarcarioca.data.local.DriverPreferences
import com.radarcarioca.data.model.RideAnalysis
import com.radarcarioca.data.model.RideOffer
import com.radarcarioca.data.model.SecurityResult
import com.radarcarioca.financial.FinancialCalculator
import com.radarcarioca.service.GeocodingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProcessRideOfferUseCase"

/**
 * Use Case principal: orquestra a análise completa de uma oferta de corrida.
 *
 * Fluxo:
 *   1. Lê configurações do motorista (DataStore)
 *   2. Geocodifica o endereço de destino (Google Maps API + fallback offline)
 *   3. Verifica segurança geográfica (GeoJSON + CheckRideSafetyUseCase)
 *   4. Fallback por palavra-chave quando geocodificação falha (sem internet)
 *   5. Calcula lucratividade (CalculateRideProfitUseCase)
 *   6. Determina status do overlay (GREEN=seguro / YELLOW=atenção / RED=risco) — só geo
 *
 * Retorna [DataResult.Success] com a análise ou [DataResult.Error] sem lançar exceção
 * — o caller (RadarForegroundService) decide como tratar a falha sem travar o fluxo.
 *
 * Meta de performance: análise completa < 1500 ms.
 */
@Singleton
class ProcessRideOfferUseCase @Inject constructor(
    private val checkSafety: CheckRideSafetyUseCase,
    private val calculateProfit: CalculateRideProfitUseCase,
    private val financialCalculator: FinancialCalculator,
    private val driverPreferences: DriverPreferences,
    private val geocodingService: GeocodingService
) {
    suspend operator fun invoke(offer: RideOffer): DataResult<RideAnalysis> =
        withContext(Dispatchers.Default) {
            try {
                val startTime = System.currentTimeMillis()
                val config = driverPreferences.driverConfig.first()

                val (lat, lng) = geocodingService.geocode(offer.destinationText)
                val enrichedOffer = offer.copy(destinationLat = lat, destinationLng = lng)

                // Quando geocodificação falha (lat=0, lng=0), usamos busca por palavra-chave
                // como fallback offline — crítico para zonas sem 4G no Rio.
                val security = if (lat != 0.0 && lng != 0.0) {
                    checkSafety(lat, lng, config.bufferMeters)
                } else {
                    checkSafetyByKeyword(offer.destinationText)
                }

                val financial = calculateProfit(enrichedOffer, config)
                val status = financialCalculator.determineOverlayStatus(security)
                val metricSignals = financialCalculator.evaluateMetricSignals(financial, config)
                val elapsed = System.currentTimeMillis() - startTime

                Log.i(TAG, "Análise completa em ${elapsed}ms — Status: $status")

                DataResult.Success(
                    RideAnalysis(
                        offer = enrichedOffer,
                        security = security,
                        financial = financial,
                        overlayStatus = status,
                        metricSignals = metricSignals,
                        analysisTimeMs = elapsed
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Falha na análise da oferta '${offer.destinationText}': ${e.message}", e)
                DataResult.Error(e)
            }
        }

    companion object {
        /**
         * Mapeamento offline de destinos de risco do Rio por palavras-chave.
         * Acionado como fallback quando a geocodificação falha por falta de internet.
         * Mantido como companion para ser testável de forma isolada.
         */
        private val RISK_KEYWORDS: Map<String, String> = mapOf(
            "chapadão"         to "Complexo do Chapadão",
            "acari"            to "Complexo do Chapadão",
            "maré"             to "Complexo da Maré",
            "nova holanda"     to "Complexo da Maré",
            "rubens vaz"       to "Complexo da Maré",
            "vila ruth"        to "Complexo de Mali",
            "morro do amor"    to "Complexo de Mali",
            "jacarezinho"      to "Zona Norte",
            "alemão"           to "Complexo do Alemão",
            "rocinha"          to "Zona Sul",
            "castelar"         to "Baixada Fluminense",
            "morro do rola"    to "Santa Cruz",
            "barreira do vasco" to "São Cristóvão"
        )

        fun checkSafetyByKeyword(destinationText: String): SecurityResult {
            val lower = destinationText.lowercase()
            for ((keyword, area) in RISK_KEYWORDS) {
                if (lower.contains(keyword)) {
                    return SecurityResult.Danger(
                        featureName  = keyword.replaceFirstChar { it.uppercase() },
                        areaName     = area,
                        distanceMeters = 0
                    )
                }
            }
            return SecurityResult.Safe
        }
    }
}
