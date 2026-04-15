package com.radarcarioca.usecase

import com.radarcarioca.domain.model.DriverConfig
import com.radarcarioca.domain.model.FuelType
import com.radarcarioca.data.model.RideOffer
import com.radarcarioca.domain.usecase.CalculateRideProfitUseCase
import com.radarcarioca.financial.FinancialCalculator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Testa o Use Case de cálculo financeiro de forma totalmente isolada.
 * Roda em JVM puro — sem Android, sem Room, sem Hilt.
 */
class CalculateRideProfitUseCaseTest {

    private lateinit var useCase: CalculateRideProfitUseCase

    private val defaultConfig = DriverConfig(
        fuelPricePerLiter  = 6.20,
        kmPerLiter         = 12.0,
        platformFeePercent = 0.20,
        targetProfitPerKm  = 2.50,
        fuelType           = FuelType.GASOLINA
    )

    @Before
    fun setup() {
        useCase = CalculateRideProfitUseCase(FinancialCalculator())
    }

    @Test
    fun `corrida lucrativa retorna lucro positivo`() {
        val offer = RideOffer(
            destinationText      = "Copacabana",
            fareValue            = 25.0,
            rideDistanceKm       = 8.0,
            deadheadDistanceKm   = 2.0
        )

        val result = useCase(offer, defaultConfig)

        assertTrue("Lucro deve ser positivo", result.netProfit > 0)
        assertTrue("R$/KM deve ser positivo", result.profitPerKm > 0)
    }

    @Test
    fun `plataforma desconta 20 porcento do valor bruto`() {
        val offer = RideOffer(
            destinationText    = "Ipanema",
            fareValue          = 30.0,
            rideDistanceKm     = 10.0,
            deadheadDistanceKm = 1.0
        )

        val result = useCase(offer, defaultConfig)

        assertEquals(6.0, result.platformCut, 0.001)   // 20% de R$30
        assertEquals(24.0, result.netRevenue, 0.001)    // R$30 - R$6
    }

    @Test
    fun `corrida curta com alto deadhead pode gerar prejuizo`() {
        val offer = RideOffer(
            destinationText    = "Zona Norte",
            fareValue          = 8.0,
            rideDistanceKm     = 2.0,
            deadheadDistanceKm = 10.0   // motorista roda muito mais do que a corrida paga
        )

        val result = useCase(offer, defaultConfig)

        assertTrue("Corrida com alto deadhead deve gerar prejuízo", result.netProfit < 0)
    }

    @Test
    fun `distancia total inclui deadhead`() {
        val offer = RideOffer(
            destinationText    = "Barra",
            fareValue          = 40.0,
            rideDistanceKm     = 15.0,
            deadheadDistanceKm = 5.0
        )

        val result = useCase(offer, defaultConfig)

        assertEquals(20.0, result.totalDistanceKm, 0.001)
    }

    @Test
    fun `distancia negativa lanca IllegalArgumentException`() {
        val offerInvalida = RideOffer(
            destinationText    = "Destino",
            fareValue          = 20.0,
            rideDistanceKm     = -1.0,
            deadheadDistanceKm = 2.0
        )

        assertThrows(IllegalArgumentException::class.java) {
            useCase(offerInvalida, defaultConfig)
        }
    }

    @Test
    fun `valor negativo lanca IllegalArgumentException`() {
        val offerInvalida = RideOffer(
            destinationText    = "Destino",
            fareValue          = -5.0,
            rideDistanceKm     = 8.0,
            deadheadDistanceKm = 2.0
        )

        assertThrows(IllegalArgumentException::class.java) {
            useCase(offerInvalida, defaultConfig)
        }
    }

    @Test
    fun `margem de lucro calculada sobre valor bruto`() {
        val offer = RideOffer(
            destinationText    = "Centro",
            fareValue          = 20.0,
            rideDistanceKm     = 5.0,
            deadheadDistanceKm = 1.0
        )

        val result = useCase(offer, defaultConfig)

        val expectedMargin = (result.netProfit / 20.0) * 100.0
        assertEquals(expectedMargin, result.profitMarginPercent, 0.01)
    }
}
