package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.repository.RideHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

/**
 * Estatísticas acumuladas desde meia-noite do dia corrente.
 */
data class TodayStats(
    val earnings: Double = 0.0,
    val acceptedCount: Int = 0,
    val rejectedCount: Int = 0,
    val alertCount: Int = 0
)

/**
 * Retorna um Flow reativo com as estatísticas do turno atual.
 *
 * Regra de negócio encapsulada: o "turno" começa à meia-noite do dia corrente.
 * Antes, esse cálculo estava espalhado em 4 viewModelScope.launch separados na ViewModel.
 */
class GetTodayStatsUseCase @Inject constructor(
    private val rideHistoryRepository: RideHistoryRepository
) {
    operator fun invoke(): Flow<TodayStats> {
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return combine(
            rideHistoryRepository.getTotalProfit(midnight),
            rideHistoryRepository.getAcceptedCount(midnight),
            rideHistoryRepository.getRejectedCount(midnight),
            rideHistoryRepository.getAlertCount(midnight)
        ) { profit, accepted, rejected, alerts ->
            TodayStats(
                earnings = profit ?: 0.0,
                acceptedCount = accepted,
                rejectedCount = rejected,
                alertCount = alerts
            )
        }
    }
}
