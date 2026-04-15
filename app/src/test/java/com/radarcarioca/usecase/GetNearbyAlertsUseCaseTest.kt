package com.radarcarioca.usecase

import app.cash.turbine.test
import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.usecase.GetNearbyAlertsUseCase
import com.radarcarioca.fake.FakeAlertRepository
import com.radarcarioca.fake.fakeSecurityAlert
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertInstanceOf
import org.junit.Before
import org.junit.Test

// Coordenadas de referência: Complexo da Maré — Rio de Janeiro
private const val LAT_MARE = -22.8901
private const val LNG_MARE = -43.2491

class GetNearbyAlertsUseCaseTest {

    private lateinit var repository: FakeAlertRepository
    private lateinit var useCase: GetNearbyAlertsUseCase

    @Before
    fun setup() {
        repository = FakeAlertRepository()
        useCase = GetNearbyAlertsUseCase(repository)
    }

    @Test
    fun `retorna alerta dentro do raio padrao`() = runTest {
        repository.emit(listOf(fakeSecurityAlert(lat = LAT_MARE, lng = LNG_MARE)))

        useCase(LAT_MARE, LNG_MARE).test {
            val result = awaitItem() as DataResult.Success
            assertEquals(1, result.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `nao retorna alerta fora do raio`() = runTest {
        // Alerta posicionado a ~5 km de distância
        repository.emit(listOf(fakeSecurityAlert(lat = LAT_MARE + 0.05, lng = LNG_MARE + 0.05)))

        useCase(LAT_MARE, LNG_MARE, radiusKm = 1.0).test {
            val result = awaitItem() as DataResult.Success
            assertEquals(0, result.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `raio negativo lanca IllegalArgumentException`() {
        try {
            useCase(LAT_MARE, LNG_MARE, radiusKm = -1.0)
            assert(false) { "Deveria ter lançado IllegalArgumentException" }
        } catch (e: IllegalArgumentException) {
            // esperado
        }
    }

    @Test
    fun `alerta inativo nao aparece no raio`() = runTest {
        repository.emit(listOf(fakeSecurityAlert(lat = LAT_MARE, lng = LNG_MARE, isActive = false)))

        useCase(LAT_MARE, LNG_MARE).test {
            val result = awaitItem() as DataResult.Success
            assertEquals(0, result.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `raio maior captura mais alertas`() = runTest {
        repository.emit(
            listOf(
                fakeSecurityAlert(id = "perto",  lat = LAT_MARE + 0.001, lng = LNG_MARE),
                fakeSecurityAlert(id = "longe",  lat = LAT_MARE + 0.04,  lng = LNG_MARE)
            )
        )

        useCase(LAT_MARE, LNG_MARE, radiusKm = 0.5).test {
            val small = awaitItem() as DataResult.Success
            assertEquals(1, small.data.size)
            cancelAndIgnoreRemainingEvents()
        }

        useCase(LAT_MARE, LNG_MARE, radiusKm = 10.0).test {
            val large = awaitItem() as DataResult.Success
            assertEquals(2, large.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
