package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.repository.DriverSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Estado unificado das preferências do motorista.
 */
data class DriverSettings(
    val config: DriverConfig = DriverConfig(),
    val isOnboardingDone: Boolean = false,
    val isRadarEnabled: Boolean = false
)

/**
 * Combina os três fluxos de preferências em um único Flow coeso.
 *
 * Antes, a ViewModel abria 3 viewModelScope.launch separados para observar
 * driverConfig, isOnboardingDone e isRadarEnabled individualmente.
 * Este UseCase os une e elimina a dependência direta da ViewModel em DriverPreferences.
 *
 * Injeção via [DriverSettingsRepository] (interface do domain) — nunca
 * via DriverPreferences (DataStore — detalhe de implementação da camada Data).
 */
class ObserveDriverSettingsUseCase @Inject constructor(
    private val driverSettingsRepository: DriverSettingsRepository
) {
    operator fun invoke(): Flow<DriverSettings> = combine(
        driverSettingsRepository.driverConfig,
        driverSettingsRepository.isOnboardingDone,
        driverSettingsRepository.isRadarEnabled
    ) { config, onboardingDone, radarEnabled ->
        DriverSettings(
            config = config,
            isOnboardingDone = onboardingDone,
            isRadarEnabled = radarEnabled
        )
    }
}
