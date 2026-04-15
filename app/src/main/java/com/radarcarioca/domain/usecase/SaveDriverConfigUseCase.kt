package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.repository.DriverSettingsRepository
import javax.inject.Inject

/**
 * Persiste as configurações do motorista (combustível, metas, etc.).
 *
 * Injeção via [DriverSettingsRepository] (interface do domain) — nunca
 * via DriverPreferences (DataStore — detalhe de implementação da camada Data).
 */
class SaveDriverConfigUseCase @Inject constructor(
    private val driverSettingsRepository: DriverSettingsRepository
) {
    suspend operator fun invoke(config: DriverConfig) {
        driverSettingsRepository.saveConfig(config)
    }
}
