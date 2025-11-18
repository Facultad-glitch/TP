/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Config.DatabaseConnection;
import Dao.FichaBibliograficaDAO;
import Entities.FichaBibliografica;
import java.sql.Connection;
import java.util.List;


/**
 *
 * @author David
 */
public class FichaBibliograficaService implements GenericService<FichaBibliografica> {
    private final FichaBibliograficaDAO fichaDao = new FichaBibliograficaDAO();

    @Override
    public void insertar(FichaBibliografica ficha) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Validación
            if (ficha.getIsbn() == null || ficha.getIsbn().isBlank()) {
                throw new Exception("El ISBN no puede estar vacío.");
            }

            fichaDao.crear(ficha, conn);
            conn.commit(); // 🔹 Commit si salió todo bien

        } catch (Exception e) {
            if (conn != null) conn.rollback(); // 🔥 ROLLBACK
            throw new Exception("Error al insertar ficha: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }



    @Override
    public void actualizar(FichaBibliografica ficha) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            fichaDao.actualizar(ficha, conn);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) conn.rollback(); // 🔥 ROLLBACK
            throw new Exception("Error al actualizar ficha: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }



    @Override
    public void eliminar(long id) throws Exception {
        Connection conn = null;
        try {
            
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            fichaDao.eliminar(id, conn);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) conn.rollback(); // 🔥 ROLLBACK
            throw new Exception("Error al eliminar ficha: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }



    @Override
    public FichaBibliografica getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return fichaDao.leer(id, conn);
        }
    }

    @Override
    public List<FichaBibliografica> getAll() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return fichaDao.leerTodos(conn);
        }
    }

    public FichaBibliografica buscarPorIsbn(String isbn) throws Exception {
    try (Connection conn = DatabaseConnection.getConnection()) {
        return fichaDao.buscarPorIsbn(isbn, conn);
    } catch (Exception e) {
        throw new Exception("Error al buscar ficha por ISBN: " + e.getMessage(), e);
    }
}


}
