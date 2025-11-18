/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entities.FichaBibliografica;
import java.sql.*;
import java.util.*;
import java.sql.Connection;

/**
 *
 * @author Luucho
 */
public class FichaBibliograficaDAO implements GenericDAO<FichaBibliografica>{

    @Override
    public void crear(FichaBibliografica ficha, Connection conn) throws SQLException {
        String sql = "INSERT INTO fichabibliografica(isbn, clasificacionDewey, estanteria, idioma) VALUES (?,?,?,?)";
        
        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            
            // Seteamos los valores
            stmt.setString(1, ficha.getIsbn());
            stmt.setString(2, ficha.getClasificacionDewey());
            stmt.setString(3, ficha.getEstanteria());
            stmt.setString(4, ficha.getIdioma());
            
            // Ejecutamos la consulta
            int filas = stmt.executeUpdate();
            
            // Validamos que se haya insertado al menos una fila
            if(filas > 0){
                // Obtenemos el id autogenerado
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        ficha.setId(rs.getLong(1)); // Guardamos el id en el objeto
                    }
                }
            }else {
                throw new SQLException("No se insertó ninguna fila en fichabibliografica");
            }
        }
    }
    
    @Override
    public FichaBibliografica leer(long id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM fichabibliografica WHERE id = ? AND eliminado = FALSE";
        FichaBibliografica ficha = null;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id); // Reemplazamos el "?" con el id que queremos buscar

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { 
                    ficha = new FichaBibliografica();
                    ficha.setId(rs.getLong("id"));
                    ficha.setEliminado(rs.getBoolean("eliminado"));
                    ficha.setIsbn(rs.getString("isbn"));
                    ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                    ficha.setEstanteria(rs.getString("estanteria"));
                    ficha.setIdioma(rs.getString("idioma"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer la ficha: " + e.getMessage());
        }
        
        return ficha; // Puede ser null si no encontró ninguna fila
    }

    @Override
    public List<FichaBibliografica> leerTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM fichabibliografica WHERE eliminado = FALSE";
        List<FichaBibliografica> fichas = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) { // Recorremos cada fila
                FichaBibliografica ficha = new FichaBibliografica();
                ficha.setId(rs.getLong("id"));
                ficha.setEliminado(rs.getBoolean("eliminado"));
                ficha.setIsbn(rs.getString("isbn"));
                ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                ficha.setEstanteria(rs.getString("estanteria"));
                ficha.setIdioma(rs.getString("idioma"));

                fichas.add(ficha); //  Agregamos la ficha a la lista
            }
        }

        return fichas; // Puede venir vacía si no hay fichas activas
    }

    @Override
    public void actualizar(FichaBibliografica ficha, Connection conn) throws SQLException {
        String sql = "UPDATE fichabibliografica SET isbn = ?, clasificacionDewey = ?, estanteria = ?, idioma = ? WHERE id = ? AND eliminado = FALSE ";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ficha.getIsbn());
            stmt.setString(2, ficha.getClasificacionDewey());
            stmt.setString(3, ficha.getEstanteria());
            stmt.setString(4, ficha.getIdioma());
            stmt.setLong(5, ficha.getId());

            int filas = stmt.executeUpdate();
            if (filas == 0) {
                System.out.println("️ No se encontró ninguna ficha con id = " + ficha.getId());
            }else {
            System.out.println("Ficha actualizada correctamente (id = " + ficha.getId() + ")");
            }
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        String sql = "UPDATE fichabibliografica SET eliminado = TRUE WHERE id = ? AND eliminado = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int filas = stmt.executeUpdate(); 

            if (filas == 0) {
                System.out.println("No se encontró ninguna ficha con id = " + id);
            } else {
                System.out.println("Ficha eliminada (id = " + id + ")");
            }
        }
    }
    
    public FichaBibliografica buscarPorIsbn(String isbn, Connection conn) throws SQLException {
    String sql = "SELECT * FROM fichabibliografica WHERE isbn = ? AND eliminado = FALSE";
    FichaBibliografica ficha = null;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, isbn);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                ficha = new FichaBibliografica();
                ficha.setId(rs.getLong("id"));
                ficha.setEliminado(rs.getBoolean("eliminado"));
                ficha.setIsbn(rs.getString("isbn"));
                ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                ficha.setEstanteria(rs.getString("estanteria"));
                ficha.setIdioma(rs.getString("idioma"));
            }
        }
    }

    return ficha; // puede ser null si no existe
}



}



