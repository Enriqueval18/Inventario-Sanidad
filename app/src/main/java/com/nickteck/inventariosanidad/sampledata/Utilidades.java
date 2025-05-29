package com.nickteck.inventariosanidad.sampledata;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;                             // Representa una solicitud HTTP (GET, POST, etc.)
import retrofit2.Callback;                         // Interfaz para manejar respuestas asíncronas
import retrofit2.Response;                         // Contiene la respuesta del servidor (JSON, código, etc.)
import retrofit2.Retrofit;                         // Clase principal de Retrofit, se encarga de configurar todo
import retrofit2.converter.gson.GsonConverterFactory; // Convierte JSON recibido en objetos Java automáticamente
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;                         // Indica que queremos hacer una petición GET
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class Utilidades {

    // Esta es la URL base de tu servidor donde está alojada la API.
    // Importante: debe terminar con una barra "/" para que Retrofit combine correctamente las rutas.
    private static final String BASE_URL = "https://inventariosan.ifpleonardo.com/web/";

    /**
     * Método estático que hace una petición a la API para verificar si un usuario existe.
     * @param usuario El nombre del usuario que queremos buscar en la base de datos.
     * @param callback2 Un objeto que recibirá el resultado (true o false) cuando la API responda.
     */
    public static  void verificarUsuario(Usuario usuario,UsuarioCallback2 callback2){
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
                    Log.d("LoginResponse", "no es nulo ");

                    Usuario usuario = response.body();
                    String tipo = usuario.getUser_type();
                    Log.d("LoginResponse", "Tipo recibido: " + tipo);

                    try {
                        if ("no existe".equals(tipo)) {
                            Log.w("LoginResultado", "Usuario no encontrado en la base de datos.");
                            callback2.onUsuarioObtenido(usuario);
                        } else {
                            Log.i("LoginResultado", "Usuario encontrado. Tipo: " + tipo);
                            callback2.onUsuarioObtenido(usuario);
                        }

                    } catch (Exception e) {
                        Log.e("LoginError", "Error al procesar el JSON de la respuesta", e);

                    }

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Este método se ejecuta si la petición no se pudo hacer (fallo de red, sin internet, etc.)
                Log.e("falla", "Error en la comunicación: " + t.getMessage());
                // Avisamos que no se pudo verificar el usuario (asumimos que no existe)

                callback2.onFailure(true);
            }
        });
    }
    public static void obtenerMateriales(final MaterialCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<List<Material>> call = api.obtenerMaterial();
        Log.d("Respuesta", "Solicitud creada. Llamando a la API...");

        call.enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                Log.d("Respuesta", "Código de respuesta: " + response.code());
                Log.d("Respuesta", "Cuerpo de la respuesta: " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    List<Material> listaMateriales = response.body();
                    Log.d("Materiales", "Cantidad de materiales obtenidos: " + listaMateriales.size());
                    for (Material material : listaMateriales) {
                        Log.d("Material", "Material recibido: " + material.getNombre() + " - " + material.getAlmacen() + " - " + material.getArmario() + " - " + material.getUnidades());

                        callback.onMaterialObtenido(
                                material.getNombre(),
                                material.getUnidades(),
                                material.getAlmacen(),
                                material.getArmario(),
                                material.getEstante(),
                                material.getUnidades_min(),
                                material.getDescripcion()
                        );
                    }
                } else {
                    Log.w("Materiales", "Respuesta no exitosa o vacía. Código de respuesta: " + response.code());
                    callback.onFailure(true);
                }
            }
            @Override
            public void onFailure(Call<List<Material>> call, Throwable t) {
                Log.e("MaterialesError", "Error en la comunicación: " + t.getMessage());
                callback.onFailure(true);
            }
        });
    }
