package com.radarcarioca.domain.usecase

import com.radarcarioca.data.model.RideRecord
import com.radarcarioca.domain.repository.RideHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retorna o fluxo reativo das corridas recentes para exibição no StatsScreen.
 *
 * Usa Room como Single Source of Truth: qualquer inserção via [RecordRideDecisionUseCase]
 * dispara automaticamente uma nova emissão para todos os coletores.
 */
class GetRecentRidesUseCase @Inject constructor(
    private val rideHistoryRepository: RideHistoryRepository
) {
    operator fun invoke(): Flow<List<RideRecord>> =
        rideHistoryRepository.getRecentRides()
}
