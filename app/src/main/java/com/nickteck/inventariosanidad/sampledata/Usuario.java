package com.nickteck.inventariosanidad.sampledata;

public class Usuario {
    private String nombre;
    private String apellido;
    private String correo;
    private String contra;
    private String tipo;

    public Usuario(String nombre) {
        this.nombre = nombre;

    }

    public Usuario(String correo, String contra) {
        this.correo = correo;
        this.contra = contra;
    }

    public Usuario(String nombre, String contra,String tipo) {
        this.nombre = nombre;
        this.contra = contra;
        this.tipo = tipo;
    }

    //este se usa para crear el usuario en la base de datos con las tablas nuevas
    //por si acaso estos campos estan como:
    //first_name,  last_name, email, password, user_type
    public Usuario(String nombre, String apellido, String correo, String contra, String tipo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contra = contra;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}