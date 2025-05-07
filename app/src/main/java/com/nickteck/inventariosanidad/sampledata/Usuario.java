package com.nickteck.inventariosanidad.sampledata;

public class Usuario {
    private String nombre;
    private String contra;

    public Usuario(String nombre, String contra) {
        this.nombre = nombre;
        this.contra = contra;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getContra() {
        return contra;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }
}
