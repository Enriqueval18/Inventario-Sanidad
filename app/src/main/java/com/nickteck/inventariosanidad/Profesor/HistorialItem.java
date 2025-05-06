package com.nickteck.inventariosanidad.Profesor;

public class HistorialItem {
    private String time;
    private String student;
    private String material;
    private String cantidad;

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
