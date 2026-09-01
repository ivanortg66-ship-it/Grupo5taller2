package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Lector;

public class LectorService {

    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String RUTA_PRESTAMOS = "data/prestamos.csv";

    public void crearlector(int id, string nombre, string apellido, int telefono) {
        nuevo = new Lector(id, nombre, apellido, telefono);
        FileWriter fw = new FileWriter(RUTA_LECTORES, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(nuevo.toString());
        bw.newLine();
        bw.close();
    }

    public boolean validar_id(int id) {
        List<Integer> lista = new ArrayList<>();
        Scanner sc = new Scanner(new File(RUTA_LECTORES));

        while (sc.hasNextLine()) {
            String[] datos = sc.nextLine().split(",");
            lista.add(Integer.parseInt(datos[0]));
        }
        sc.close();

        for (int i=0;i<lista.size();i++){
            if(id == lista.get(i)){
                system.out.println("el id ya existe.");
                return false;
            }
        }
        return true;
    }
    

    // 3.Eliminar lector (Física)
    public void eliminarlector(int id_lector) throws IOException {

        // Verificar si el lector tiene préstamos asociados
        List<String> idsConPrestamos = obtenerIdsLectoresConPrestamosActivos();
        if (idsConPrestamos.contains(String.valueOf(id_lector))) {
            System.out.println("No se puede eliminar: El lector con ID " + id_lector + " tiene préstamos activos.");
            return;
        }

        // Leer el archivo lectores.csv y buscar el ID
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    lineasFiltradas.add(linea); // Conservar el encabezado
                    esEncabezado = false;
                    continue;
                }

                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    int idActual = Integer.parseInt(datos[0].trim());

                    if (idActual == id_lector) {
                        existeLector = true; // Confirma que el ID existe
                    } else {
                        // Si NO es el ID se conservamos la línea
                        lineasFiltradas.add(linea);
                    }
                }
            }
        }
        // Validar que el lector exista en el archivo
        if (!existeLector) {
            System.out.println("El lector con ID " + id_lector + " no existe en el sistema.");
            return;
        }

        // Sobreescribir el archivo
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_LECTORES))) {
            for (String linea : lineasFiltradas) {
                bw.write(linea);
                bw.newLine();
            }
        }

        System.out.println("Lector con ID " + id_lector + " eliminado exitosamente.");
    }

    // 6. Reportar lectores con préstamos activos
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
                        System.out.println("ID: " + idLector + " | Nombre: " + nombre + " " + apellido + " | Teléfono: "
                                + telefono);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
        }
    }

    // Revisa prestamos.csv y devuelve una lista con los IDs de lectores que tienen
    // entregas pendientes
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
                // Estructura CSV: id_prestamo, id_lector, libro, fecha_prestamo,
                // fecha_devolucion
                if (datos.length >= 4) {
                    String idLector = datos[1].trim();

                    // Si no tiene 5 posiciones o el campo fecha_devolucion está vacío, el préstamo
                    // está activo
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