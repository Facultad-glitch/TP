/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities;

/**
 *
 * @author Luucho
 */
public class Libro {
    private long id;
    private boolean eliminado;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioEdicion;
    private FichaBibliografica fichaBibliografica;
    
    // Constructor Vacio
    public Libro(){};
    
    // Constructor
    public Libro(long id, boolean eliminado, String titulo, String autor, String editorial, int anioEdicion) {
        this.id = id;
        this.eliminado = eliminado;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioEdicion = anioEdicion;
    }
    
    // Getters y Setters
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public boolean isEliminado() {return eliminado;}
    public void setEliminado(boolean eliminado) {this.eliminado = eliminado;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getAutor() {return autor;}
    public void setAutor(String autor) {this.autor = autor;}
    
    public String getEditorial() {return editorial;}
    public void setEditorial(String editorial) {this.editorial = editorial;}

    public int getAnioEdicion() {return anioEdicion;}
    public void setAnioEdicion(int anioEdicion) {this.anioEdicion = anioEdicion;}

    public FichaBibliografica getFichaBibliografica() {return fichaBibliografica;}
    public void setFichaBibliografica(FichaBibliografica ficha) {this.fichaBibliografica = ficha;}

    @Override
    public String toString() {
        return "Libro{" + "id=" + id + ", eliminado=" + eliminado + ", titulo=" + titulo + ", autor=" + autor + ", editorial=" + editorial + ", anioEdicion=" + anioEdicion + ", ficha=" + fichaBibliografica.getIsbn() + '}';
    }
    
    
}
