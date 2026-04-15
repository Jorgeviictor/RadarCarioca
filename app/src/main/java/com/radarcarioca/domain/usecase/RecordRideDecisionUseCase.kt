package com.radarcarioca.domain.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.data.model.RideAnalysis
import com.radarcarioca.data.model.RideRecord
import com.radarcarioca.data.model.SecurityResult
import com.radarcarioca.domain.repository.RideHistoryRepository
import javax.inject.Inject

/**
 * Registra a decisão do motorista (aceitar/recusar) para uma oferta analisada.
 *
 * Regra de negócio encapsulada: transforma [RideAnalysis] + decisão em [RideRecord]
 * pronto para persistência. Antes, essa montagem estava inline no RadarForegroundService,
 * misturando lógica de negócio com gerenciamento de ciclo de vida do serviço Android.
 *
 * Retorna [DataResult.Error] em caso de falha no banco — o caller pode logar
 * sem travar o fluxo principal.
 */
class RecordRideDecisionUseCase @Inject constructor(
    private val rideHistoryRepository: RideHistoryRepository
) {
    suspend operator fun invoke(
        analysis: RideAnalysis,
        wasAccepted: Boolean
    ): DataResult<Unit> = try {
        val record = RideRecord(
            destinationText = analysis.offer.destinationText,
            fareValue       = analysis.offer.fareValue,
            netProfit       = analysis.financial.netProfit,
            profitPerKm     = analysis.financial.profitPerKm,
            wasAccepted     = wasAccepted,
            hadSecurityAlert = analysis.security is SecurityResult.Danger,
            securityZoneName = (analysis.security as? SecurityResult.Danger)
                ?.featureName
                .orEmpty(),
            sourceApp = analysis.offer.sourceApp
        )
        rideHistoryRepository.insert(record)
        DataResult.Success(Unit)
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}
