package com.nickteck.inventariosanidad.Profesor.MaterialesPro;

public class MaterialItem {
    private String nombre;
    private String cantidad;

    public MaterialItem(String nombre, String cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCantidad() {
        return cantidad;
    }
}

