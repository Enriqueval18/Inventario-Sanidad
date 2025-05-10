package com.nickteck.inventariosanidad.sampledata;

public class Respuesta {
    private boolean respuesta;
    private String error; // <-- Este campo debe coincidir exactamente con el JSON: "error"

    public Respuesta(boolean respuesta, String error) {
        this.respuesta = respuesta;
        this.error = error;
    }

    public boolean isRespuesta() {
        return respuesta;
    }

    public void setRespuesta(boolean respuesta) {
        this.respuesta = respuesta;
    }

    public String getError() {  // <-- Agrega getter
        return error;
    }

    public void setError(String error) {  // <-- Agrega setter
        this.error = error;
    }
}
