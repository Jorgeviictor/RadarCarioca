package com.radarcarioca.domain.usecase

import com.radarcarioca.data.model.SecurityResult
import com.radarcarioca.geo.GeoSecurityManager
import javax.inject.Inject

/**
 * Verifica a segurança geográfica de um destino geocodificado.
 *
 * **Pré-condição:** receba coordenadas válidas (lat != 0 && lng != 0).
 * Coordenadas (0, 0) indicam falha de geocodificação — nesse caso, o caller
 * ([ProcessRideOfferUseCase]) deve acionar o fallback por palavra-chave antes
 * de invocar este UseCase.
 *
 * Delegação intencional: encapsula o [GeoSecurityManager] para que os callers
 * não dependam diretamente da implementação geoespacial.
 */
class CheckRideSafetyUseCase @Inject constructor(
    private val geoSecurityManager: GeoSecurityManager
) {
    operator fun invoke(
        lat: Double,
        lng: Double,
        bufferMeters: Int = 400
    ): SecurityResult = geoSecurityManager.checkSafety(lat, lng, bufferMeters)
}
