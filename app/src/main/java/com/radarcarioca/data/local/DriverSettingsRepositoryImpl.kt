package com.radarcarioca.data.local

import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.repository.DriverSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação de [DriverSettingsRepository] que delega ao [DriverPreferences] (DataStore).
 *
 * Esta classe existe para satisfazer a Regra de Dependência da Clean Architecture:
 * UseCases do domain injetam a interface, nunca esta implementação concreta.
 * O vínculo interface ↔ implementação é feito no [com.radarcarioca.di.RepositoryModule].
 *
 * Delegação pura — nenhuma lógica de negócio aqui.
 * Transformações ou regras (ex.: valor padrão de configuração) pertencem aos UseCases.
 */
@Singleton
class DriverSettingsRepositoryImpl @Inject constructor(
    private val driverPreferences: DriverPreferences
) : DriverSettingsRepository {

    override val driverConfig: Flow<DriverConfig>
        get() = driverPreferences.driverConfig

    override val isOnboardingDone: Flow<Boolean>
        get() = driverPreferences.isOnboardingDone

    override val isRadarEnabled: Flow<Boolean>
        get() = driverPreferences.isRadarEnabled

    override suspend fun saveConfig(config: DriverConfig) =
        driverPreferences.saveConfig(config)

    override suspend fun setRadarEnabled(enabled: Boolean) =
        driverPreferences.setRadarEnabled(enabled)

    override suspend fun setOnboardingDone(done: Boolean) =
        driverPreferences.setOnboardingDone(done)
}
