package Main;

import Entities.FichaBibliografica;
import Entities.Libro;
import Service.FichaBibliograficaService;
import Service.LibroService;

import java.util.List;
import java.util.Scanner;

public class MenuController {

    private final Scanner scanner;
    private final LibroService libroService;
    private final FichaBibliograficaService fichaService;

    public MenuController(Scanner scanner) {
        this.scanner = scanner;
        this.libroService = new LibroService();
        this.fichaService = new FichaBibliograficaService();
    }

    // =========================================================
    //                 MENÚ LIBROS (A)
    // =========================================================
    public void menuLibros() {
        int opcion;
        do {
            System.out.println("\n===== GESTIÓN DE LIBROS =====");
            System.out.println("1 - Crear libro");
            System.out.println("2 - Leer libro por ID");
            System.out.println("3 - Listar libros");
            System.out.println("4 - Actualizar libro");
            System.out.println("5 - Eliminar lógico libro");
            System.out.println("0 - Volver");
            System.out.print("Opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> crearLibro();
                case 2 -> leerLibroPorId();
                case 3 -> listarLibros();
                case 4 -> actualizarLibro();
                case 5 -> eliminarLogicoLibro();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    // =========================================================
    //                 MENÚ FICHAS (B)
    // =========================================================
    public void menuFichas() {
        int opcion;
        do {
            System.out.println("\n===== GESTIÓN DE FICHAS BIBLIOGRÁFICAS =====");
            System.out.println("1 - Crear ficha");
            System.out.println("2 - Leer ficha por ID");
            System.out.println("3 - Listar fichas");
            System.out.println("4 - Actualizar ficha");
            System.out.println("5 - Eliminar lógico ficha");
            System.out.println("6 - Buscar ficha por ISBN");
            System.out.println("0 - Volver");
            System.out.print("Opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> crearFicha();
                case 2 -> leerFichaPorId();
                case 3 -> listarFichas();
                case 4 -> actualizarFicha();
                case 5 -> eliminarLogicoFicha();
                case 6 -> buscarFichaPorIsbn(); 
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    // =========================================================
    //              CRUD LIBRO (usa LibroService)
    // =========================================================

    private void crearLibro() {
        try {
            System.out.println("\n=== CREAR LIBRO ===");

            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim(); // respetamos may/min

            System.out.print("Autor: ");
            String autor = scanner.nextLine().trim();

            System.out.print("Editorial: ");
            String editorial = scanner.nextLine().trim().toUpperCase(); // normalizamos

            System.out.print("Año de edición (ej: 2020): ");
            Integer anioEdicion = leerEnteroNullable();

            // --- Datos de la ficha asociada (B) ---
            System.out.println("\n--- Datos de la FICHA BIBLIOGRÁFICA ---");

            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim(); // puede tener guiones, lo dejamos como viene

            System.out.print("Clasificación Dewey: ");
            String clasif = scanner.nextLine().trim().toUpperCase();

            System.out.print("Estantería: ");
            String estanteria = scanner.nextLine().trim().toUpperCase();

            System.out.print("Idioma: ");
            String idioma = scanner.nextLine().trim().toUpperCase();

            // Armamos ficha (B)
            FichaBibliografica ficha = new FichaBibliografica();
            ficha.setIsbn(isbn);
            ficha.setClasificacionDewey(clasif);
            ficha.setEstanteria(estanteria);
            ficha.setIdioma(idioma);
            ficha.setEliminado(false);

            // Armamos libro (A)
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setEditorial(editorial);
            if (anioEdicion != null) {
                libro.setAnioEdicion(anioEdicion);
            }
            libro.setEliminado(false);
            libro.setFichaBibliografica(ficha); // Relación 1→1

            // Llamamos al service (transacción: crear B y luego A)
            libroService.insertar(libro);

            System.out.println("✔ Libro creado correctamente con ID: " + libro.getId());
            System.out.println("✔ Ficha creada con ID: " + ficha.getId());

        } catch (Exception e) {
            System.out.println("✖ Error al crear libro: " + e.getMessage());
        }
    }

    private void leerLibroPorId() {
        try {
            System.out.println("\n=== LEER LIBRO POR ID ===");
            System.out.print("ID del libro: ");
            long id = leerLong();

            Libro libro = libroService.getById(id);
            if (libro == null) {
                System.out.println("No se encontró ningún libro con ID " + id);
                return;
            }

            System.out.println("\nLibro encontrado:");
            System.out.println("ID: " + libro.getId());
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor: " + libro.getAutor());
            System.out.println("Editorial: " + libro.getEditorial());
            System.out.println("Año edición: " + libro.getAnioEdicion());
            System.out.println("Eliminado: " + libro.isEliminado());

            FichaBibliografica f = libro.getFichaBibliografica();
            if (f != null) {
                System.out.println("=== Ficha asociada ===");
                System.out.println("ID ficha: " + f.getId());
                System.out.println("ISBN: " + f.getIsbn());
                System.out.println("Clasificación Dewey: " + f.getClasificacionDewey());
                System.out.println("Estantería: " + f.getEstanteria());
                System.out.println("Idioma: " + f.getIdioma());
                System.out.println("Eliminado: " + f.isEliminado());
            } else {
                System.out.println("El libro no tiene ficha asociada.");
            }

        } catch (Exception e) {
            System.out.println("✖ Error al leer libro: " + e.getMessage());
        }
    }

    private void listarLibros() {
        try {
            System.out.println("\n=== LISTAR LIBROS ===");
            List<Libro> libros = libroService.getAll();

            if (libros.isEmpty()) {
                System.out.println("No hay libros activos para mostrar.");
                return;
            }

            for (Libro libro : libros) {
                System.out.println("---------------------------");
                System.out.println("ID: " + libro.getId());
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + libro.getAutor());
                System.out.println("Editorial: " + libro.getEditorial());
                System.out.println("Año: " + libro.getAnioEdicion());

                FichaBibliografica f = libro.getFichaBibliografica();
                if (f != null) {
                    System.out.println("  Ficha ID: " + f.getId() +
                            " | ISBN: " + f.getIsbn() +
                            " | Estantería: " + f.getEstanteria());
                }
            }
        } catch (Exception e) {
            System.out.println("✖ Error al listar libros: " + e.getMessage());
        }
    }

    private void actualizarLibro() {
        try {
            System.out.println("\n=== ACTUALIZAR LIBRO ===");
            System.out.print("ID del libro a actualizar: ");
            long id = leerLong();

            Libro libro = libroService.getById(id);
            if (libro == null) {
                System.out.println("No se encontró ningún libro con ese ID.");
                return;
            }

            System.out.println("Dejar vacío para mantener el valor actual.");

            System.out.println("Título actual: " + libro.getTitulo());
            System.out.print("Nuevo título: ");
            String nuevoTitulo = scanner.nextLine().trim();
            if (!nuevoTitulo.isEmpty()) libro.setTitulo(nuevoTitulo);

            System.out.println("Autor actual: " + libro.getAutor());
            System.out.print("Nuevo autor: ");
            String nuevoAutor = scanner.nextLine().trim();
            if (!nuevoAutor.isEmpty()) libro.setAutor(nuevoAutor);

            System.out.println("Editorial actual: " + libro.getEditorial());
            System.out.print("Nueva editorial: ");
            String nuevaEditorial = scanner.nextLine().trim().toUpperCase();
            if (!nuevaEditorial.isEmpty()) libro.setEditorial(nuevaEditorial);

            System.out.println("Año edición actual: " + libro.getAnioEdicion());
            System.out.print("Nuevo año (o vacío): ");
            String anioStr = scanner.nextLine().trim();
            if (!anioStr.isEmpty()) {
                try {
                    int nuevoAnio = Integer.parseInt(anioStr);
                    libro.setAnioEdicion(nuevoAnio);
                } catch (NumberFormatException e) {
                    System.out.println("Año inválido, se mantiene el anterior.");
                }
            }

            // No tocamos ficha acá (para simplificar),
            // el service exige que exista una ficha con ID válido
            if (libro.getFichaBibliografica() == null || libro.getFichaBibliografica().getId() == 0) {
                System.out.println("Atención: el libro debe tener una ficha válida asociada para actualizar.");
            }

            libroService.actualizar(libro);
            System.out.println("✔ Libro actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("✖ Error al actualizar libro: " + e.getMessage());
        }
    }

    private void eliminarLogicoLibro() {
        try {
            System.out.println("\n=== ELIMINAR LÓGICO LIBRO ===");
            System.out.print("ID del libro a eliminar: ");
            long id = leerLong();

            libroService.eliminar(id);

            System.out.println("✔ Libro eliminado lógicamente (eliminado = TRUE).");

        } catch (Exception e) {
            System.out.println("✖ Error al eliminar libro: " + e.getMessage());
        }
    }

    // =========================================================
    //           CRUD FICHA (usa FichaBibliograficaService)
    // =========================================================

    private void crearFicha() {
        try {
            System.out.println("\n=== CREAR FICHA BIBLIOGRÁFICA ===");

            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim();

            System.out.print("Clasificación Dewey: ");
            String clasif = scanner.nextLine().trim().toUpperCase();

            System.out.print("Estantería: ");
            String estanteria = scanner.nextLine().trim().toUpperCase();

            System.out.print("Idioma: ");
            String idioma = scanner.nextLine().trim().toUpperCase();

            FichaBibliografica ficha = new FichaBibliografica();
            ficha.setIsbn(isbn);
            ficha.setClasificacionDewey(clasif);
            ficha.setEstanteria(estanteria);
            ficha.setIdioma(idioma);
            ficha.setEliminado(false);

            fichaService.insertar(ficha);

            System.out.println("✔ Ficha creada con ID: " + ficha.getId());

        } catch (Exception e) {
            System.out.println("✖ Error al crear ficha: " + e.getMessage());
        }
    }

    private void leerFichaPorId() {
        try {
            System.out.println("\n=== LEER FICHA POR ID ===");
            System.out.print("ID de la ficha: ");
            long id = leerLong();

            FichaBibliografica ficha = fichaService.getById(id);
            if (ficha == null) {
                System.out.println("No se encontró ficha con ID " + id);
                return;
            }

            System.out.println("ID: " + ficha.getId());
            System.out.println("ISBN: " + ficha.getIsbn());
            System.out.println("Clasificación Dewey: " + ficha.getClasificacionDewey());
            System.out.println("Estantería: " + ficha.getEstanteria());
            System.out.println("Idioma: " + ficha.getIdioma());
            System.out.println("Eliminado: " + ficha.isEliminado());

        } catch (Exception e) {
            System.out.println("✖ Error al leer ficha: " + e.getMessage());
        }
    }

    private void listarFichas() {
        try {
            System.out.println("\n=== LISTAR FICHAS BIBLIOGRÁFICAS ===");
            List<FichaBibliografica> fichas = fichaService.getAll();

            if (fichas.isEmpty()) {
                System.out.println("No hay fichas activas.");
                return;
            }

            for (FichaBibliografica ficha : fichas) {
                System.out.println("---------------------------");
                System.out.println("ID: " + ficha.getId());
                System.out.println("ISBN: " + ficha.getIsbn());
                System.out.println("Clasificación Dewey: " + ficha.getClasificacionDewey());
                System.out.println("Estantería: " + ficha.getEstanteria());
                System.out.println("Idioma: " + ficha.getIdioma());
            }

        } catch (Exception e) {
            System.out.println("✖ Error al listar fichas: " + e.getMessage());
        }
    }

    private void actualizarFicha() {
        try {
            System.out.println("\n=== ACTUALIZAR FICHA ===");
            System.out.print("ID de la ficha: ");
            long id = leerLong();

            FichaBibliografica ficha = fichaService.getById(id);
            if (ficha == null) {
                System.out.println("No se encontró ficha con ese ID.");
                return;
            }

            System.out.println("Dejar vacío para mantener valor actual.");

            System.out.println("ISBN actual: " + ficha.getIsbn());
            System.out.print("Nuevo ISBN: ");
            String nuevoIsbn = scanner.nextLine().trim();
            if (!nuevoIsbn.isEmpty()) ficha.setIsbn(nuevoIsbn);

            System.out.println("Clasificación actual: " + ficha.getClasificacionDewey());
            System.out.print("Nueva clasificación: ");
            String nuevaClasif = scanner.nextLine().trim().toUpperCase();
            if (!nuevaClasif.isEmpty()) ficha.setClasificacionDewey(nuevaClasif);

            System.out.println("Estantería actual: " + ficha.getEstanteria());
            System.out.print("Nueva estantería: ");
            String nuevaEstanteria = scanner.nextLine().trim().toUpperCase();
            if (!nuevaEstanteria.isEmpty()) ficha.setEstanteria(nuevaEstanteria);

            System.out.println("Idioma actual: " + ficha.getIdioma());
            System.out.print("Nuevo idioma: ");
            String nuevoIdioma = scanner.nextLine().trim().toUpperCase();
            if (!nuevoIdioma.isEmpty()) ficha.setIdioma(nuevoIdioma);

            fichaService.actualizar(ficha);
            System.out.println("✔ Ficha actualizada correctamente.");

        } catch (Exception e) {
            System.out.println("✖ Error al actualizar ficha: " + e.getMessage());
        }
    }

    private void eliminarLogicoFicha() {
        try {
            System.out.println("\n=== ELIMINAR LÓGICO FICHA ===");
            System.out.print("ID de la ficha: ");
            long id = leerLong();

            fichaService.eliminar(id);

            System.out.println("✔ Ficha eliminada lógicamente (eliminado = TRUE)");

        } catch (Exception e) {
            System.out.println("✖ Error al eliminar ficha: " + e.getMessage());
        }
    }

    private void buscarFichaPorIsbn() {
    try {
        System.out.println("\n=== BUSCAR FICHA POR ISBN ===");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();  // lo dejamos tal cual, puede tener guiones

        if (isbn.isEmpty()) {
            System.out.println("El ISBN no puede estar vacío.");
            return;
        }

        FichaBibliografica ficha = fichaService.buscarPorIsbn(isbn);

        if (ficha == null) {
            System.out.println("No se encontró ninguna ficha con ISBN " + isbn);
            return;
        }

        System.out.println("Ficha encontrada:");
        System.out.println("ID: " + ficha.getId());
        System.out.println("ISBN: " + ficha.getIsbn());
        System.out.println("Clasificación Dewey: " + ficha.getClasificacionDewey());
        System.out.println("Estantería: " + ficha.getEstanteria());
        System.out.println("Idioma: " + ficha.getIdioma());
        System.out.println("Eliminado: " + ficha.isEliminado());

    } catch (Exception e) {
        System.out.println("✖ Error al buscar ficha por ISBN: " + e.getMessage());
    }
}

    
    
    
    // =========================================================
    //              MÉTODOS AUXILIARES DE LECTURA
    // =========================================================

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Long leerLong() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido, ingrese un número válido: ");
            }
        }
    }

    private Integer leerEnteroNullable() {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, se usará null.");
            return null;
        }
    }
}
