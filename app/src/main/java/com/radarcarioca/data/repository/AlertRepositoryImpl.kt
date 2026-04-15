package com.radarcarioca.data.repository

import com.radarcarioca.core.DataResult
import com.radarcarioca.core.Result
import com.radarcarioca.data.local.datasource.AlertLocalDataSource
import com.radarcarioca.data.mapper.toDomain
import com.radarcarioca.data.mapper.toDomainList
import com.radarcarioca.data.mapper.toEntityList
import com.radarcarioca.data.remote.datasource.AlertRemoteDataSource
import com.radarcarioca.data.util.networkBoundResource
import com.radarcarioca.domain.model.SecurityAlert
import com.radarcarioca.domain.model.SecurityAlertException
import com.radarcarioca.domain.repository.AlertRepository
import com.radarcarioca.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta de [AlertRepository].
 *
 * ─── Responsabilidades ───────────────────────────────────────────────
 *  1. Orquestrar [AlertLocalDataSource] (Room) e [AlertRemoteDataSource] (Firebase).
 *  2. Aplicar estratégia de cache via [networkBoundResource] (offline-first).
 *  3. Converter Throwables genéricos em [SecurityAlertException] semânticas.
 *  4. Garantir que todo I/O rode em [ioDispatcher] — nunca na Main thread.
 *
 * ─── O que esta classe NÃO faz ───────────────────────────────────────
 *  - Não conhece Compose, ViewModel, ou qualquer API de UI.
 *  - Não toma decisões de negócio (ex.: "esse alerta bloqueia a corrida?").
 *    Isso pertence a UseCases na camada domain.
 *
 * ─── Cálculo geoespacial ─────────────────────────────────────────────
 *  1° latitude ≈ 111 km → 1 km ≈ 0.009°
 *  Bounding box simples — suficiente para alertas urbanos no RJ (~20 km de raio).
 *  Para precisão geodésica real, usar Haversine (já disponível em GeoSecurityManager).
 */
@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val localDataSource: AlertLocalDataSource,
    private val remoteDataSource: AlertRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AlertRepository {

    // ─────────────────────────────────────────────────────────────────
    // Alertas ativos — NetworkBoundResource: cache-first + refresh Firebase
    // ─────────────────────────────────────────────────────────────────

    override fun getActiveAlerts(): Flow<Result<List<SecurityAlert>>> =
        networkBoundResource(
            query           = { localDataSource.observeActiveAlerts() },
            fetch           = { remoteDataSource.fetchActiveAlerts() },
            saveFetchResult = { dtos -> localDataSource.upsertAlerts(dtos.toEntityList()) },
            mapToDomain     = { entities -> entities.toDomainList() },
            shouldFetch     = { true },
            onFetchFailed   = { /* Crashlytics / Analytics aqui */ }
        ).flowOn(ioDispatcher)

    // ─────────────────────────────────────────────────────────────────
    // Alertas por proximidade — RadarForegroundService / CheckRideSafetyUseCase
    // ─────────────────────────────────────────────────────────────────

    override fun getNearbyAlerts(
        lat: Double,
        lng: Double,
        radiusKm: Double
    ): Flow<Result<List<SecurityAlert>>> {
        val delta = radiusKm * KM_TO_DEGREES
        return networkBoundResource(
            query           = {
                localDataSource.observeNearbyAlerts(
                    lat      = lat,
                    lng      = lng,
                    latDelta = delta,
                    lngDelta = delta
                )
            },
            fetch           = { remoteDataSource.fetchActiveAlerts() },
            saveFetchResult = { dtos ->
                // Persiste todos; o DAO filtra pelo raio via SQL
                localDataSource.upsertAlerts(dtos.toEntityList())
            },
            mapToDomain     = { entities -> entities.toDomainList() },
            shouldFetch     = { cached ->
                cached.isNullOrEmpty() ||
                    cached.any { System.currentTimeMillis() - it.cachedAt > CACHE_TTL_MS }
            }
        ).flowOn(ioDispatcher)
    }

    // ─────────────────────────────────────────────────────────────────
    // Refresh forçado — pull-to-refresh no Dashboard / WorkManager
    // ─────────────────────────────────────────────────────────────────

    override suspend fun refreshAlerts(): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val dtos = remoteDataSource.fetchActiveAlerts()
                localDataSource.upsertAlerts(dtos.toEntityList())
                DataResult.Success(Unit)
            }.getOrElse { throwable ->
                DataResult.Error(throwable.toSecurityAlertException())
            }
        }

    // ─────────────────────────────────────────────────────────────────
    // Observação por ID
    // ─────────────────────────────────────────────────────────────────

    override fun observeAlertById(id: String): Flow<SecurityAlert?> =
        localDataSource.observeAlertById(id)
            .map { entity -> entity?.toDomain() }
            .flowOn(ioDispatcher)

    // ─────────────────────────────────────────────────────────────────
    // Purge de cache expirado
    // ─────────────────────────────────────────────────────────────────

    override suspend fun purgeExpiredAlerts(): Unit =
        withContext(ioDispatcher) {
            localDataSource.deleteExpiredAlerts(beforeTimestamp = System.currentTimeMillis())
        }

    // ─────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────

    /**
     * Converte qualquer [Throwable] para [SecurityAlertException] tipada.
     * Exceções já tipadas não são re-embaladas, preservando o subtipo original.
     */
    private fun Throwable.toSecurityAlertException(): SecurityAlertException = when (this) {
        is SecurityAlertException -> this
        else -> SecurityAlertException.RemoteUnavailable(
            message = message ?: "Erro desconhecido ao sincronizar alertas.",
            cause   = this
        )
    }

    private companion object {
        /** 1 km ≈ 0.009° (latitude ~23°S — Rio de Janeiro) */
        const val KM_TO_DEGREES = 0.009

        /** Cache considerado fresco por 5 minutos — balanceia tráfego vs atualidade */
        const val CACHE_TTL_MS = 5 * 60 * 1_000L
    }
}
