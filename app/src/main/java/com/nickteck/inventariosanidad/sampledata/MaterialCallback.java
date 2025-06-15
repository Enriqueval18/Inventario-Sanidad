package com.nickteck.inventariosanidad.sampledata;

public interface MaterialCallback {
    void onMaterialObtenido(int material_id,String nombre, int unidades, String alamacen, String armario, String estante, int unidades_min, String descripcion,String tipo);
    void onFailure(boolean error);
}

