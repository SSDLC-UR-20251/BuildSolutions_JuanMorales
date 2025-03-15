package com.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

public class App {

    // 🔹 1. Leer el archivo JSON desde un .txt con codificación UTF-8
    public static String leerArchivo(String rutaArchivo) {
        try {
            return new String(Files.readAllBytes(Paths.get(rutaArchivo)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return null;
        }
    }

    // 🔹 2. Obtener transacciones de un usuario específico según el correo electrónico
    public static List<JSONObject> obtenerTransacciones(String jsonData, String usuario) {
        List<JSONObject> transaccionesUsuario = new ArrayList<>();

        if (jsonData == null || jsonData.isEmpty()) {
            System.err.println("El JSON proporcionado está vacío o es nulo.");
            return transaccionesUsuario;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            if (!jsonObject.has(usuario)) {
                System.err.println("El JSON no contiene transacciones para el usuario: " + usuario);
                return transaccionesUsuario;
            }

            // Obtener el arreglo de transacciones para el usuario
            JSONArray transacciones = jsonObject.getJSONArray(usuario);

            for (int i = 0; i < transacciones.length(); i++) {
                JSONObject transaccion = transacciones.getJSONObject(i);
                transaccionesUsuario.add(transaccion);
            }

        } catch (JSONException e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
        }

        return transaccionesUsuario;
    }

    // 🔹 3. Generar extracto bancario en un archivo .txt con UTF-8
    public static void generarExtracto(String usuario, List<JSONObject> transacciones) {
        String nombreArchivo = usuario + "_extracto.txt";

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(nombreArchivo), StandardCharsets.UTF_8))) {

            writer.write("Extracto Bancario de: " + usuario);
            writer.newLine();
            writer.write("------------------------------------------");
            writer.newLine();

            for (JSONObject transaccion : transacciones) {
                String tipo = transaccion.optString("type", "Desconocido");
                String timestamp = transaccion.optString("timestamp", "Sin fecha");
                String balance = transaccion.optString("balance", "0");

                writer.write("Fecha: " + timestamp);
                writer.newLine();
                writer.write("Tipo: " + tipo);
                writer.newLine();
                writer.write("Balance: " + balance);
                writer.newLine();
                writer.write("------------------------------------------");
                writer.newLine();
            }

            System.out.println("Extracto bancario generado correctamente en: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al generar el extracto bancario: " + e.getMessage());
        }
    }

    // 🔹 4. Método main para solicitar datos y ejecutar el flujo
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        try {
            System.out.print("Ingrese la ruta del archivo JSON (archivo .txt): ");
            String rutaArchivo = scanner.nextLine();

            String jsonData = leerArchivo(rutaArchivo);
            if (jsonData == null) {
                System.err.println("No se pudo procesar el archivo. Terminando ejecución.");
                return;
            }

            System.out.print("Ingrese el correo del usuario: ");
            String usuario = scanner.nextLine();

            List<JSONObject> transacciones = obtenerTransacciones(jsonData, usuario);
            if (transacciones.isEmpty()) {
                System.out.println("No se encontraron transacciones para el usuario: " + usuario);
            } else {
                generarExtracto(usuario, transacciones);
            }
        } finally {
            scanner.close();
        }
    }
}
