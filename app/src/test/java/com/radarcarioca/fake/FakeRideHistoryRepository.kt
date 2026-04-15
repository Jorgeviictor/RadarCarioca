package com.radarcarioca.fake

import com.radarcarioca.data.model.RideRecord
import com.radarcarioca.domain.repository.RideHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Repositório fake para testes unitários de estatísticas e ViewModel.
 */
class FakeRideHistoryRepository : RideHistoryRepository {

    private val records = mutableListOf<RideRecord>()

    override suspend fun insert(record: RideRecord) {
        records.add(record)
    }

    override fun getRecentRides(): Flow<List<RideRecord>> =
        flowOf(records.sortedByDescending { it.timestamp }.take(100))

    override fun getTotalProfit(since: Long): Flow<Double?> =
        flowOf(records.filter { it.wasAccepted && it.timestamp > since }.sumOf { it.netProfit })

    override fun getAcceptedCount(since: Long): Flow<Int> =
        flowOf(records.count { it.wasAccepted && it.timestamp > since })

    override fun getRejectedCount(since: Long): Flow<Int> =
        flowOf(records.count { !it.wasAccepted && it.timestamp > since })

    override fun getAlertCount(since: Long): Flow<Int> =
        flowOf(records.count { it.hadSecurityAlert && it.timestamp > since })

    override suspend fun deleteOlderThan(before: Long) {
        records.removeAll { it.timestamp < before }
    }
}
