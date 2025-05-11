package com.nickteck.inventariosanidad.sampledata;

public interface UsuarioCallback2 {
    void onUsuarioObtenido(Usuario usuario); // El método que se llama con el resultado final

    void onFailure(boolean error);
}
