package com.radarcarioca.domain.usecase

import com.radarcarioca.core.Result
import com.radarcarioca.domain.model.SecurityAlert
import com.radarcarioca.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retorna alertas ativos dentro de [radiusKm] km de uma coordenada.
 *
 * Usado pelo overlay ao apresentar alertas próximos ao destino da corrida,
 * complementando a verificação de segurança geoespacial do GeoJSON.
 *
 * Regra de negócio: raio padrão de 1 km; cache local é consultado primeiro
 * (TTL de 5 minutos no AlertRepositoryImpl).
 */
class GetNearbyAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    operator fun invoke(
        lat: Double,
        lng: Double,
        radiusKm: Double = 1.0
    ): Flow<Result<List<SecurityAlert>>> {
        require(radiusKm > 0) { "Raio de busca deve ser positivo" }
        return alertRepository.getNearbyAlerts(lat, lng, radiusKm)
    }
}
