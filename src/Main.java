import java.util.Scanner;
import service.LectorService;
import service.PrestamoService;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        // Le pasamos el objeto scanner al constructor de LectorService
        LectorService lectorService = new LectorService(scanner);
        PrestamoService prestamoService = new PrestamoService();

        while (!salir) {
            System.out.println("\n===== SISTEMA DE BIBLIOTECA =====");
            System.out.println("1. Registrar lector");
            System.out.println("2. Listar lectores");
            System.out.println("3. Eliminar lector (Física)");
            System.out.println("4. Registrar préstamo");
            System.out.println("5. Listar préstamos de un lector");
            System.out.println("6. Reportar lectores con préstamos activos");
            System.out.println("7. Dar de baja a un lector (Eliminación Lógica)");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

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
                    try {
                        lectorService.eliminarLector();
                    } catch (Exception e) {
                        System.out.println("Error al eliminar lector: " + e.getMessage());
                    }
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
                    lectorService.reportarLectoresConPrestamosActivos();
                    break;
                case 7:
                    System.out.print("Ingrese el ID del lector que desea dar de baja: ");
                    String idBaja = scanner.nextLine();
                    // Usamos la instancia existente
                    lectorService.darDeBajaLector(idBaja);
                    break;
                case 8:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        scanner.close();
    }
}