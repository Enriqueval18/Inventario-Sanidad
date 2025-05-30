package com.nickteck.inventariosanidad.sampledata;

public class Usuario {
    private int user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String user_type;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Usuario(String first_name) {
        this.first_name = first_name;

    }

    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Usuario(String first_name, String password, String user_type) {
        this.first_name = first_name;
        this.password = password;
        this.user_type = user_type;
    }

    private Usuario(int user_id, String first_name, String last_name, String email, String password, String user_type) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
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

    public Usuario(int user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String apellido) {
        this.email = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

}