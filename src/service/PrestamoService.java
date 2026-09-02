package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PrestamoService {

    private final String RUTA_PRESTAMOS = "data/prestamos.csv";
    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String SEPARADOR_CSV = ",";

    private final int COL_ID_LECTOR = 0;
    private final int COL_ESTADO_LECTOR = 4; // ID, Nombre, Apellido, Telefono, Estado

    // Índices de columnas en prestamos.csv
    // Estructura: idPrestamo, idLector, fechaPrestamo, fechaLimite, fechaDevolucion
    private final int COL_ID_PRESTAMO = 0;
    private final int COL_ID_LECTOR_PRESTAMO = 1;
    private final int COL_FECHA_PRESTAMO = 2;
    private final int COL_FECHA_LIMITE = 3;
    private final int COL_FECHA_DEVOLUCION = 4;
    private final int COLUMNAS_MINIMAS_PRESTAMO = 4;

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

    // Listar préstamos de un lector
    public void listarPrestamosDeLector(String idLector) {
        if (idLector == null || idLector.trim().isEmpty()) {
            System.out.println("El ID del lector no puede estar vacío.");
            return;
        }
        idLector = idLector.trim();

        File archivo = new File(RUTA_PRESTAMOS);
        if (!archivo.exists()) {
            System.out.println("No hay préstamos registrados todavía.");
            return;
        }

        boolean encontrados = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esEncabezado = true;

            System.out.println("\n--- PRÉSTAMOS DEL LECTOR " + idLector + " ---");

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(SEPARADOR_CSV, -1);
                if (datos.length < COLUMNAS_MINIMAS_PRESTAMO) continue;

                String idLectorLinea = datos[COL_ID_LECTOR_PRESTAMO].trim();
                if (!idLectorLinea.equals(idLector)) continue;

                String idPrestamo = datos[COL_ID_PRESTAMO].trim();
                String fechaPrestamo = datos[COL_FECHA_PRESTAMO].trim();
                String fechaLimite = datos[COL_FECHA_LIMITE].trim();
                String fechaDevolucion = (datos.length > COL_FECHA_DEVOLUCION) ? datos[COL_FECHA_DEVOLUCION].trim() : "";

                String estado = fechaDevolucion.isEmpty() ? "ACTIVO" : "DEVUELTO";

                System.out.println(
                    "ID Préstamo: " + idPrestamo +
                    " | Fecha préstamo: " + fechaPrestamo +
                    " | Fecha límite: " + fechaLimite +
                    " | Fecha devolución: " + (fechaDevolucion.isEmpty() ? "-" : fechaDevolucion) +
                    " | Estado: " + estado
                );

                encontrados = true;
            }

            if (!encontrados) {
                System.out.println("El lector con ID " + idLector + " no tiene préstamos registrados.");
            }

        } catch (FileNotFoundException e) {
            System.out.println("No hay préstamos registrados todavía.");
        } catch (IOException e) {
            System.out.println("Error al leer prestamos.csv: " + e.getMessage());
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

    // Consulta 3: Libros / Préstamos actualmente activos
    public void consultarLibrosActualmentePrestados() {
        Map<String, String> lectoresMap = new HashMap<>();

        // 1. Cargar lectores en memoria
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;
            while ((linea = br.readLine()) != null) {
                if (esEncabezado) { esEncabezado = false; continue; }
                String[] datos = linea.split(SEPARADOR_CSV);
                if (datos.length >= 3) {
                    lectoresMap.put(datos[0].trim(), datos[1].trim() + " " + datos[2].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar lectores: " + e.getMessage());
            return;
        }

        System.out.println("\n========================================");
        System.out.println("       LIBROS ACTUALMENTE PRESTADOS");
        System.out.println("========================================");
        System.out.printf("%-15s %-25s %-15s %-15s\n", "ID Préstamo", "Lector", "F. Préstamo", "F. Límite");
        System.out.println("------------------------------------------------------------------");

        boolean hayActivos = false;

        // 2. Leer prestamos.csv
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_PRESTAMOS))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) { esEncabezado = false; continue; }
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(SEPARADOR_CSV, -1);
                if (datos.length < COLUMNAS_MINIMAS_PRESTAMO) continue;

                String idPrestamo = datos[COL_ID_PRESTAMO].trim();
                String idLector = datos[COL_ID_LECTOR_PRESTAMO].trim();
                String fechaPrestamo = datos[COL_FECHA_PRESTAMO].trim();
                String fechaLimite = datos[COL_FECHA_LIMITE].trim();
                String fechaDevolucion = (datos.length > COL_FECHA_DEVOLUCION) ? datos[COL_FECHA_DEVOLUCION].trim() : "";

                // Si no se ha devuelto, sigue activo
                if (fechaDevolucion.isEmpty()) {
                    hayActivos = true;
                    String nombreLector = lectoresMap.getOrDefault(idLector, "ID: " + idLector);
                    System.out.printf("%-15s %-25s %-15s %-15s\n", idPrestamo, nombreLector, fechaPrestamo, fechaLimite);
                }
            }

            if (!hayActivos) {
                System.out.println("No hay préstamos activos actualmente.");
            }

        } catch (FileNotFoundException e) {
            System.out.println("No hay préstamos registrados todavía.");
        } catch (IOException e) {
            System.out.println("Error al leer prestamos.csv: " + e.getMessage());
        }
    }
}