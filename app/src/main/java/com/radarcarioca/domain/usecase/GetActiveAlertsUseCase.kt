package com.radarcarioca.domain.usecase

import com.radarcarioca.core.Result
import com.radarcarioca.domain.model.SecurityAlert
import com.radarcarioca.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retorna o fluxo reativo de todos os alertas de segurança ativos.
 *
 * Aplica a estratégia offline-first do [AlertRepository]:
 *   Loading(cache) → Success(fresh) ou Error(exception, cache)
 *
 * A UI recebe dados imediatamente do Room enquanto o Firebase sincroniza.
 */
class GetActiveAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    operator fun invoke(): Flow<Result<List<SecurityAlert>>> =
        alertRepository.getActiveAlerts()
}
