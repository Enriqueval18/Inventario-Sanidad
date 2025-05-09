package com.nickteck.inventariosanidad.sampledata;

import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import retrofit2.Call;                             // Representa una solicitud HTTP (GET, POST, etc.)
import retrofit2.Callback;                         // Interfaz para manejar respuestas asíncronas
import retrofit2.Response;                         // Contiene la respuesta del servidor (JSON, código, etc.)
import retrofit2.Retrofit;                         // Clase principal de Retrofit, se encarga de configurar todo
import retrofit2.converter.gson.GsonConverterFactory; // Convierte JSON recibido en objetos Java automáticamente
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;                         // Indica que queremos hacer una petición GET
import retrofit2.http.POST;
import retrofit2.http.Query;                       // Permite agregar parámetros a la URL como ?nombre=juan

public class Utilidades {

    // Esta es la URL base de tu servidor donde está alojada la API.
    // Importante: debe terminar con una barra "/" para que Retrofit combine correctamente las rutas.
    private static final String BASE_URL = "https://inventariosan.ifpleonardo.com/web/";

    /**
     * Método estático que hace una petición a la API para verificar si un usuario existe.
     * @param usuario El nombre del usuario que queremos buscar en la base de datos.
     * @param callback Un objeto que recibirá el resultado (true o false) cuando la API responda.
     */
    public static void verificarUsuario(Usuario usuario,String contra, UsuarioCallback callback) {

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit


        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        // ⚠️ NO usamos "implements" aquí, Retrofit lo hace por nosotros.
        // Usa reflexión para analizar las anotaciones @GET y @Query, y genera el código necesario internamente.
        ApiService api = retrofit.create(ApiService.class);
        Log.e("Verificar usaurio ","coencta apiservidce.clas");



        // Paso 3: Usamos ese objeto para llamar al método definido en la interfaz.
        // Esto crea una solicitud HTTP de tipo GET a la URL: api/usuario.php?nombre=juan123 (por ejemplo).
        // Este objeto "call" representa la solicitud, pero todavía no la envía.
        Call<Usuario> call = api.verificarUsuario(usuario);
        Log.e("nuevo verificar","crea la solicitud ");

        // Paso 4: Ejecutamos la solicitud de forma asíncrona (no bloquea el hilo principal).
        // Retrofit se encargará de hacer la llamada en segundo plano.
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // Este método se ejecuta si el servidor respondió, aunque sea con error.

                // Verificamos si la respuesta fue correcta (código 200–299)
                // Y además que haya datos (el cuerpo no sea null)
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    String tipo = usuario.getTipo();
                    Log.d("LoginResponse", "Tipo recibido: " + tipo);

                    try {
                        if ("no existe".equals(tipo)) {
                            Log.w("LoginResultado", "Usuario no encontrado en la base de datos.");
                            callback.onResultado("false");
                        } else {
                            Log.i("LoginResultado", "Usuario encontrado. Tipo: " + tipo);
                            callback.onResultado(tipo);
                        }

                    } catch (Exception e) {
                        Log.e("LoginError", "Error al procesar el JSON de la respuesta", e);
                        callback.onResultado("false");
                    }

                } else {
                    Log.w("LoginResultado", "Respuesta no exitosa o cuerpo vacío. Código: " + response.code());
                    callback.onResultado("false");
                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Este método se ejecuta si la petición no se pudo hacer (fallo de red, sin internet, etc.)
                Log.e("falla", "Error en la comunicación: " + t.getMessage());
                // Avisamos que no se pudo verificar el usuario (asumimos que no existe)

                callback.onFailure(true);
            }
        });
    }



    /**
     * Esta es la interfaz que Retrofit usa para definir cómo hacer las peticiones HTTP.
     * Retrofit la implementa automáticamente usando las anotaciones que tú le pongas.
     * NO tienes que implementar esta interfaz tú mismo.
     */
    private interface ApiService {

        // Anotación que indica que queremos hacer una petición GET a esta ruta: api/usuario.php
        // El parámetro "nombre" se agregará a la URL como un parámetro de consulta (?nombre=...)
        // Retrofit se encargará de formar correctamente la URL final.
        // @GET("api/usuario.php")
        // Call<Usuario> obtenerUsuario(@Query("nombre") String nombre);

        // Esta anotación indica que vamos a hacer una solicitud POST
        // y enviar un objeto 'Usuario' en el cuerpo de la solicitud
        // @POST("api/usuario.php")
        //Call<Usuario> verificarUsuario(@Field("nombre") String nombre); // El parámetro "nombre" que se enviará
        @POST("index.php")
        Call<Usuario> verificarUsuario(@Body Usuario usuario);


    }



}