package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.repository.RideHistoryRepository
import javax.inject.Inject

/**
 * Remove corridas mais antigas que [retentionDays] dias do histórico.
 *
 * Regra de negócio encapsulada: converte dias de retenção em timestamp Unix
 * (lógica que antes estava duplicada em cada chamador).
 *
 * Chamado pelo [RadarForegroundService] no início de cada turno e
 * opcionalmente configurável nas preferências do motorista.
 */
class PurgeOldRidesUseCase @Inject constructor(
    private val rideHistoryRepository: RideHistoryRepository
) {
    suspend operator fun invoke(retentionDays: Int) {
        require(retentionDays > 0) { "Retenção deve ser maior que zero" }
        val cutoffTimestamp = System.currentTimeMillis() - retentionDays.toDurationMs()
        rideHistoryRepository.deleteOlderThan(cutoffTimestamp)
    }

    private fun Int.toDurationMs(): Long = this * 24L * 60L * 60L * 1_000L
}
