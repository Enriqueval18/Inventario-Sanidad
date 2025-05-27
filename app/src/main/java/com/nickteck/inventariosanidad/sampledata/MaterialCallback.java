package com.nickteck.inventariosanidad.sampledata;

public interface MaterialCallback {
    void onMaterialObtenido(String nombre, int unidades, String alamacen, String armario, String estante, int unidades_min, String descripcion);
    void onFailure(boolean error);
}

