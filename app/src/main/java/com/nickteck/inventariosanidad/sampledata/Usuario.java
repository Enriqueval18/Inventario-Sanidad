package com.nickteck.inventariosanidad.sampledata;

public class Usuario {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String user_type;

    public Usuario(String first_name) {
        this.first_name = first_name;

    }

    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Usuario(String first_name, String password,String user_type) {
        this.first_name = first_name;
        this.password = password;
        this.user_type = user_type;
    }

    //este se usa para crear el usuario en la base de datos con las tablas nuevas
    //por si acaso estos campos estan como:
    //first_name,  last_name, email, password, user_type
    public Usuario(String first_name, String last_name, String email, String password, String user_type) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.user_type = user_type;
    }

    public String getNombre() {
        return first_name;
    }

    public void setNombre(String first_name) {
        this.first_name = first_name;
    }

    public String getApellido() {
        return last_name;
    }

    public void setApellido(String last_name) {
        this.last_name = last_name;
    }

    public String getCorreo() {
        return email;
    }

    public void setCorreo(String email) {
        this.email = email;
    }

    public String getContra() {
        return password;
    }

    public void setContra(String password) {
        this.password = password;
    }

    public String getTipo() {
        return user_type;
    }

    public void setTipo(String user_type) {
        this.user_type = user_type;
    }

}