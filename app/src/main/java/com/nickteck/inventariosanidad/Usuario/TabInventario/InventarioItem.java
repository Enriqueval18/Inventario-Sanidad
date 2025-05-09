package com.nickteck.inventariosanidad.Usuario.TabInventario;

public class InventarioItem {
    private String nombre;
    private int unidades;
    private String almacen;
    private String armario;

    public InventarioItem() {}

    public InventarioItem(String nombre, int unidades, String almacen, String armario) {
        this.nombre = nombre;
        this.unidades = unidades;
        this.almacen = almacen;
        this.armario = armario;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getUnidades() {
        return unidades;
    }
    public void setUnidades(int unidades) {
        this.unidades = unidades;
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
}