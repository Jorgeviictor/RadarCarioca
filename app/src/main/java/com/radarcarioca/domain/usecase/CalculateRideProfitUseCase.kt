package com.radarcarioca.domain.usecase

import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.data.model.FinancialResult
import com.radarcarioca.data.model.RideOffer
import com.radarcarioca.financial.FinancialCalculator
import javax.inject.Inject

/**
 * Use Case: calcula o lucro real de uma oferta de corrida.
 *
 * Isola o FinancialCalculator da camada de Presentation,
 * garantindo que a lógica de negócio não vaze para o ViewModel.
 */
class CalculateRideProfitUseCase @Inject constructor(
    private val calculator: FinancialCalculator
) {
    operator fun invoke(offer: RideOffer, config: DriverConfig): FinancialResult {
        require(offer.rideDistanceKm >= 0) { "Distância da corrida não pode ser negativa" }
        require(offer.fareValue >= 0) { "Valor da corrida não pode ser negativo" }
        require(config.kmPerLiter >= 0) { "Consumo KM/L não pode ser negativo" }
        return calculator.calculate(offer, config)
    }
}
