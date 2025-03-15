package org.example

import kotlin.random.Random

class App {
    fun generarCodigo(): String {
        /**
         * Genera un código de 6 caracteres alfanuméricos sin caracteres ambiguos.
         * Caracteres ambiguos a evitar: '0', 'O', '1', 'l'.
         */
        val caracteresValidos = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
        return (1..6)
            .map { caracteresValidos[Random.nextInt(caracteresValidos.length)] }
            .joinToString("")
    }

    fun ejecutar(cantidad: Int) {
        if (cantidad > 0) {
            println("Generando códigos de recuperación...")
            repeat(cantidad) { i ->
                println("Código ${i + 1}: ${generarCodigo()}")
            }
        } else {
            println("Cantidad inválida. Usa: gradle run --args='3'")
        }
    }
}

fun main(args: Array<String>) {
    val cantidad = args.firstOrNull()?.toIntOrNull()
    App().ejecutar(cantidad ?: -1)
}
