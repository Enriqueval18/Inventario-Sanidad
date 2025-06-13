package com.nickteck.inventariosanidad.sampledata;

import java.util.ArrayList;

public class Respuesta {
    private boolean respuesta;
    private String mensaje;
    private int usuario_id;
    private String  descripcion;
    private ArrayList<Integer> unidades;
    private ArrayList<Integer> material_ids;
    public Respuesta(String mensaje) {
        this.mensaje = mensaje;
    }

    public Respuesta(boolean respuesta) {
        this.respuesta = respuesta;
    }

    public Respuesta(boolean respuesta, String mensaje) {
        this.respuesta = respuesta;
        this.mensaje = mensaje;
    }

    public Respuesta(int usuario_id, String descripcion, ArrayList<Integer> unidades, ArrayList<Integer> material_ids) {
        this.usuario_id = usuario_id;
        this.descripcion = descripcion;
        this.unidades = unidades;
        this.material_ids = material_ids;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<Integer> getUnidades() {
        return unidades;
    }

    public void setUnidades(ArrayList<Integer> unidades) {
        this.unidades = unidades;
    }

    public ArrayList<Integer> getMaterial_ids() {
        return material_ids;
    }

    public void setMaterial_ids(ArrayList<Integer> material_ids) {
        this.material_ids = material_ids;
    }

    public boolean isRespuesta() {
        return respuesta;
    }

    public void setRespuesta(boolean respuesta) {
        this.respuesta = respuesta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
