package com.radarcarioca.domain.usecase

import com.radarcarioca.core.Result
import com.radarcarioca.domain.repository.AlertRepository
import javax.inject.Inject

/**
 * Força sincronização pontual dos alertas com o Firebase.
 *
 * Usado pelo pull-to-refresh do Dashboard. Diferente de [GetActiveAlertsUseCase],
 * esta operação é imperativa (suspend), não reativa — útil quando o motorista
 * quer garantir dados frescos antes de iniciar o turno.
 *
 * Retorna [DataResult.Error] se a rede estiver indisponível, sem lançar exceção.
 */
class RefreshAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        alertRepository.refreshAlerts()
}
