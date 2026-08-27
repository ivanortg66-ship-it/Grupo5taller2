package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorService {

    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String RUTA_PRESTAMOS = "data/prestamos.csv";

    // Método para cruzar archivos y listar lectores con préstamos pendientes
    public void reportarLectoresConPrestamosActivos() {
        // 1. Guardaremos aquí los ID de los lectores que deben libros
        List<String> idsLectoresConPrestamos = obtenerIdsLectoresConPrestamosActivos();

        if (idsLectoresConPrestamos.isEmpty()) {
            System.out.println("No hay lectores con préstamos activos o pendientes.");
            return;
        }

        System.out.println("\n--- LECTORES CON PRÉSTAMOS ACTIVOS ---");
        
        // 2. Leemos lectores.csv y mostramos solo los que coinciden con la lista de IDs
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue; // Saltar primera línea
                }

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    String idLector = datos[0].trim();
                    String nombre = datos[1].trim();
                    String apellido = datos[2].trim();
                    String telefono = datos[3].trim();

                    // Si el ID del lector está en la lista de activos, lo imprimimos
                    if (idsLectoresConPrestamos.contains(idLector)) {
                        System.out.println("ID: " + idLector + " | Nombre: " + nombre + " " + apellido + " | Teléfono: " + telefono);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
        }
    }

    // Revisa prestamos.csv y devuelve una lista con los IDs de lectores que tienen entregas pendientes
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

                String[] datos = linea.split(",");
                // Estructura CSV: id_prestamo, id_lector, libro, fecha_prestamo, fecha_devolucion
                if (datos.length >= 4) {
                    String idLector = datos[1].trim();
                    
                    // Si no tiene 5 posiciones o el campo fecha_devolucion está vacío, el préstamo está activo
                    boolean estaPendiente = (datos.length < 5) || datos[4].trim().isEmpty();

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
}