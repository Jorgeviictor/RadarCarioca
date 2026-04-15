package com.radarcarioca.data.local

import androidx.room.*
import com.radarcarioca.data.local.entity.AlertEntity
import com.radarcarioca.data.local.entity.GeoFeatureEntity
import com.radarcarioca.data.local.entity.RideRecordEntity
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════
// GEO FEATURE DAO
// ═══════════════════════════════════════════════════════════════════

@Dao
interface GeoFeatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(features: List<GeoFeatureEntity>)

    @Query("SELECT * FROM geo_features")
    suspend fun getAllFeatures(): List<GeoFeatureEntity>

    @Query("""
        SELECT * FROM geo_features
        WHERE centerLat BETWEEN :lat - 0.05 AND :lat + 0.05
          AND centerLng BETWEEN :lng - 0.05 AND :lng + 0.05
    """)
    suspend fun getFeaturesNearby(lat: Double, lng: Double): List<GeoFeatureEntity>

    @Query("SELECT COUNT(*) FROM geo_features")
    suspend fun count(): Int

    @Query("DELETE FROM geo_features")
    suspend fun deleteAll()

    @Query("SELECT DISTINCT area FROM geo_features WHERE area != ''")
    fun getAllAreas(): Flow<List<String>>
}


@Dao
interface RideHistoryDao {

    @Insert
    suspend fun insert(record: RideRecordEntity)

    @Query("SELECT * FROM ride_history ORDER BY timestamp DESC LIMIT 100")
    fun getRecentRides(): Flow<List<RideRecordEntity>>

    @Query("SELECT * FROM ride_history WHERE timestamp > :since ORDER BY timestamp DESC")
    fun getRidesSince(since: Long): Flow<List<RideRecordEntity>>

    @Query("SELECT SUM(netProfit) FROM ride_history WHERE wasAccepted = 1 AND timestamp > :since")
    fun getTotalProfit(since: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM ride_history WHERE wasAccepted = 1 AND timestamp > :since")
    fun getAcceptedCount(since: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM ride_history WHERE wasAccepted = 0 AND timestamp > :since")
    fun getRejectedCount(since: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM ride_history WHERE hadSecurityAlert = 1 AND timestamp > :since")
    fun getAlertCount(since: Long): Flow<Int>

    @Query("DELETE FROM ride_history WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}


// ═══════════════════════════════════════════════════════════════════
// ALERT DAO — Alertas de Segurança Dinâmicos (Firebase → Room cache)
// ═══════════════════════════════════════════════════════════════════

@Dao
interface AlertDao {

    /**
     * Upsert idempotente: re-inserir o mesmo alerta do Firebase atualiza os campos
     * sem duplicar registros (PrimaryKey = ID do Firebase).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(alerts: List<AlertEntity>)

    /** Flow de todos os alertas ativos — atualiza automaticamente ao gravar. */
    @Query("SELECT * FROM security_alerts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActiveAlerts(): Flow<List<AlertEntity>>

    /**
     * Alertas ativos dentro de uma bounding box.
     * Usa índice composto (isActive, lat, lng) — operação de ~1ms em produção.
     */
    @Query("""
        SELECT * FROM security_alerts
        WHERE isActive = 1
          AND lat BETWEEN :latMin AND :latMax
          AND lng BETWEEN :lngMin AND :lngMax
        ORDER BY createdAt DESC
    """)
    fun observeNearbyActiveAlerts(
        latMin: Double,
        latMax: Double,
        lngMin: Double,
        lngMax: Double
    ): Flow<List<AlertEntity>>

    @Query("SELECT * FROM security_alerts WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<AlertEntity?>

    /**
     * Remove alertas com expiresAt anterior ao timestamp informado.
     * expiresAt IS NULL preserva alertas sem data de expiração definida.
     */
    @Query("DELETE FROM security_alerts WHERE expiresAt IS NOT NULL AND expiresAt < :beforeTimestamp")
    suspend fun deleteExpired(beforeTimestamp: Long)

    @Query("DELETE FROM security_alerts")
    suspend fun deleteAll()
}


@Database(
    entities = [
        GeoFeatureEntity::class,
        RideRecordEntity::class,
        AlertEntity::class          // v2: adicionado para alertas dinâmicos Firebase
    ],
    version = 2,
    exportSchema = false
)
abstract class RadarDatabase : RoomDatabase() {
    abstract fun geoFeatureDao(): GeoFeatureDao
    abstract fun rideHistoryDao(): RideHistoryDao
    abstract fun alertDao(): AlertDao
}
