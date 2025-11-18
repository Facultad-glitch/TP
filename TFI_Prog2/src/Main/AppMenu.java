package Main;

import java.util.Scanner;

public class AppMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final MenuController controller;

    public AppMenu() {
        this.controller = new MenuController(scanner);
    }

    public void run() {
        int opcion;

        do {
            System.out.println("====== MENU PRINCIPAL ======");
            System.out.println("1 - CRUD de Libros");
            System.out.println("2 - CRUD de Fichas Bibliograficas");
            System.out.println("0 - Salir");
            System.out.print("Opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> controller.menuLibros();
                case 2 -> controller.menuFichas();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }
}
