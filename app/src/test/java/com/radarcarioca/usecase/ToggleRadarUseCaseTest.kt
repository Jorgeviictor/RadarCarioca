package com.radarcarioca.usecase

import com.radarcarioca.domain.repository.DriverSettingsRepository
import com.radarcarioca.domain.service.PermissionsChecker
import com.radarcarioca.domain.service.RadarController
import com.radarcarioca.domain.service.SystemStatus
import com.radarcarioca.domain.usecase.ToggleRadarUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleRadarUseCaseTest {

    private lateinit var driverSettingsRepository: DriverSettingsRepository
    private lateinit var radarController: RadarController
    private lateinit var permissionsChecker: PermissionsChecker
    private lateinit var useCase: ToggleRadarUseCase

    private val allPermissionsGranted = SystemStatus(
        isAccessibilityEnabled     = true,
        isOverlayEnabled           = true,
        geoFeaturesLoaded          = 10,
        isBatteryOptimizationIgnored = true
    )

    private val accessibilityMissing = allPermissionsGranted.copy(isAccessibilityEnabled = false)
    private val overlayMissing       = allPermissionsGranted.copy(isOverlayEnabled = false)

    @Before
    fun setup() {
        driverSettingsRepository = mockk(relaxed = true)
        radarController          = mockk(relaxed = true)
        permissionsChecker       = mockk()
        useCase = ToggleRadarUseCase(driverSettingsRepository, radarController, permissionsChecker)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ativar radar (currentlyActive = false → newState = true)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `ativar radar com todas permissoes concedidas retorna true`() = runTest {
        every { permissionsChecker.getSystemStatus() } returns allPermissionsGranted

        val result = useCase(currentlyActive = false)

        assertTrue(result)
        coVerify { driverSettingsRepository.setRadarEnabled(true) }
        verify { radarController.start() }
    }

    @Test
    fun `ativar radar sem acessibilidade retorna false e nao inicia servico`() = runTest {
        every { permissionsChecker.getSystemStatus() } returns accessibilityMissing

        val result = useCase(currentlyActive = false)

        assertFalse(result)
        coVerify(exactly = 0) { driverSettingsRepository.setRadarEnabled(any()) }
        verify(exactly = 0) { radarController.start() }
    }

    @Test
    fun `ativar radar sem overlay retorna false e nao persiste estado`() = runTest {
        every { permissionsChecker.getSystemStatus() } returns overlayMissing

        val result = useCase(currentlyActive = false)

        assertFalse(result)
        coVerify(exactly = 0) { driverSettingsRepository.setRadarEnabled(any()) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Desativar radar (currentlyActive = true → newState = false)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `desativar radar nao verifica permissoes e retorna true`() = runTest {
        // Permissões NÃO devem ser consultadas ao desativar
        every { permissionsChecker.getSystemStatus() } returns accessibilityMissing

        val result = useCase(currentlyActive = true)

        assertTrue(result)
        verify(exactly = 0) { permissionsChecker.getSystemStatus() }
        coVerify { driverSettingsRepository.setRadarEnabled(false) }
        verify { radarController.stop() }
    }

    @Test
    fun `desativar radar persiste boot state como false`() = runTest {
        val result = useCase(currentlyActive = true)

        assertTrue(result)
        verify { radarController.persistBootState(false) }
    }
}
