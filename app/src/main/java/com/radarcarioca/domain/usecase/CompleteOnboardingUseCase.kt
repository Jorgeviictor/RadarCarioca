package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.repository.DriverSettingsRepository
import javax.inject.Inject

/**
 * Marca o onboarding como concluído de forma permanente.
 *
 * Injeção via [DriverSettingsRepository] (interface do domain) — nunca
 * via DriverPreferences (DataStore — detalhe de implementação da camada Data).
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val driverSettingsRepository: DriverSettingsRepository
) {
    suspend operator fun invoke() {
        driverSettingsRepository.setOnboardingDone(true)
    }
}
