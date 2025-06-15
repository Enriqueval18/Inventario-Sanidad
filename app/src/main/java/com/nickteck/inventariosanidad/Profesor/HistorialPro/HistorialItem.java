package com.nickteck.inventariosanidad.Profesor.HistorialPro;

public class HistorialItem {
    private final String time;
    private final String student;
    private final String material;
    private final String cantidad;

    public HistorialItem(String time, String student, String material, String cantidad) {
        this.time = time;
        this.student = student;
        this.material = material;
        this.cantidad = cantidad;
    }

    public String getTime() {
        return time;
    }

    public String getStudent() {
        return student;
    }

    public String getMaterial() {
        return material;
    }
    public String getCantidad(){
        return cantidad;
    }
}
