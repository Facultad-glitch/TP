/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities;

/**
 *
 * @author Luucho
 */
public class FichaBibliografica {
    private long id;
    private boolean eliminado;
    private String isbn;
    private String clasificacionDewey;
    private String estanteria;
    private String idioma;
    
    // Constructor Vacio
    public FichaBibliografica(){};
    
    // Constructor
    public FichaBibliografica(long id, boolean eliminado, String isbn, String clasificacionDewey, String estanteria, String idioma) {
        this.id = id;
        this.eliminado = eliminado;
        this.isbn = isbn;
        this.clasificacionDewey = clasificacionDewey;
        this.estanteria = estanteria;
        this.idioma = idioma;
    }
    
    // Getters y Setters
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public boolean isEliminado() {return eliminado;}
    public void setEliminado(boolean eliminado) {this.eliminado = eliminado;}

    public String getIsbn() {return isbn;}
    public void setIsbn(String isbn) {this.isbn = isbn;}

    public String getClasificacionDewey() {return clasificacionDewey;}
    public void setClasificacionDewey(String clasificacionDewey) {this.clasificacionDewey = clasificacionDewey;}

    public String getEstanteria() {return estanteria;}
    public void setEstanteria(String estanteria) {this.estanteria = estanteria;}

    public String getIdioma() {return idioma;}
    public void setIdioma(String idioma) {this.idioma = idioma;}

    @Override
    public String toString() {
        return "FichaBibliografica{" + "id=" + id + ", eliminado=" + eliminado + ", isbn=" + isbn + ", clasificacionDewey=" + clasificacionDewey + ", estanteria=" + estanteria + ", idioma=" + idioma + '}';
    }
    
}
