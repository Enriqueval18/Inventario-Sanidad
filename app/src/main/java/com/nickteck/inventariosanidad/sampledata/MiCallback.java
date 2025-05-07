package com.nickteck.inventariosanidad.sampledata;

/**
 * Esta clase implementa UsuarioCallback.
 * Aquí defines lo que quieres hacer cuando ya tengas el resultado:
 * imprimir si el usuario existe o no, guardar datos, etc.
 */
public class MiCallback implements UsuarioCallback {
    @Override
    public void onResultado(boolean existe) {
        if (existe) {
            System.out.println("✅ El usuario existe");
        } else {
            System.out.println("❌ El usuario NO existe");
        }
    }



    @Override
    public void onFailure(boolean error) {

    }
}