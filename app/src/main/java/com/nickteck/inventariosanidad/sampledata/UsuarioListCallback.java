package com.nickteck.inventariosanidad.sampledata;

import java.util.List;

public interface UsuarioListCallback {
    void onUsuariosObtenidos(List<Usuario> usuarios);
    void onFailure(boolean error);
}