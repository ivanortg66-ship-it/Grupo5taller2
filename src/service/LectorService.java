package service;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
 
import model.Lector;
 
public class LectorService {
 
    private final String RUTA_LECTORES = "data/lectores.csv";
    private final String RUTA_PRESTAMOS = "data/prestamos.csv";
 
    // ==========================================================
    // 1. Registrar lector
    // ==========================================================
    public void crearLector(String nombre, String apellido, String telefono) {
        int nuevoId = generarNuevoId();
        Lector nuevo = new Lector(nuevoId, nombre, apellido, telefono);
 
        File archivo = new File(RUTA_LECTORES);
        boolean archivoExiste = archivo.exists();
 
        try (FileWriter fw = new FileWriter(RUTA_LECTORES, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
 
            // Si el archivo no existía, escribimos primero el encabezado
            if (!archivoExiste) {
                bw.write("id_lector,nombre,apellido,telefono");
                bw.newLine();
            } else if (!terminaConSaltoDeLinea(archivo)) {
                // Si el archivo ya tenia contenido pero la ultima linea
                // no terminaba en salto de linea, lo agregamos primero
                // para no pegar el nuevo registro a la linea anterior
                bw.newLine();
            }
 
            bw.write(nuevo.toString());
            bw.newLine();
 
            System.out.println("Lector registrado correctamente.");
            System.out.println("ID asignado: " + nuevoId);
 
        } catch (IOException e) {
            System.out.println("Error al registrar el lector: " + e.getMessage());
        }
    }
 
    // Verifica si el ultimo byte del archivo es un salto de linea
    private boolean terminaConSaltoDeLinea(File archivo) throws IOException {
        if (archivo.length() == 0) {
            return true;
        }
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            raf.seek(archivo.length() - 1);
            int ultimoByte = raf.read();
            return ultimoByte == '\n' || ultimoByte == '\r';
        }
    }
 
    // Calcula el siguiente ID disponible (id_lector maximo existente + 1)
    private int generarNuevoId() {
        File archivo = new File(RUTA_LECTORES);
        if (!archivo.exists()) {
            return 1;
        }
 
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esEncabezado = true;
 
            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }
                if (linea.trim().isEmpty()) continue;
 
                String[] datos = linea.split(",");
                if (datos.length >= 1) {
                    int idActual = Integer.parseInt(datos[0].trim());
                    if (idActual > maxId) {
                        maxId = idActual;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
        }
 
        return maxId + 1;
    }
 
    // Verifica si un id_lector ya existe en lectores.csv
    public boolean validarId(int id) {
        List<Integer> lista = new ArrayList<>();
        File archivo = new File(RUTA_LECTORES);
 
        if (!archivo.exists()) {
            return true; // no existe el archivo, entonces el id no existe aun
        }
 
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esEncabezado = true;
 
            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }
                if (linea.trim().isEmpty()) continue;
 
                String[] datos = linea.split(",");
                lista.add(Integer.parseInt(datos[0].trim()));
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
            return false;
        }
 
        for (int idExistente : lista) {
            if (id == idExistente) {
                System.out.println("El id ya existe.");
                return false;
            }
        }
        return true;
    }
 
    // ==========================================================
    // 2. Listar lectores
    // ==========================================================
    public void listarLectores() {
        File archivo = new File(RUTA_LECTORES);
 
        if (!archivo.exists()) {
            System.out.println("No hay lectores registrados todavia.");
            return;
        }
 
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esEncabezado = true;
            boolean hayLectores = false;
 
            System.out.println("\n--- LISTADO DE LECTORES ---");
 
            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }
                if (linea.trim().isEmpty()) continue;
 
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    System.out.println(
                        "ID: " + datos[0].trim() +
                        " | Nombre: " + datos[1].trim() +
                        " | Apellido: " + datos[2].trim() +
                        " | Telefono: " + datos[3].trim()
                    );
                    hayLectores = true;
                }
            }
 
            if (!hayLectores) {
                System.out.println("No hay lectores registrados.");
            }
 
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de lectores: " + e.getMessage());
        }
    }
 
    // ==========================================================
    // 3. Eliminar lector (Física) -- de tu companero, tiene bugs
    // ==========================================================
    public void eliminarlector(int id_lector) throws IOException {
 
        List<String> idsConPrestamos = obtenerIdsLectoresConPrestamosActivos();
        if (idsConPrestamos.contains(String.valueOf(id_lector))) {
            System.out.println("No se puede eliminar: El lector con ID " + id_lector + " tiene prestamos activos.");
            return;
        }
 
        List<String> lineasFiltradas = new ArrayList<>();   // <-- FALTABA declarar
        boolean existeLector = false;                        // <-- FALTABA declarar
 
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_LECTORES))) {
            String linea;
            boolean esEncabezado = true;
 
            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    lineasFiltradas.add(linea);
                    esEncabezado = false;
                    continue;
                }
 
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    int idActual = Integer.parseInt(datos[0].trim());
 
                    if (idActual == id_lector) {
                        existeLector = true;
                    } else {
                        lineasFiltradas.add(linea);
                    }
                }
            }
        }
 
        if (!existeLector) {
            System.out.println("El lector con ID " + id_lector + " no existe en el sistema.");
            return;
        }
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_LECTORES))) {
            for (String linea : lineasFiltradas) {
                bw.write(linea);
                bw.newLine();
            }
        }
 
        System.out.println("Lector con ID " + id_lector + " eliminado exitosamente.");
    }
 
    // 6. Reportar lectores con prestamos activos
    public void reportarLectoresConPrestamosActivos() {
        List<String> idsLectoresConPrestamos = obtenerIdsLectoresConPrestamosActivos();
 
        if (idsLectoresConPrestamos.isEmpty()) {
            System.out.println("No hay lectores con prestamos activos o pendientes.");
            return;
        }
 
        System.out.println("\n--- LECTORES CON PRESTAMOS ACTIVOS ---");
 
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
                        System.out.println("ID: " + idLector + " | Nombre: " + nombre + " " + apellido + " | Telefono: "
                                + telefono);
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
            System.out.println("Error al leer el archivo de prestamos: " + e.getMessage());
        }
 
        return idsActivos;
    }
}
 