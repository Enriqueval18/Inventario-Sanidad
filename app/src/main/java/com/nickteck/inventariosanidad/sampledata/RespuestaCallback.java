package com.nickteck.inventariosanidad.sampledata;

public interface RespuestaCallback {
    void onResultado(boolean correcto); // El método que se llama con el resultado final

    void onFailure(boolean error);

}
