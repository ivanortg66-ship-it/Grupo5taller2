package service;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorService {

    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String RUTA_PRESTAMOS = "data/prestamos.csv";

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

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    String idLector = datos[0].trim();
                    String nombre = datos[1].trim();
                    String apellido = datos[2].trim();
                    String telefono = datos[3].trim();

                    if (idsLectoresConPrestamos.contains(idLector)) {
                        System.out.println("ID: " + idLector + " | Nombre: " + nombre + " " + apellido + " | Teléfono: " + telefono);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
        }
    }

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
                if (datos.length >= 4) {
                    String idLector = datos[1].trim();
                    
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

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    String id = datos[0].trim();

                    if (id.equals(idLector)) {
                        lectorEncontrado = true;
                        String nombre = datos[1].trim();
                        String apellido = datos[2].trim();
                        String telefono = datos[3].trim();
                        
                        // Reescribimos la línea marcándola como INACTIVO
                        lineasActualizadas.add(id + "," + nombre + "," + apellido + "," + telefono + ",INACTIVO");
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