package com.radarcarioca.data.local.datasource

import com.radarcarioca.data.local.AlertDao
import com.radarcarioca.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação Room de [AlertLocalDataSource].
 *
 * Delegação simples ao [AlertDao] — qualquer lógica além de CRUD
 * (ex.: filtros de TTL) pertence aqui, não no DAO.
 */
@Singleton
class AlertLocalDataSourceImpl @Inject constructor(
    private val dao: AlertDao
) : AlertLocalDataSource {

    override fun observeActiveAlerts(): Flow<List<AlertEntity>> =
        dao.observeActiveAlerts()

    override fun observeNearbyAlerts(
        lat: Double,
        lng: Double,
        latDelta: Double,
        lngDelta: Double
    ): Flow<List<AlertEntity>> =
        dao.observeNearbyActiveAlerts(
            latMin = lat - latDelta,
            latMax = lat + latDelta,
            lngMin = lng - lngDelta,
            lngMax = lng + lngDelta
        )

    override fun observeAlertById(id: String): Flow<AlertEntity?> =
        dao.observeById(id)

    override suspend fun upsertAlerts(alerts: List<AlertEntity>) =
        dao.upsertAll(alerts)

    override suspend fun deleteExpiredAlerts(beforeTimestamp: Long) =
        dao.deleteExpired(beforeTimestamp)

    override suspend fun deleteAll() =
        dao.deleteAll()
}
