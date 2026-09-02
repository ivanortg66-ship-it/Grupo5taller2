package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LectorService {

    // Rutas de los archivos
    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String RUTA_PRESTAMOS = "data/prestamos.csv";

    // Separador y formato del CSV
    private final String SEPARADOR_CSV = ",";
    private final String ESTADO_INACTIVO = "INACTIVO";

    // Índices de columnas en lectores.csv
    private final int COL_ID_LECTOR = 0;
    private final int COL_NOMBRE = 1;
    private final int COL_APELLIDO = 2;
    private final int COL_TELEFONO = 3;
    private final int COLUMNAS_MINIMAS_LECTOR = 4;

    // Índices de columnas en prestamos.csv
    private final int COL_ID_LECTOR_PRESTAMO = 1;
    private final int COL_FECHA_DEVOLUCION = 4;
    private final int COLUMNAS_MINIMAS_PRESTAMO = 4;

    // Scanner
    private Scanner scanner;

    // Constructor
    public LectorService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void registrarLector() {

        
    }

    
    //3. Eliminar Lector
    public void eliminarlector() throws IOException {



    }

    //6. Reportar lectores con préstamos activos

    public void reportarLectoresConPrestamosActivos() {
        List<String> idsLectoresConPrestamos = obtenerIdsLectoresConPrestamosActivos();

        if (idsLectoresConPrestamos.isEmpty()) {
            System.out.println("No hay lectores con préstamos activos o pendientes.");
            return;
        }

        System.out.println("\n--- LECTORES CON PRÉSTAMOS ACTIVOS ---");

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }

                String[] datos = linea.split(SEPARADOR_CSV);
                if (datos.length >= COLUMNAS_MINIMAS_LECTOR) {
                    String idLector = datos[COL_ID_LECTOR].trim();
                    String nombre = datos[COL_NOMBRE].trim();
                    String apellido = datos[COL_APELLIDO].trim();
                    String telefono = datos[COL_TELEFONO].trim();

                    if (idsLectoresConPrestamos.contains(idLector)) {
                        System.out.println("ID: " + idLector + " | Nombre: " + nombre + " " + apellido + " | Teléfono: " + telefono);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
        }
    }
    //Listar o Reportar lectores con préstamos activos
    private List<String> obtenerIdsLectoresConPrestamosActivos() {
        List<String> idsActivos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_PRESTAMOS))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }

                String[] datos = linea.split(SEPARADOR_CSV);
                if (datos.length >= COLUMNAS_MINIMAS_PRESTAMO) {
                    String idLector = datos[COL_ID_LECTOR_PRESTAMO].trim();

                    boolean estaPendiente = (datos.length <= COL_FECHA_DEVOLUCION) || datos[COL_FECHA_DEVOLUCION].trim().isEmpty();

                    if (estaPendiente && !idsActivos.contains(idLector)) {
                        idsActivos.add(idLector);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de préstamos: " + e.getMessage());
        }

        return idsActivos;
    }


    //Eliminar o Dar de Baja a un lector (Eliminación Lógica)
    public void darDeBajaLector(String idLector) {
        List<String> lectoresConPrestamos = obtenerIdsLectoresConPrestamosActivos();
        if (lectoresConPrestamos.contains(idLector)) {
            System.out.println("Error: El lector con ID " + idLector + " tiene préstamos activos y no puede ser dado de baja.");
            return;
        }

        List<String> lineasActualizadas = new ArrayList<>();
        boolean lectorEncontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    lineasActualizadas.add(linea);
                    esEncabezado = false;
                    continue;
                }

                String[] datos = linea.split(SEPARADOR_CSV);
                if (datos.length >= COLUMNAS_MINIMAS_LECTOR) {
                    String id = datos[COL_ID_LECTOR].trim();

                    if (id.equals(idLector)) {
                        lectorEncontrado = true;
                        String nombre = datos[COL_NOMBRE].trim();
                        String apellido = datos[COL_APELLIDO].trim();
                        String telefono = datos[COL_TELEFONO].trim();

                        // Reescribir la línea marcándola como inactivp
                        lineasActualizadas.add(id + SEPARADOR_CSV + nombre + SEPARADOR_CSV + apellido + SEPARADOR_CSV + telefono + SEPARADOR_CSV + ESTADO_INACTIVO);
                    } else {
                        lineasActualizadas.add(linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer lectores.csv: " + e.getMessage());
            return;
        }

        if (!lectorEncontrado) {
            System.out.println("No se encontró ningún lector con el ID: " + idLector);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_LECTORES))) {
            for (String l : lineasActualizadas) {
                bw.write(l);
                bw.newLine();
            }
            System.out.println("El lector con ID " + idLector + " ha sido dado de baja (INACTIVO) exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al actualizar lectores.csv: " + e.getMessage());
        }
    }
}                       