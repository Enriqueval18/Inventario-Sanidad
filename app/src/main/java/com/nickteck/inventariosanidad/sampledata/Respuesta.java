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
    private  String nombre_usuario;
    private  String tipo_usuario;
    private int unidades_modificacion;
    private String fecha_modificacion;
    private String peticiones_ids;
    private  String unidades_peticiones;
    public String getEnviados() {
        return enviados;
    }

    public void setEnviados(String enviados) {
        this.enviados = enviados;
    }

    public String getPeticiones_ids() {
        return peticiones_ids;
    }

    public void setPeticiones_ids(String peticiones_ids) {
        this.peticiones_ids = peticiones_ids;
    }

    public String getUnidades_peticiones() {
        return unidades_peticiones;
    }

    public void setUnidades_peticiones(String unidades_peticiones) {
        this.unidades_peticiones = unidades_peticiones;
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

    public Respuesta(boolean respuesta, String mensaje,  String materiales,  String nombre_usuario, String tipo_usuario, int unidades_modificacion, String fecha_modificacion) {
        this.respuesta = respuesta;
        this.mensaje = mensaje;
        this.materiales = materiales;
        this.nombre_usuario = nombre_usuario;
        this.tipo_usuario = tipo_usuario;
        this.unidades_modificacion = unidades_modificacion;
        this.fecha_modificacion = fecha_modificacion;
    }


    public Respuesta(boolean respuesta, String mensaje,   String materiales,   String nombre_usuario,   String fecha_modificacion, String peticiones_ids, String unidades_peticiones) {
        this.respuesta = respuesta;
        this.mensaje = mensaje;
         this.materiales = materiales;
         this.nombre_usuario = nombre_usuario;
         this.fecha_modificacion = fecha_modificacion;
        this.peticiones_ids = peticiones_ids;
        this.unidades_peticiones = unidades_peticiones;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public int getUnidades_modificacion() {
        return unidades_modificacion;
    }

    public void setUnidades_modificacion(int unidades_modificacion) {
        this.unidades_modificacion = unidades_modificacion;
    }

    public String getFecha_modificacion() {
        return fecha_modificacion;
    }

    public void setFecha_modificacion(String fecha_modificacion) {
        this.fecha_modificacion = fecha_modificacion;
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
