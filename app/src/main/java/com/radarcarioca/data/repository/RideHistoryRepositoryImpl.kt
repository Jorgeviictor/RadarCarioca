package com.radarcarioca.data.repository

import com.radarcarioca.data.local.RideHistoryDao
import com.radarcarioca.data.mapper.toDomain
import com.radarcarioca.data.mapper.toEntity
import com.radarcarioca.data.model.RideRecord
import com.radarcarioca.domain.repository.RideHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta de RideHistoryRepository.
 * Converte RideRecordEntity ↔ RideRecord via mapper.
 * ViewModel e Use Cases dependem apenas da interface.
 */
@Singleton
class RideHistoryRepositoryImpl @Inject constructor(
    private val dao: RideHistoryDao
) : RideHistoryRepository {

    override suspend fun insert(record: RideRecord) =
        dao.insert(record.toEntity())

    override fun getRecentRides(): Flow<List<RideRecord>> =
        dao.getRecentRides().map { list -> list.map { it.toDomain() } }

    override fun getTotalProfit(since: Long): Flow<Double?> =
        dao.getTotalProfit(since)

    override fun getAcceptedCount(since: Long): Flow<Int> =
        dao.getAcceptedCount(since)

    override fun getRejectedCount(since: Long): Flow<Int> =
        dao.getRejectedCount(since)

    override fun getAlertCount(since: Long): Flow<Int> =
        dao.getAlertCount(since)

    override suspend fun deleteOlderThan(before: Long) =
        dao.deleteOlderThan(before)
}
