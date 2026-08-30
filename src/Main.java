import java.util.Scanner;
import service.LectorService;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        LectorService lectorService = new LectorService();
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
                    break;
                case 2:
                    break;
                case 3:
                     lectorService.eliminarlector();
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    lectorService.reportarLectoresConPrestamosActivos();
                    break;
                case 7:
                        
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