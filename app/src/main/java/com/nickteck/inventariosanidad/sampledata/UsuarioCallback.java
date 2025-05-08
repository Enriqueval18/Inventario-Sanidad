package com.nickteck.inventariosanidad.sampledata;

/**
 * Esta interfaz actúa como una forma de "devolver datos" desde una tarea asíncrona como Retrofit.
 * En vez de usar return true/false (que no se puede en procesos asíncronos),
 * usas esta interfaz para enviar el resultado cuando esté listo.
 */
public interface UsuarioCallback {
    void onResultado(String tipo); // El método que se llama con el resultado final


    void onFailure(boolean error);
}