package com.nickteck.inventariosanidad.sampledata;

public class Material {
    String nombre;
    int id;
    String descripcion;
    int unidades;
    int unidades_min;
    String almacen;
    String armario;
    String estante;
    String cajon;
    String tipo;
    public Material() {

    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCajon() {
        return cajon;
    }

    public void setCajon(String cajon) {
        this.cajon = cajon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Material(String nombre, String descripcion, int unidades, int unidades_min, String almacen, String armario, String estante) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidades = unidades;
        this.unidades_min = unidades_min;
        this.almacen = almacen;
        this.armario = armario;
        this.estante = estante;
    }

    public Material(String nombre, int id, String descripcion, int unidades, int unidades_min, String almacen, String armario, String estante, String cajon,String tipo) {
        this.nombre = nombre;
        this.id = id;
        this.descripcion = descripcion;
        this.unidades = unidades;
        this.unidades_min = unidades_min;
        this.almacen = almacen;
        this.armario = armario;
        this.estante = estante;
        this.cajon = cajon;
        this.tipo=tipo;
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

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public String getArmario() {
        return armario;
    }

    public void setArmario(String armario) {
        this.armario = armario;
    }

    public String getEstante() {
        return estante;
    }

    public void setEstante(String estante) {
        this.estante = estante;
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
