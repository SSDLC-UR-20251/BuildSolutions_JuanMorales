package com.example;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    public void testLeerArchivo() {
        String rutaArchivo = "test_archivo.txt";

        // Crear un archivo temporal de prueba con contenido JSON
        String contenido = "{\n" +
                "  \"juan.jose@urosario.edu.Saco\": [\n" +
                "    {\"balance\": \"50\", \"type\": \"Deposit\", \"timestamp\": \"2025-02-11 14:17:21.921536\"},\n" +
                "    {\"balance\": \"-20\", \"type\": \"Withdrawal\", \"timestamp\": \"2025-02-15 10:30:15.123456\"}\n" +
                "  ]\n" +
                "}";

        try {
            // Escribir el contenido en un archivo temporal
            Files.write(Paths.get(rutaArchivo), contenido.getBytes());

            // Llamamos a la función de lectura
            String resultado = App.leerArchivo(rutaArchivo);

            // Verificar que el contenido leído es correcto
            assertNotNull(resultado, "El resultado no debe ser nulo");
            assertTrue(resultado.contains("Deposit"));
            assertTrue(resultado.contains("Withdrawal"));

        } catch (IOException e) {
            fail("Error al escribir o leer el archivo de prueba");
        } finally {
            // Eliminar el archivo de prueba
            try {
                Files.deleteIfExists(Paths.get(rutaArchivo));
            } catch (IOException e) {
                System.err.println("No se pudo eliminar el archivo de prueba: " + e.getMessage());
            }
        }
    }

    @Test
    public void testObtenerTransacciones() {
        String jsonData = "{\n" +
                "  \"juan.jose@urosario.edu.Saco\": [\n" +
                "    {\"balance\": \"50\", \"type\": \"Deposit\", \"timestamp\": \"2025-02-11 14:17:21.921536\"},\n" +
                "    {\"balance\": \"-20\", \"type\": \"Withdrawal\", \"timestamp\": \"2025-02-15 10:30:15.123456\"}\n" +
                "  ]\n" +
                "}";

        // Llamamos a la función para obtener las transacciones de "juan.jose@urosario.edu.Saco"
        List<JSONObject> transacciones = App.obtenerTransacciones(jsonData, "juan.jose@urosario.edu.Saco");

        // Verificar que se han extraído correctamente las transacciones
        assertNotNull(transacciones, "La lista de transacciones no debe ser nula");
        assertEquals(2, transacciones.size(), "El usuario debe tener exactamente 2 transacciones");
        assertEquals("Deposit", transacciones.get(0).getString("type"));
        assertEquals("Withdrawal", transacciones.get(1).getString("type"));
    }

    @Test
    public void testGenerarExtracto() {
        String usuario = "juan.jose@urosario.edu.Saco";
        List<JSONObject> transacciones = List.of(
                new JSONObject("{\"balance\": \"50\", \"type\": \"Deposit\", \"timestamp\": \"2025-02-11 14:17:21.921536\"}"),
                new JSONObject("{\"balance\": \"-20\", \"type\": \"Withdrawal\", \"timestamp\": \"2025-02-15 10:30:15.123456\"}")
        );

        // Generar el extracto en un archivo
        App.generarExtracto(usuario, transacciones);

        // Verificar si el archivo fue creado
        File archivo = new File(usuario + "_extracto.txt");
        assertTrue(archivo.exists(), "El archivo de extracto no fue creado");

        // Leer el contenido del archivo y verificarlo
        try {
            String contenido = new String(Files.readAllBytes(archivo.toPath()));
            assertTrue(contenido.contains("Extracto Bancario de: " + usuario));
            assertTrue(contenido.contains("Deposit"));
            assertTrue(contenido.contains("Withdrawal"));
        } catch (IOException e) {
            fail("Error al leer el archivo de extracto generado");
        } finally {
            // Eliminar el archivo de prueba
            if (archivo.exists()) {
                archivo.delete();
            }
        }
    }
}