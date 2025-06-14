package com.nickteck.inventariosanidad.sampledata;

public class Respuesta {
    private boolean respuesta;
    private String mensaje;
    private int usuario_id;
    private String descripciones;
    private String unidades;
    private String materiales;
    private String enviados;
    private String activity_ids;

    public String getEnviados() {
        return enviados;
    }

    public void setEnviados(String enviados) {
        this.enviados = enviados;
    }


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

    public Respuesta(String descripciones, String unidades, String materiales, String enviados,String activity_ids) {
        this.descripciones = descripciones;
        this.unidades = unidades;
        this.materiales = materiales;
        this.enviados = enviados;
        this.activity_ids=activity_ids;
    }

    public Respuesta(boolean respuesta, String mensaje, String descripciones, String unidades, String materiales, String enviados) {
        this.respuesta = respuesta;
        this.mensaje = mensaje;
        this.descripciones = descripciones;
        this.unidades = unidades;
        this.materiales = materiales;
        this.enviados = enviados;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getDescripciones() {
        return descripciones;
    }

    public void setDescripciones(String descripciones) {
        this.descripciones = descripciones;
    }

    public String getUnidades() {
        return unidades;
    }

    public void setUnidades(String unidades) {
        this.unidades = unidades;
    }

    public String getMateriales() {
        return materiales;
    }

    public void setMateriales(String materiales) {
        this.materiales = materiales;
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

    public String getActivity_ids() {
        return activity_ids;
    }

    public void setActivity_ids(String activity_ids) {
        this.activity_ids = activity_ids;
    }
}
