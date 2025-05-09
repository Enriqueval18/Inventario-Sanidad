package com.nickteck.inventariosanidad.Usuario.TabInventario;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface InventarioAPI {
    // Actualiza "inventario" por el endpoint que te indique tu compañero
    @GET("inventario")
    Call<List<InventarioItem>> getInventario();
}
