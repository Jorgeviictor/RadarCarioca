package com.radarcarioca.usecase

import app.cash.turbine.test
import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.usecase.GetActiveAlertsUseCase
import com.radarcarioca.fake.FakeAlertRepository
import com.radarcarioca.fake.fakeSecurityAlert
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertInstanceOf
import org.junit.Before
import org.junit.Test

class GetActiveAlertsUseCaseTest {

    private lateinit var repository: FakeAlertRepository
    private lateinit var useCase: GetActiveAlertsUseCase

    @Before
    fun setup() {
        repository = FakeAlertRepository()
        useCase = GetActiveAlertsUseCase(repository)
    }

    @Test
    fun `retorna lista vazia quando nao ha alertas`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertInstanceOf(DataResult.Success::class.java, result)
            assertEquals(0, (result as DataResult.Success).data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retorna alertas ativos com sucesso`() = runTest {
        repository.emit(listOf(fakeSecurityAlert(), fakeSecurityAlert(id = "alert_002")))

        useCase().test {
            val result = awaitItem()
            assertInstanceOf(DataResult.Success::class.java, result)
            assertEquals(2, (result as DataResult.Success).data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtra alertas inativos`() = runTest {
        repository.emit(
            listOf(
                fakeSecurityAlert(id = "ativo",   isActive = true),
                fakeSecurityAlert(id = "inativo", isActive = false)
            )
        )

        useCase().test {
            val result = awaitItem() as DataResult.Success
            assertEquals(1, result.data.size)
            assertEquals("ativo", result.data.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emite novo alerta adicionado em tempo real`() = runTest {
        useCase().test {
            val primeiro = awaitItem() as DataResult.Success
            assertEquals(0, primeiro.data.size)

            repository.add(fakeSecurityAlert(id = "novo"))

            val segundo = awaitItem() as DataResult.Success
            assertEquals(1, segundo.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