//    public static void editarUsuarios(Usuario usuario, String nombreAntiguo ,RespuestaCallback callback) {
//
//        // Paso 1: Crear la instancia de Retrofit con configuración básica
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
//                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
//                .build(); // Creamos la instancia final de Retrofit
//
//
//        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
//        ApiService api = retrofit.create(ApiService.class);
//
//        Log.d("EditarUsuario", "Usuario a editar: " + nombreAntiguo);
//
//// Llamar a la función para eliminar el usuario de la base de datos
//        Call<Respuesta> call = api.editarUsuarios(usuario,nombreAntiguo);
//        Log.d("EditarUsuario", "URL de la solicitud: " + call.request().url().toString());
//
//        call.enqueue(new Callback<Respuesta>() {
//            @Override
//            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
//                Log.d("EditarUsuario", "Código de respuesta: " + response.code());
//                Log.d("EditarUsuario", "Cuerpo de la respuesta: " + response.body());
//
//
//                if (response.isSuccessful() && response.body() != null) {
//                    Respuesta recibido = response.body();
//                    if (recibido.isRespuesta()) {
//                        Log.d("respuestaEditar", "Modificacion exitosa");
//                        callback.onResultado(true);
//                    } else {
//                        Log.d("respuestaEditar", "Error desde el servidor: " + recibido.isRespuesta()+recibido.getError());
//                        callback.onResultado(false);
//                    }
//                } else {
//                    Log.d("EditarUsuario", "Error en la respuesta HTTP: " + response.message());
//                    callback.onResultado(false);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Respuesta> call, Throwable t) {
//                Log.e("EditarUsuario", "Error al editar: " + t.getMessage());
//                callback.onFailure(true);
//            }
//        });
//
//    }
    public static void añadirUsuario(Usuario usuario, RespuestaCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        // Log para depuración
        Log.d("AñadirUsuario", "Datos a enviar: " +
                "Nombre: " + usuario.getFirst_name() + ", " +
                "Apellido: " + usuario.getLast_name() + ", " +
                "Email: " + usuario.getEmail() + ", " +
                "Tipo: " + usuario.getUser_type());

        Call<Respuesta> call = api.añadirUsuario(usuario);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                Log.d("AñadirUsuario", "Código respuesta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("AñadirUsuario", "Respuesta del servidor: " + recibido.isRespuesta());

                    if (recibido.isRespuesta()) {
                        callback.onResultado(true);
                    } else {
                        // Verificar si hay mensaje de error
                        if (recibido.getMensaje() != null) {
                            Log.e("AñadirUsuario", "Error del servidor: " + recibido.getMensaje());
                        }
                        callback.onResultado(false);
                    }
                } else {
                    try {
                        Log.e("AñadirUsuario", "Respuesta no exitosa. Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onResultado(false);
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("AñadirUsuario", "Error en la comunicación: " + t.getMessage(), t);
                callback.onFailure(true);
            }
        });

    }
