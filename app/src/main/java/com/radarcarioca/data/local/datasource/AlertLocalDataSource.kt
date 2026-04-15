package com.radarcarioca.data.local.datasource

import com.radarcarioca.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da fonte de dados local para alertas de segurança.
 *
 * Retorna [AlertEntity] (modelo Room), nunca modelos de domínio.
 * O [AlertRepositoryImpl] é responsável pela conversão para [Alert].
 *
 * Separar esta interface de [AlertDao] permite:
 *  1. Adicionar lógica de negócio local (ex.: filtros, TTL) sem poluir o DAO.
 *  2. Criar um fake em testes sem depender de Room.
 */
interface AlertLocalDataSource {

    /** Flow de todos os alertas ativos persistidos no Room. */
    fun observeActiveAlerts(): Flow<List<AlertEntity>>

    /**
     * Alertas ativos dentro da bounding box definida por [latDelta]/[lngDelta]
     * a partir do ponto [lat]/[lng].
     * Usa índice Room — operação de ~1ms mesmo com centenas de registros.
     */
    fun observeNearbyAlerts(
        lat: Double,
        lng: Double,
        latDelta: Double,
        lngDelta: Double
    ): Flow<List<AlertEntity>>

    /** Retorna um alerta específico por ID, ou null se não existir. */
    fun observeAlertById(id: String): Flow<AlertEntity?>

    /**
     * Faz upsert de uma lista de alertas.
     * OnConflictStrategy.REPLACE garante idempotência: re-inserir o mesmo
     * alerta atualiza os campos sem duplicar registros.
     */
    suspend fun upsertAlerts(alerts: List<AlertEntity>)

    /** Remove alertas cujo [expiresAt] seja anterior a [beforeTimestamp]. */
    suspend fun deleteExpiredAlerts(beforeTimestamp: Long)

    /** Remove todos os alertas (usado no logout ou reset de dados). */
    suspend fun deleteAll()
}
