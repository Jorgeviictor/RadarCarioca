package com.radarcarioca.domain.repository

import com.radarcarioca.domain.model.DriverConfig
import kotlinx.coroutines.flow.Flow

/**
 * Contrato do domain para preferências persistidas do motorista.
 *
 * ─── Por que esta interface existe? ──────────────────────────────────
 * Antes, quatro UseCases importavam [com.radarcarioca.data.local.DriverPreferences]
 * diretamente, introduzindo uma dependência do Domain na camada Data.
 * Esta interface inverte essa dependência: o Domain declara o contrato,
 * a Data implementa via [com.radarcarioca.data.local.DriverSettingsRepositoryImpl].
 *
 * ─── Regra de Dependência (Clean Architecture) ───────────────────────
 * Domain → nenhuma dependência de frameworks externos ou camadas externas.
 * Qualquer UseCase agora injeta DriverSettingsRepository (esta interface),
 * não DriverPreferences (DataStore — detalhe de implementação da camada Data).
 */
interface DriverSettingsRepository {

    /** Fluxo reativo da configuração atual do motorista. */
    val driverConfig: Flow<DriverConfig>

    /** Fluxo reativo: onboarding já foi concluído? */
    val isOnboardingDone: Flow<Boolean>

    /** Fluxo reativo: radar está habilitado? */
    val isRadarEnabled: Flow<Boolean>

    /** Persiste novas configurações do motorista. */
    suspend fun saveConfig(config: DriverConfig)

    /** Atualiza o estado de habilitação do radar. */
    suspend fun setRadarEnabled(enabled: Boolean)

    /** Marca o onboarding como concluído. */
    suspend fun setOnboardingDone(done: Boolean)
}
