package com.radarcarioca

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Teste de smoke — valida que o ambiente JUnit está funcional.
 * Nenhuma dependência Android, Room ou Firebase.
 */
class HelloWorldTest {

    @Test
    fun `ambiente de testes esta configurado corretamente`() {
        assertEquals("Radar Carioca", "Radar Carioca")
    }
}
