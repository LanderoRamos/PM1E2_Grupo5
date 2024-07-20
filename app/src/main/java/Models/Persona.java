package Models;

import java.io.Serializable;

public class Persona implements Serializable {
    private int id;
    private String nombre;
    private String telefono;
    private String latitud;
    private String longitud;
    private String firma;

    // Constructor
    public Persona(String nombre, String telefono, String latitud, String longitud, String firma, int id) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.firma = firma;
    }

    public Persona() {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.firma = firma;
    }


    // Getters
    public int getId() { return id;}

    public void getId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String fotoBase64) {
        this.firma = fotoBase64;
    }
}
