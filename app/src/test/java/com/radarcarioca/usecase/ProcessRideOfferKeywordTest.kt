package com.radarcarioca.usecase

import com.radarcarioca.data.model.SecurityResult
import com.radarcarioca.domain.usecase.ProcessRideOfferUseCase
import org.junit.Assert.assertInstanceOf
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testa o fallback offline de segurança por palavra-chave.
 *
 * ProcessRideOfferUseCase.checkSafetyByKeyword() é o único mecanismo de
 * segurança disponível quando a geocodificação falha por falta de 4G —
 * cenário comum em zonas de risco do Rio.
 */
class ProcessRideOfferKeywordTest {

    @Test
    fun `destino na Mare detectado como perigo`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("Nova Holanda, Maré")
        assertInstanceOf(SecurityResult.Danger::class.java, result)
    }

    @Test
    fun `destino no Alemao detectado como perigo`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("Rua Joaquim, Alemão")
        assertInstanceOf(SecurityResult.Danger::class.java, result)
    }

    @Test
    fun `destino em Copacabana retorna seguro`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("Av Atlântica, Copacabana")
        assertTrue("Copacabana deve ser seguro", result is SecurityResult.Safe)
    }

    @Test
    fun `busca e case insensitive — mARé detectado`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("mARé — Nova Holanda")
        assertInstanceOf(SecurityResult.Danger::class.java, result)
    }

    @Test
    fun `destino no Chapadao detectado como perigo`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("Rua das Flores, Chapadão")
        assertInstanceOf(SecurityResult.Danger::class.java, result)
        val danger = result as SecurityResult.Danger
        assertTrue(danger.areaName.contains("Chapadão", ignoreCase = true))
    }

    @Test
    fun `destino na Rocinha detectado como perigo`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("Estrada da Rocinha")
        assertInstanceOf(SecurityResult.Danger::class.java, result)
    }

    @Test
    fun `destino vazio retorna seguro`() {
        val result = ProcessRideOfferUseCase.checkSafetyByKeyword("")
        assertTrue(result is SecurityResult.Safe)
    }
}