//    public static void eliminarUsuario(Usuario usuario, RespuestaCallback callback) {
//
//        // Paso 1: Crear la instancia de Retrofit con configuración básica
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
//                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
//                .build(); // Creamos la instancia final de Retrofit
//
//        Log.d("crearConexion", "Usuario a eliminar: " + usuario.getEmail() + "nombre: " + usuario.getFirst_name());
//
//        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
//        ApiService api = retrofit.create(ApiService.class);
//
//        Log.d("EliminarUsuario", "Usuario a eliminar: " + usuario.getEmail() + "nombre: " + usuario.getFirst_name());
//
//// Llamar a la función para eliminar el usuario de la base de datos
//        Call<Respuesta> call = api.eliminarUsuario(usuario.getEmail());
//        Log.d("EliminarUsuario", "URL de la solicitud: " + call.request().url().toString());
//
//        call.enqueue(new Callback<Respuesta>() {
//            @Override
//            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
//                Log.d("EliminarUsuario", "Código de respuesta: " + response.code());
//                Log.d("EliminarUsuario", "Cuerpo de la respuesta: " + response.body());
//
//
//                if (response.isSuccessful() && response.body() != null) {
//                    Respuesta recibido = response.body();
//                    if (recibido.isRespuesta()) {
//                        Log.d("respuestaEliminar", "Eliminación exitosa");
//                        callback.onResultado(true);
//                    } else {
//                        Log.d("respuestaEliminar", "Error desde el servidor: " + recibido.isRespuesta()+recibido.getError());
//                        callback.onResultado(false);
//                    }
//                } else {
//                    Log.d("EliminarUsuario", "Error en la respuesta HTTP: " + response.message());
//                    callback.onResultado(false);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Respuesta> call, Throwable t) {
//                Log.e("EliminarUsuario", "Error al eliminar: " + t.getMessage());
//                callback.onFailure(true);
//            }
//        });
//
//    }
    public static void mostrarUsuarios(final UsuarioListCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<List<Usuario>> call = api.obtenerUsuarios();

        Log.d("Respuesta", "Solicitud creada. Llamando a la API...");

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                Log.d("Respuesta", "Código de respuesta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> listaUsuarios = response.body();
                    Log.d("Usuarios", "Cantidad de usuarios obtenidos: " + listaUsuarios.size());
                    callback.onUsuariosObtenidos(listaUsuarios);
                } else {
                    Log.w("Usuarios", "Respuesta no exitosa o vacía");
                    callback.onFailure(true);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("UsuariosError", "Error en la comunicación: " + t.getMessage());
                callback.onFailure(true);
            }
        });
    }
    public static void validarMaterial(final Context context, final String materialIngresado, final MaterialCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<List<Material>> call = api.obtenerMaterial();  // Devuelve una lista de materiales
        Log.d("Respuesta", "Solicitud creada. Llamando a la API...");

        call.enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                Log.d("Respuesta", "Código de respuesta: " + response.code());
                Log.d("Respuesta", "Cuerpo de la respuesta: " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    List<Material> listaMateriales = response.body();
                    Log.d("Materiales", "Cantidad de materiales obtenidos: " + listaMateriales.size());

                    boolean encontrado = false;
                    // Recorrer la lista para ver si existe el material ingresado
                    for (Material material : listaMateriales) {
                        Log.d("Material", "Material recibido: "
                                + material.getNombre() + " - " + material.getDescripcion());

                        // Se compara ignorando mayúsculas/minúsculas
                        if (material.getNombre().equalsIgnoreCase(materialIngresado)) {
                            encontrado = true;
                            // Material encontrado; se invoca el callback con los datos
                            callback.onMaterialObtenido(
                                    material.getNombre(),
                                    material.getUnidades(),
                                    material.getAlmacen(),
                                    material.getArmario(),
                                    material.getEstante(),
                                    material.getUnidades_min(),
                                    material.getDescripcion()
                            );
                            break;
                        }
                    }

                    // Si no se encontró el material, se muestra un mensaje y se notifica error
                    if (!encontrado) {
                        Toast.makeText(context, "El material ingresado no existe", Toast.LENGTH_SHORT).show();
                        callback.onFailure(true);
                    }
                } else {
                    Log.w("Materiales", "Respuesta no exitosa o vacía. Código de respuesta: " + response.code());
                    callback.onFailure(true);
                }
            }

            @Override
            public void onFailure(Call<List<Material>> call, Throwable t) {
                // Si hay un error de comunicación, se muestra el error en los logs y se notifica mediante callback
                Log.e("MaterialesError", "Error en la comunicación: " + t.getMessage());
                callback.onFailure(true);
            }
        });
    }

    /**
         * Esta es la interfaz que Retrofit usa para definir cómo hacer las peticiones HTTP.
         * Retrofit la implementa automáticamente usando las anotaciones que tú le pongas.
         * NO tienes que implementar esta interfaz tú mismo.
         * @QUERY SE USA CON GET SI QUEREMOS ENVIAR DATOS EN LA URL
         * @FIELD / @BODY SE USA CON POST
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
        @POST("general/Login.php")
        Call<Usuario> verificarUsuario(@Body Usuario usuario);

        @GET("usuario/ObtenerMateriales.php")
        Call<List<Material>> obtenerMaterial();  // Esta vez esperamos un List<Material> directamente

        @POST("admin/AccionesSobreUsuario.php")
        Call<Respuesta>añadirUsuario(@Body Usuario nuevoUsuario);


        // Usamos DELETE y pasamos el 'email' como parte del path
        @DELETE("admin/AccionesSobreUsuario.php")
        Call<Respuesta> eliminarUsuario(@Query("email") String emailUsuario);

        @GET("admin/AccionesSobreUsuario.php")

        Call<List<Usuario>> obtenerUsuarios();


        @PUT("admin/editarUsuario.php")
        Call<Respuesta> editarUsuarios(@Body Usuario nuevoUsuario,@Query("nombre_antiguo") String nombreUsuario);


    }

    public static void getMaterialList(final MaterialListCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)  // Asegúrate de que BASE_URL esté definido correctamente
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<List<Material>> call = api.obtenerMaterial();
        call.enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                if (response.isSuccessful() && response.body() != null)
                    callback.onSuccess(response.body());
                else
                    callback.onFailure();
            }
            @Override
            public void onFailure(Call<List<Material>> call, Throwable t) {
                callback.onFailure();
            }
        });
    }




}