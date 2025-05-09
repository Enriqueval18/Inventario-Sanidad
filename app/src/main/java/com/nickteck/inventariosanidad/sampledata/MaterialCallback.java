package com.nickteck.inventariosanidad.sampledata;

public interface MaterialCallback {
    void onMaterialObtenido(Material material);

    void onFailure(boolean b);
}
