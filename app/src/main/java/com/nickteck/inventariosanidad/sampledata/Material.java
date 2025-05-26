package com.nickteck.inventariosanidad.sampledata;

public class Material {
    String nombre;
    String descripcion;
    int unidades;
    int unidades_min;
    String alamacen;
    String armario;
    String balda;
    String cajon;
    public Material() {

    }

    public String getCajon() {
        return cajon;
    }

    public void setCajon(String cajon) {
        this.cajon = cajon;
    }

    public Material(String nombre, String descripcion, int unidades, int unidades_min, String alamacen, String armario, String balda) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidades = unidades;
        this.unidades_min = unidades_min;
        this.alamacen = alamacen;
        this.armario = armario;
        this.balda = balda;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public int getUnidades_min() {
        return unidades_min;
    }

    public void setUnidades_min(int unidades_min) {
        this.unidades_min = unidades_min;
    }

    public String getAlamacen() {
        return alamacen;
    }

    public void setAlamacen(String alamacen) {
        this.alamacen = alamacen;
    }

    public String getArmario() {
        return armario;
    }

    public void setArmario(String armario) {
        this.armario = armario;
    }

    public String getBalda() {
        return balda;
    }

    public void setBalda(String balda) {
        this.balda = balda;
    }

    public Material(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
