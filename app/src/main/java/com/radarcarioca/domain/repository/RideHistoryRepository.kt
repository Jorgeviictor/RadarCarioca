package com.radarcarioca.domain.repository

import com.radarcarioca.data.model.RideRecord
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso ao histórico de corridas.
 * O ViewModel depende desta interface, nunca do DAO diretamente.
 */
interface RideHistoryRepository {
    suspend fun insert(record: RideRecord)
    fun getRecentRides(): Flow<List<RideRecord>>
    fun getTotalProfit(since: Long): Flow<Double?>
    fun getAcceptedCount(since: Long): Flow<Int>
    fun getRejectedCount(since: Long): Flow<Int>
    fun getAlertCount(since: Long): Flow<Int>
    suspend fun deleteOlderThan(before: Long)
}
