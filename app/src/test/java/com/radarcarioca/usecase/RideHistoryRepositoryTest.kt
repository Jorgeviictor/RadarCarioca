package com.radarcarioca.usecase

import com.radarcarioca.data.model.RideRecord
import com.radarcarioca.fake.FakeRideHistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class RideHistoryRepositoryTest {

    private lateinit var repository: FakeRideHistoryRepository

    @Before
    fun setup() {
        repository = FakeRideHistoryRepository()
    }

    @Test
    fun `corridas aceitas somam lucro total`() = runTest {
        val agora = System.currentTimeMillis()
        repository.insert(RideRecord(destinationText = "A", fareValue = 20.0, netProfit = 10.0, profitPerKm = 2.0, wasAccepted = true,  hadSecurityAlert = false))
        repository.insert(RideRecord(destinationText = "B", fareValue = 15.0, netProfit = 7.0,  profitPerKm = 1.5, wasAccepted = true,  hadSecurityAlert = false))
        repository.insert(RideRecord(destinationText = "C", fareValue = 10.0, netProfit = 4.0,  profitPerKm = 1.0, wasAccepted = false, hadSecurityAlert = false))

        val lucro = repository.getTotalProfit(agora - 60_000).first()
        assertEquals(17.0, lucro ?: 0.0, 0.001)  // Apenas corridas aceitas: 10 + 7
    }

    @Test
    fun `contagem de corridas aceitas e rejeitadas`() = runTest {
        val agora = System.currentTimeMillis()
        repeat(3) { repository.insert(RideRecord(destinationText = "X", fareValue = 10.0, netProfit = 5.0, profitPerKm = 1.0, wasAccepted = true,  hadSecurityAlert = false)) }
        repeat(2) { repository.insert(RideRecord(destinationText = "Y", fareValue = 10.0, netProfit = 5.0, profitPerKm = 1.0, wasAccepted = false, hadSecurityAlert = false)) }

        assertEquals(3, repository.getAcceptedCount(agora - 60_000).first())
        assertEquals(2, repository.getRejectedCount(agora - 60_000).first())
    }

    @Test
    fun `contagem de alertas de seguranca`() = runTest {
        val agora = System.currentTimeMillis()
        repository.insert(RideRecord(destinationText = "Maré",   fareValue = 20.0, netProfit = 8.0, profitPerKm = 1.0, wasAccepted = false, hadSecurityAlert = true))
        repository.insert(RideRecord(destinationText = "Alemão", fareValue = 20.0, netProfit = 8.0, profitPerKm = 1.0, wasAccepted = false, hadSecurityAlert = true))
        repository.insert(RideRecord(destinationText = "Barra",  fareValue = 30.0, netProfit = 12.0, profitPerKm = 2.0, wasAccepted = true, hadSecurityAlert = false))

        assertEquals(2, repository.getAlertCount(agora - 60_000).first())
    }

    @Test
    fun `deleteOlderThan remove apenas registros antigos`() = runTest {
        val agora = System.currentTimeMillis()
        val antigo = RideRecord(timestamp = agora - 100_000, destinationText = "Antigo", fareValue = 10.0, netProfit = 4.0, profitPerKm = 1.0, wasAccepted = true, hadSecurityAlert = false)
        val recente = RideRecord(timestamp = agora,          destinationText = "Recente", fareValue = 20.0, netProfit = 8.0, profitPerKm = 2.0, wasAccepted = true, hadSecurityAlert = false)

        repository.insert(antigo)
        repository.insert(recente)
        repository.deleteOlderThan(agora - 50_000)

        val corridas = repository.getRecentRides().first()
        assertEquals(1, corridas.size)
        assertEquals("Recente", corridas.first().destinationText)
    }
}
