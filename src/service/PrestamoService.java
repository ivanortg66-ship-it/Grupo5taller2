package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PrestamoService {

    private final String RUTA_PRESTAMOS = "data/prestamos.csv";
    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String SEPARADOR_CSV = ",";

    private final int COL_ID_LECTOR = 0;
    private final int COL_ESTADO_LECTOR = 4; // Asumiendo ID, Nombre, Apellido, Telefono, Estado

    public PrestamoService() {
    }

    // Método para registrar un nuevo préstamo
    public void registrarPrestamo(Scanner scanner) {
        System.out.println("\n--- REGISTRAR PRÉSTAMO ---");
        System.out.print("Ingrese el ID del lector: ");
        String idLector = scanner.nextLine().trim();

        if (idLector.isEmpty()) {
            System.out.println("El ID del lector no puede estar vacío.");
            return;
        }

        // Validar si el lector existe y está ACTIVO
        if (!esLectorValidoYActivo(idLector)) {
            System.out.println("Error: El lector con ID " + idLector + " no existe o se encuentra INACTIVO.");
            return;
        }

        System.out.print("Ingrese el ID del libro/préstamo: ");
        String idPrestamo = scanner.nextLine().trim();

        System.out.print("Ingrese la fecha de préstamo (YYYY-MM-DD): ");
        String fechaPrestamo = scanner.nextLine().trim();

        System.out.print("Ingrese la fecha límite de devolución (YYYY-MM-DD): ");
        String fechaLimite = scanner.nextLine().trim();

        // Estructura: idPrestamo, idLector, fechaPrestamo, fechaLimite, fechaDevolucion
        // La fechaDevolucion queda vacía al momento de registrar el préstamo activo
        String lineaNueva = idPrestamo + SEPARADOR_CSV + idLector + SEPARADOR_CSV + 
                            fechaPrestamo + SEPARADOR_CSV + fechaLimite + SEPARADOR_CSV;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_PRESTAMOS, true))) {
            bw.write(lineaNueva);
            bw.newLine();
            System.out.println("Préstamo registrado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar en prestamos.csv: " + e.getMessage());
        }
    }

    // Auxiliar: Verifica que el lector exista y no esté dado de baja (INACTIVO)
    private boolean esLectorValidoYActivo(String idLector) {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }

                String[] datos = linea.split(SEPARADOR_CSV);
                if (datos.length > COL_ID_LECTOR) {
                    String id = datos[COL_ID_LECTOR].trim();

                    if (id.equals(idLector)) {
                        // Si existe columna de estado y dice INACTIVO, no es válido
                        if (datos.length > COL_ESTADO_LECTOR && datos[COL_ESTADO_LECTOR].trim().equalsIgnoreCase("INACTIVO")) {
                            return false;
                        }
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer lectores.csv: " + e.getMessage());
        }
        return false;
    }
}