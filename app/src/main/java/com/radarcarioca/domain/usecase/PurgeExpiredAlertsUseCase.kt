package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.repository.AlertRepository
import javax.inject.Inject

/**
 * Remove do banco os alertas cujo prazo de validade já expirou.
 *
 * Deve ser chamado periodicamente (WorkManager ou início de turno)
 * para manter o Room enxuto e as queries do [GetActiveAlertsUseCase]
 * performáticas — sem acumular alertas obsoletos ao longo do tempo.
 */
class PurgeExpiredAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke() {
        alertRepository.purgeExpiredAlerts()
    }
}
