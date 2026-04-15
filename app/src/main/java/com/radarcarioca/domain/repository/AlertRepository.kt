package com.radarcarioca.domain.repository

import com.radarcarioca.core.DataResult
import com.radarcarioca.core.Result
import com.radarcarioca.domain.model.SecurityAlert
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da camada de domínio para alertas de segurança dinâmicos.
 *
 * ─── Inversão de Dependência (SOLID-D) ───────────────────────────────
 * O Domain define ESTE contrato.
 * A camada Data implementa em [com.radarcarioca.data.repository.AlertRepositoryImpl].
 * ViewModels e UseCases dependem APENAS desta interface — nunca das implementações.
 *
 * ─── Por que Flow<Result<T>> e não suspend? ───────────────────────────
 * A assinatura Flow<Result<List<SecurityAlert>>> permite emitir a sequência:
 *   Loading(cache) → Success(fresh) em tempo real, sem polling.
 * Essencial para o padrão offline-first: o motorista vê os alertas mesmo sem
 * conexão (Loading com dados cacheados), e recebe atualização assim que o
 * Firebase responde.
 *
 * ─── Nomenclatura ─────────────────────────────────────────────────────
 * [Result] é o typealias de [DataResult] definido em core/Resource.kt.
 * Evitamos kotlin.Result porque ele não possui estado Loading.
 */
interface AlertRepository {

    /**
     * Fluxo de todos os alertas ativos.
     *
     * Estratégia de cache (NetworkBoundResource):
     *  1. Emite [DataResult.Loading] com dados do Room (se houver).
     *  2. Faz fetch no Firebase e persiste no Room.
     *  3. Emite [DataResult.Success] com os dados frescos via Room (Single Source of Truth).
     *  4. Em falha de rede, emite [DataResult.Error] com [DataResult.Error.cachedData]
     *     preenchido — motorista não fica sem informação.
     */
    fun getActiveAlerts(): Flow<Result<List<SecurityAlert>>>

    /**
     * Alertas ativos dentro de [radiusKm] km a partir de [lat]/[lng].
     *
     * Chamado pelo [com.radarcarioca.service.RadarForegroundService] ao capturar
     * o destino de uma corrida — verifica colisão com zonas de alerta ativas.
     * Cache local é consultado primeiro; fetch remoto só ocorre se o cache
     * estiver vazio ou expirado (TTL de 5 minutos).
     */
    fun getNearbyAlerts(
        lat: Double,
        lng: Double,
        radiusKm: Double = 1.0
    ): Flow<Result<List<SecurityAlert>>>

    /**
     * Força sincronização pontual com o Firebase e persiste no Room.
     * Retorna [DataResult.Success] ou [DataResult.Error] — sem Loading (operação única).
     * Chamado pelo pull-to-refresh do Dashboard ou agendado via WorkManager.
     */
    suspend fun refreshAlerts(): Result<Unit>

    /**
     * Observa um alerta específico por ID.
     * Emite `null` quando o alerta é desativado ou removido do Room
     * (ex.: após expiresAt ou limpeza de cache).
     */
    fun observeAlertById(id: String): Flow<SecurityAlert?>

    /**
     * Remove do Room os alertas cujo [SecurityAlert.expiresAt] já passou.
     * Deve ser chamado periodicamente (ex.: pelo WorkManager) para manter
     * o banco enxuto e as queries performáticas.
     */
    suspend fun purgeExpiredAlerts()
}
