package com.nickteck.inventariosanidad.sampledata;

public interface RespuestaFinalCallback {

    void onResultado(Respuesta respuesta); // El método que se llama con el resultado final

    void onFailure(boolean error);


}
