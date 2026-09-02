import java.util.Scanner;
import service.LectorService;
import service.PrestamoService;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        LectorService lectorService = new LectorService(scanner);
        PrestamoService prestamoService = new PrestamoService();

        while (!salir) {
            System.out.println("\n========================================");
            System.out.println("          SISTEMA DE BIBLIOTECA");
            System.out.println("========================================");
            System.out.println("1. Registrar lector");
            System.out.println("2. Listar lectores");
            System.out.println("3. Eliminar lector");
            System.out.println("4. Registrar préstamo");
            System.out.println("5. Listar préstamos de un lector");
            System.out.println("6. Consultas");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese el nombre del lector: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("Ingrese el apellido del lector: ");
                        String apellido = scanner.nextLine().trim();
                        System.out.print("Ingrese el teléfono del lector: ");
                        String telefono = scanner.nextLine().trim();

                        if (nombre.isEmpty() || apellido.isEmpty()) {
                            System.out.println("Nombre y apellido no pueden estar vacíos.");
                        } else {
                            lectorService.crearLector(nombre, apellido, telefono);
                        }
                        break;

                    case 2:
                        lectorService.listarLectores();
                        break;

                    case 3:
                        System.out.print("Ingrese el ID del lector que desea dar de baja: ");
                        String idBaja = scanner.nextLine().trim();
                        lectorService.darDeBajaLector(idBaja);
                        break;

                    case 4:
                        prestamoService.registrarPrestamo(scanner);
                        break;

                    case 5:
                        System.out.print("Ingrese el ID del lector: ");
                        String idConsulta = scanner.nextLine().trim();
                        prestamoService.listarPrestamosDeLector(idConsulta);
                        break;

                    case 6:
                        boolean volver = false;
                        while (!volver) {
                            System.out.println("\n========================================");
                            System.out.println("              CONSULTAS");
                            System.out.println("========================================");
                            System.out.println("1. Historial completo de un lector");
                            System.out.println("2. Lectores con mayor cantidad de préstamos");
                            System.out.println("3. Libros actualmente prestados");
                            System.out.println("4. Generar reporte de lectores con préstamos activos");
                            System.out.println("5. Generar reporte de préstamos vencidos");
                            System.out.println("6. Volver al menú principal");
                            System.out.print("Seleccione una opción: ");

                            try {
                                int opcionConsulta = Integer.parseInt(scanner.nextLine().trim());
                                switch (opcionConsulta) {
                                    case 1:
                                    case 2:
                                    case 5:
                                        System.out.println("Opción asignada a otro integrante del equipo.");
                                        break;
                                    case 3:
                                        prestamoService.consultarLibrosActualmentePrestados();
                                        break;
                                    case 4:
                                        lectorService.reportarLectoresConPrestamosActivos();
                                        break;
                                    case 6:
                                        volver = true;
                                        break;
                                    default:
                                        System.out.println("Opción no válida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Debe ingresar un número válido.");
                            }
                        }
                        break;

                    case 7:
                        salir = true;
                        System.out.println("Saliendo del sistema...");
                        break;

                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número entero válido.");
            }
        }
        scanner.close();
    }
}