package com.nickteck.inventariosanidad.sampledata;

import java.util.List;

public interface MaterialListCallback {
    void onSuccess(List<Material> materialList);
    void onFailure();
}
