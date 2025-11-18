/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entities.FichaBibliografica;
import Entities.Libro;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Luucho
 */
public class LibroDAO implements GenericDAO<Libro>{

    @Override
    public void crear(Libro libro, Connection conn) throws SQLException {
        String sql = "INSERT INTO libro (titulo, autor, editorial, anioEdicion, fichaBibliografica_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setInt(4, libro.getAnioEdicion());
            stmt.setLong(5, libro.getFichaBibliografica().getId()); // Relación 1 a 1

            int filas = stmt.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se pudo insertar el libro.");
            }

            // Obtener el ID autogenerado y asignarlo al objeto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getLong(1));
                    System.out.println("Libro insertado con ID: " + libro.getId());
                }
            }
        }
    }

    @Override
    public Libro leer(long id, Connection conn) throws SQLException {
        String sql = """
        SELECT 
            l.id AS libro_id,
            l.titulo,
            l.autor,
            l.editorial,
            l.anioEdicion,
            l.eliminado AS libro_eliminado,
            f.id AS ficha_id,
            f.isbn,
            f.clasificacionDewey,
            f.estanteria,
            f.idioma,
            f.eliminado AS ficha_eliminado
        FROM libro l
        LEFT JOIN fichabibliografica f ON l.fichaBibliografica_id = f.id
        WHERE l.id = ? AND l.eliminado = FALSE
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Crear el objeto FichaBibliografica
                    FichaBibliografica ficha = new FichaBibliografica();
                    ficha.setId(rs.getLong("ficha_id"));
                    ficha.setIsbn(rs.getString("isbn"));
                    ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                    ficha.setEstanteria(rs.getString("estanteria"));
                    ficha.setIdioma(rs.getString("idioma"));
                    ficha.setEliminado(rs.getBoolean("ficha_eliminado"));

                    // Crear el objeto Libro
                    Libro libro = new Libro();
                    libro.setId(rs.getLong("libro_id"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setEditorial(rs.getString("editorial"));
                    libro.setAnioEdicion(rs.getInt("anioEdicion"));
                    libro.setEliminado(rs.getBoolean("libro_eliminado"));
                    libro.setFichaBibliografica(ficha);

                    return libro;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener Libro por ID: " + e.getMessage(), e);
        }

        return null; // Si no encontró nada
    }

    @Override
    public List<Libro> leerTodos(Connection conn) throws SQLException {
        String sql = """
        SELECT 
            l.id AS libro_id,
            l.titulo,
            l.autor,
            l.editorial,
            l.anioEdicion,
            l.eliminado AS libro_eliminado,
            f.id AS ficha_id,
            f.isbn,
            f.clasificacionDewey,
            f.estanteria,
            f.idioma,
            f.eliminado AS ficha_eliminado
        FROM libro l
        LEFT JOIN fichabibliografica f ON l.fichaBibliografica_id = f.id
        WHERE l.eliminado = FALSE
        """;

        List<Libro> libros = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Crear el objeto FichaBibliografica
                FichaBibliografica ficha = new FichaBibliografica();
                ficha.setId(rs.getLong("ficha_id"));
                ficha.setIsbn(rs.getString("isbn"));
                ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                ficha.setEstanteria(rs.getString("estanteria"));
                ficha.setIdioma(rs.getString("idioma"));
                ficha.setEliminado(rs.getBoolean("ficha_eliminado"));

                // Crear el objeto Libro
                Libro libro = new Libro();
                libro.setId(rs.getLong("libro_id"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioEdicion(rs.getInt("anioEdicion"));
                libro.setEliminado(rs.getBoolean("libro_eliminado"));
                libro.setFichaBibliografica(ficha);

                libros.add(libro); // Agrega el libro a la lista
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener todas los Libros: " + e.getMessage(), e);
        }
        return libros; // Si no hay resultados, devuelve lista vacía
    }

    @Override
    public void actualizar(Libro libro, Connection conn) throws SQLException {
        String sql = """
            UPDATE libro 
            SET titulo = ?, 
                autor = ?, 
                editorial = ?, 
                anioEdicion = ?, 
                fichaBibliografica_id = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setInt(4, libro.getAnioEdicion());
            stmt.setLong(5, libro.getFichaBibliografica().getId());
            stmt.setLong(6, libro.getId());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el libro con ID: " + libro.getId());
            }
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        String sql = "UPDATE libro SET eliminado = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo eliminar el libro con ID: " + id);
            } else {
                System.out.println("Libro con ID " + id + " eliminado.");
            }
        }
    }
    
}
