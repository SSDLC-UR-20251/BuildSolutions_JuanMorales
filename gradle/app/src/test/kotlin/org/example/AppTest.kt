package org.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppTest {
    @Test
    fun testGenerarCodigoLongitudCorrecta() {
        val classUnderTest = App()
        val codigo = classUnderTest.generarCodigo()
        
        // Verificar que el código generado tiene 6 caracteres
        assertEquals(6, codigo.length, "El código debe tener exactamente 6 caracteres")
    }

    @Test
    fun testGenerarCodigoSinCaracteresAmbiguos() {
        val classUnderTest = App()
        val codigo = classUnderTest.generarCodigo()
        val caracteresInvalidos = setOf('0', 'O', '1', 'l')

        // Verificar que el código generado no contiene caracteres ambiguos
        assertTrue(codigo.none { it in caracteresInvalidos }, "El código no debe contener caracteres ambiguos")
    }
}
