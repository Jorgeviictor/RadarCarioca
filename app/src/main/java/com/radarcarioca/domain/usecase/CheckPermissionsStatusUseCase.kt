package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.service.PermissionsChecker
import com.radarcarioca.domain.service.SystemStatus
import javax.inject.Inject

/**
 * Consulta o status atual das permissões e recursos carregados do sistema.
 *
 * Encapsula a agregação de três verificações independentes (acessibilidade,
 * overlay e features geo) que antes estavam diretamente na ViewModel,
 * misturando código Android com lógica de apresentação.
 */
class CheckPermissionsStatusUseCase @Inject constructor(
    private val permissionsChecker: PermissionsChecker
) {
    operator fun invoke(): SystemStatus = permissionsChecker.getSystemStatus()
}
