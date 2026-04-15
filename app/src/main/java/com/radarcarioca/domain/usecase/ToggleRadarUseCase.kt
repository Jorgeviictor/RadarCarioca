package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.repository.DriverSettingsRepository
import com.radarcarioca.domain.service.PermissionsChecker
import com.radarcarioca.domain.service.RadarController
import javax.inject.Inject

/**
 * Alterna o estado do radar: persiste no DataStore, no SharedPreferences
 * (para o BootReceiver) e aciona o ForegroundService correspondente.
 *
 * Regra de negócio encapsulada: o toggle é atômico — persistência e controle
 * do serviço acontecem juntos. A ViewModel não precisa conhecer nenhum dos
 * dois mecanismos de persistência nem as APIs de serviço Android.
 *
 * Guard de segurança: ao ativar, verifica se acessibilidade E overlay estão
 * concedidos. Se faltar qualquer um, o estado NÃO é persistido e o serviço
 * NÃO é iniciado — evita crash no FloatingButtonManager e OverlayManager.
 *
 * Injeção via [DriverSettingsRepository] (interface do domain) — nunca
 * via DriverPreferences (DataStore — detalhe de implementação da camada Data).
 *
 * @param currentlyActive estado atual lido do UiState antes da chamada.
 * @return true se a operação foi executada, false se bloqueada por falta de permissão.
 */
class ToggleRadarUseCase @Inject constructor(
    private val driverSettingsRepository: DriverSettingsRepository,
    private val radarController: RadarController,
    private val permissionsChecker: PermissionsChecker
) {
    suspend operator fun invoke(currentlyActive: Boolean): Boolean {
        val newState = !currentlyActive

        // Ao ativar: checar permissões obrigatórias antes de qualquer persistência
        if (newState) {
            val status = permissionsChecker.getSystemStatus()
            if (!status.isAccessibilityEnabled || !status.isOverlayEnabled) {
                return false // UI pode chamar refreshPermissionsStatus() para atualizar badges
            }
        }

        driverSettingsRepository.setRadarEnabled(newState)
        radarController.persistBootState(newState)
        if (newState) radarController.start() else radarController.stop()
        return true
    }
}
