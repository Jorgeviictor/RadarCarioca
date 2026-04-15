package com.radarcarioca.usecase

import com.radarcarioca.core.DataResult
import com.radarcarioca.domain.usecase.RefreshAlertsUseCase
import com.radarcarioca.fake.FakeAlertRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertInstanceOf
import org.junit.Before
import org.junit.Test

class RefreshAlertsUseCaseTest {

    private lateinit var repository: FakeAlertRepository
    private lateinit var useCase: RefreshAlertsUseCase

    @Before
    fun setup() {
        repository = FakeAlertRepository()
        useCase = RefreshAlertsUseCase(repository)
    }

    @Test
    fun `refresh retorna sucesso quando rede disponivel`() = runTest {
        val result = useCase()
        assertInstanceOf(DataResult.Success::class.java, result)
    }

    @Test
    fun `refresh retorna erro quando rede indisponivel`() = runTest {
        repository.setRefreshFailure(true)

        val result = useCase()

        assertInstanceOf(DataResult.Error::class.java, result)
    }

    @Test
    fun `refresh nao lanca excecao — retorna DataResult Error`() = runTest {
        repository.setRefreshFailure(true)

        // Não deve propagar exceção para o caller
        val result = runCatching { useCase() }
        assert(result.isSuccess) { "UseCase não deve lançar exceção — deve encapsular em DataResult.Error" }
    }
}
