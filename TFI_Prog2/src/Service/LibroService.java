/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Config.DatabaseConnection;
import Dao.FichaBibliograficaDAO;
import Dao.LibroDAO;
import Entities.FichaBibliografica;
import Entities.Libro;

import java.sql.Connection;
import java.util.List;

/**
 *
 * @author David
 */
public class LibroService implements GenericService<Libro> {
    private final LibroDAO libroDao = new LibroDAO();
    private final FichaBibliograficaDAO fichaDao = new FichaBibliograficaDAO();

    // ---------------------------------------------------------
    // INSERTAR: TRANSACCIÓN COMPUESTA 1 → 1
    // ---------------------------------------------------------
    @Override
    public void insertar(Libro libro) throws Exception {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // VALIDACIONES
            if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
                throw new Exception("El título no puede estar vacío.");
            }
            if (libro.getFichaBibliografica() == null) {
                throw new Exception("El libro debe tener una ficha asociada.");
            }

            FichaBibliografica ficha = libro.getFichaBibliografica();

            // Regla 1 → 1: La ficha NO debe tener ID todavía
            if (ficha.getId() != 0) {
                throw new Exception("La ficha ya existe. No puede asociarse una ficha ya creada.");
            }

            // 1) Crear ficha (B)
            fichaDao.crear(ficha, conn);

            // 2) Asignar ID de ficha al libro
            libro.setFichaBibliografica(ficha);
            
          
            // INICIO DE LA SIMULACIÓN DE FALLO PARA ROLLBACK 
            // Si el título es "FALLO SIMULADO", lanzamos una excepción.
            // Esto sucede DESPUÉS de insertar la ficha, pero ANTES de insertar el libro.
            
            if ("FALLO SIMULADO".equalsIgnoreCase(libro.getTitulo())) {
                 // Esta excepción provocará el salto al catch y la ejecución de conn.rollback()
                throw new Exception("ERROR SIMULADO: Se forzó un fallo transaccional antes de insertar el libro.");
            }
            


            // 3) Crear libro (A)
            libroDao.crear(libro, conn);

            // Si todo OK
            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                //  ROLLBACK: Deshace la inserción de la ficha que se hizo en el paso 1)
                conn.rollback(); 
            }
            throw new Exception("Error al insertar libro: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ---------------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------------
    @Override
    public void actualizar(Libro libro) throws Exception {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Validaciones básicas
            if (libro.getId() == 0) {
                throw new Exception("El ID del libro no puede ser 0.");
            }
            if (libro.getFichaBibliografica() == null) {
                throw new Exception("El libro debe tener una ficha asociada.");
            }
            if (libro.getFichaBibliografica().getId() == 0) {
                throw new Exception("La ficha asociada debe existir antes de actualizar.");
            }

            // Actualizar libro
            libroDao.actualizar(libro, conn);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al actualizar libro: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ---------------------------------------------------------
    // ELIMINAR (SOLO MARCAR ELIMINADO)
    // ---------------------------------------------------------
    @Override
    public void eliminar(long id) throws Exception {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            libroDao.eliminar(id, conn);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al eliminar libro: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ---------------------------------------------------------
    // LECTURAS
    // ---------------------------------------------------------
    @Override
    public Libro getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return libroDao.leer(id, conn);
        }
    }

    @Override
    public List<Libro> getAll() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return libroDao.leerTodos(conn);
        }
    }
}