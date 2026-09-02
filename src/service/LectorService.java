package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    // NOTA: el método registrarLector() original estaba vacío (dead code).
    // La lógica real vive en crearLector(nombre, apellido, telefono).
    // Si algo en Main llamaba a registrarLector(), actualízalo para llamar
    // a crearLector(...) en su lugar.

    //1. Registrar Lector
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
                    try {
                        int idActual = Integer.parseInt(datos[0].trim());
                        if (idActual > maxId) {
                            maxId = idActual;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Línea con ID inválido ignorada: " + linea);
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
                try {
                    lista.add(Integer.parseInt(datos[0].trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Línea con ID inválido ignorada: " + linea);
                }
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

    //2. Listar Lectores
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
                if (datos.length >= COLUMNAS_MINIMAS_LECTOR) {
                    System.out.println(
                        "ID: " + datos[COL_ID_LECTOR].trim() +
                        " | Nombre: " + datos[COL_NOMBRE].trim() +
                        " | Apellido: " + datos[COL_APELLIDO].trim() +
                        " | Telefono: " + datos[COL_TELEFONO].trim()
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

    // 3. Eliminar Lector
    public void eliminarLector() throws IOException {
        String idLector;
        boolean lectorEncontrado;
        List<String> lineasActualizadas;

        // Solicitar y validar la existencia del ID, y filtrar en la misma pasada
        while (true) {
            System.out.print("Ingrese el ID del lector que desea eliminar: ");
            idLector = scanner.nextLine().trim();

            // Verifica que no esté vacío
            if (idLector.isEmpty()) {
                System.out.println("El ID del lector no puede estar vacío. Intente de nuevo.\n");
                continue;
            }

            lectorEncontrado = false;
            lineasActualizadas = new ArrayList<>();

            // Buscar y filtrar en una sola lectura del archivo
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(RUTA_LECTORES), StandardCharsets.UTF_8))) {
                String linea;
                boolean esEncabezado = true;

                while ((linea = br.readLine()) != null) {
                    if (esEncabezado) {
                        lineasActualizadas.add(linea);
                        esEncabezado = false;
                        continue;
                    }

                    String[] datos = linea.split(SEPARADOR_CSV, -1); // -1 conserva campos vacíos al final

                    // Si la línea no tiene columnas suficientes, se conserva tal cual (no se descarta)
                    if (datos.length < COLUMNAS_MINIMAS_LECTOR) {
                        lineasActualizadas.add(linea);
                        continue;
                    }

                    String id = datos[COL_ID_LECTOR].trim();

                    if (id.equals(idLector)) {
                        lectorEncontrado = true;
                        // No se agrega esta línea: es la que se va a eliminar
                    } else {
                        lineasActualizadas.add(linea);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer lectores.csv: " + e.getMessage());
                return;
            }

            if (!lectorEncontrado) {
                System.out.println("No se encontró ningún lector con el ID: " + idLector);
                System.out.println("Ingrese nuevamente el ID.\n");
                continue;
            }

            break; // ID válido y encontrado, salir del bucle de validación
        }

        // Confirmación antes de eliminar
        System.out.print("¿Está seguro que desea eliminar el lector con ID " + idLector + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim();
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada. El lector no fue eliminado.");
            return;
        }

        // Escribir a un archivo temporal y luego reemplazar el original (escritura segura)
        Path rutaOriginal = Paths.get(RUTA_LECTORES);
        Path rutaTemporal = Paths.get(RUTA_LECTORES + ".tmp");

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(rutaTemporal.toFile()), StandardCharsets.UTF_8))) {
            for (String linea : lineasActualizadas) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir archivo temporal: " + e.getMessage());
            Files.deleteIfExists(rutaTemporal);
            return;
        }

        try {
            Files.move(rutaTemporal, rutaOriginal, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("El lector con ID " + idLector + " fue eliminado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al reemplazar lectores.csv: " + e.getMessage());
            Files.deleteIfExists(rutaTemporal);
        }
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
        boolean yaInactivo = false;

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

                // Las líneas malformadas (menos columnas de las esperadas) se
                // conservan tal cual en lugar de perderse silenciosamente.
                if (datos.length < COLUMNAS_MINIMAS_LECTOR) {
                    lineasActualizadas.add(linea);
                    continue;
                }

                String id = datos[COL_ID_LECTOR].trim();

                if (id.equals(idLector)) {
                    lectorEncontrado = true;
                    String nombre = datos[COL_NOMBRE].trim();
                    String apellido = datos[COL_APELLIDO].trim();
                    String telefono = datos[COL_TELEFONO].trim();

                    // Si ya tiene una 5ta columna con el estado INACTIVO, no hacemos nada más
                    if (datos.length > COLUMNAS_MINIMAS_LECTOR
                            && ESTADO_INACTIVO.equalsIgnoreCase(datos[COLUMNAS_MINIMAS_LECTOR].trim())) {
                        yaInactivo = true;
                        lineasActualizadas.add(linea);
                    } else {
                        // Reescribir la línea marcándola como inactivo
                        lineasActualizadas.add(id + SEPARADOR_CSV + nombre + SEPARADOR_CSV + apellido + SEPARADOR_CSV + telefono + SEPARADOR_CSV + ESTADO_INACTIVO);
                    }
                } else {
                    lineasActualizadas.add(linea);
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

        if (yaInactivo) {
            System.out.println("El lector con ID " + idLector + " ya se encontraba INACTIVO.");
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