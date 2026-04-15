package com.radarcarioca.fake

import com.radarcarioca.core.DataResult
import com.radarcarioca.core.Result
import com.radarcarioca.domain.model.SecurityAlert
import com.radarcarioca.domain.model.SecurityAlertException
import com.radarcarioca.domain.model.SecurityAlertSeverity
import com.radarcarioca.domain.model.SecurityAlertSource
import com.radarcarioca.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fake in-memory de [AlertRepository] para testes unitários.
 *
 * Sem Room, sem Firebase, sem emulador — roda em JVM puro.
 * Cobre todos os cenários relevantes ao domínio do Radar Carioca:
 *
 * ```kotlin
 * // Cenário 1: alertas disponíveis normalmente
 * val repo = FakeAlertRepository(listOf(fakeAlert()))
 * repo.getActiveAlerts().test {
 *     assertIs<DataResult.Success>(awaitItem())
 * }
 *
 * // Cenário 2: motorista sem sinal em zona de risco
 * val repo = FakeAlertRepository(shouldFailRefresh = true)
 * assertIs<DataResult.Error>(repo.refreshAlerts())
 *
 * // Cenário 3: alerta crítico aparece em tempo real
 * val repo = FakeAlertRepository()
 * launch { repo.getActiveAlerts().collect { /* assert */ } }
 * repo.add(fakeAlert(severity = SecurityAlertSeverity.CRITICAL))
 * ```
 */
class FakeAlertRepository(
    initialAlerts: List<SecurityAlert> = emptyList(),
    private var shouldFailRefresh: Boolean = false
) : AlertRepository {

    private val alertsFlow = MutableStateFlow(initialAlerts)

    // ─────────────────────────────────────────────────────────────────
    // API de controle para testes
    // ─────────────────────────────────────────────────────────────────

    fun emit(alerts: List<SecurityAlert>) { alertsFlow.value = alerts }

    fun add(alert: SecurityAlert) { alertsFlow.update { it + alert } }

    fun setRefreshFailure(fail: Boolean) { shouldFailRefresh = fail }

    // ─────────────────────────────────────────────────────────────────
    // Implementação de AlertRepository
    // ─────────────────────────────────────────────────────────────────

    override fun getActiveAlerts(): Flow<Result<List<SecurityAlert>>> =
        alertsFlow.map { list ->
            DataResult.Success(list.filter { it.isActive })
        }

    override fun getNearbyAlerts(
        lat: Double,
        lng: Double,
        radiusKm: Double
    ): Flow<Result<List<SecurityAlert>>> =
        alertsFlow.map { list ->
            val delta = radiusKm * 0.009
            DataResult.Success(
                list.filter { alert ->
                    alert.isActive &&
                        kotlin.math.abs(alert.lat - lat) <= delta &&
                        kotlin.math.abs(alert.lng - lng) <= delta
                }
            )
        }

    override suspend fun refreshAlerts(): Result<Unit> =
        if (shouldFailRefresh) DataResult.Error(SecurityAlertException.NetworkUnavailable())
        else DataResult.Success(Unit)

    override fun observeAlertById(id: String): Flow<SecurityAlert?> =
        alertsFlow.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun purgeExpiredAlerts() {
        val now = System.currentTimeMillis()
        alertsFlow.update { list ->
            list.filter { alert -> alert.expiresAt == null || alert.expiresAt > now }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Factory para testes — evita boilerplate nos test files
// ═══════════════════════════════════════════════════════════════════

/**
 * Cria um [SecurityAlert] com valores padrão sensíveis ao contexto do Radar Carioca.
 * Sobrescreva apenas os campos relevantes ao cenário do teste.
 */
fun fakeSecurityAlert(
    id: String = "alert_001",
    title: String = "Operação Policial — Área de Teste",
    areaName: String = "Complexo da Maré",
    lat: Double = -22.8901,
    lng: Double = -43.2491,
    radiusMeters: Int = 300,
    severity: SecurityAlertSeverity = SecurityAlertSeverity.HIGH,
    source: SecurityAlertSource = SecurityAlertSource.FIREBASE,
    isActive: Boolean = true,
    expiresAt: Long? = null
): SecurityAlert = SecurityAlert(
    id           = id,
    title        = title,
    description  = "Alerta de teste gerado por fakeSecurityAlert().",
    areaName     = areaName,
    lat          = lat,
    lng          = lng,
    radiusMeters = radiusMeters,
    severity     = severity,
    source       = source,
    isActive     = isActive,
    createdAt    = System.currentTimeMillis(),
    expiresAt    = expiresAt,
    cachedAt     = System.currentTimeMillis()
)
