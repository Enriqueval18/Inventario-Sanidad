package com.nickteck.inventariosanidad.sampledata;

public class Usuario {
    private String nombre;
    private String contra;
    private String tipo;

    public Usuario(String nombre, String contra) {
        this.nombre = nombre;
        this.contra = contra;
    }
    public Usuario(String nombre) {
        this.nombre = nombre;

    }
    public Usuario(String nombre, String contra,String tipo) {
        this.nombre = nombre;
        this.contra = contra;
        this.tipo = tipo;
    }
    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getContra() {
        return contra;
    }



    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }
}