package com.nickteck.inventariosanidad.sampledata;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.nickteck.inventariosanidad.R;
import java.io.IOException;
import java.util.ArrayList;
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
    private static final String BASE_URL = "https://inventariosan.ifpleonardo.com/web/";

    /**
     * Método estático que hace una petición a la API para verificar si un usuario existe.
     * @param usuario El nombre del usuario que queremos buscar en la base de datos.
     * @param callback2 Un objeto que recibirá el resultado (true o false) cuando la API responda.
     */
    public static void verificarUsuario(Usuario usuario, UsuarioCallback2 callback2, ErrorDisplayer errorDisplayer) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<Usuario> call = api.verificarUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // 1) Si no es 200… informar y fallo
                if (!response.isSuccessful()) {
                    errorDisplayer.mostrarMensaje(R.string.error_servidor);
                    callback2.onFailure(true);
                    return;
                }

                Usuario body = response.body();
                // 2) Si el body es nulo… informar y fallo
                if (body == null) {
                    errorDisplayer.mostrarMensaje(R.string.error_respuesta);
                    callback2.onFailure(true);
                    return;
                }

                // 3) Si la API te devuelve un campo “error” no nulo…
                if (body.getError() != null) {
                    switch (body.getError()) {
                        case "usuario no encontrado":
                            errorDisplayer.mostrarMensaje(R.string.usuario_no_encontrado);
                            break;
                        case "contraseña incorrecta":
                            errorDisplayer.mostrarMensaje(R.string.contrasena_incorrecta);
                            break;
                        default:
                            errorDisplayer.mostrarMensaje(R.string.usu_con_inco);
                    }
                    callback2.onFailure(true);
                    return;
                }

                // 4) OK: invocamos éxito
                callback2.onUsuarioObtenido(body);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("verificarUsuario", "Error en comunicación", t);
                callback2.onFailure(true);
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------//
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
                        Log.d("Material", "Material recibido: " + material.getNombre() + " - " + material.getAlmacen() + " - " + material.tipo + " - " + material.getUnidades());

                        callback.onMaterialObtenido(
                                material.getId(),
                                material.getNombre(),
                                material.getUnidades(),
                                material.getAlmacen(),
                                material.getArmario(),
                                material.getEstante(),
                                material.getUnidades_min(),
                                material.getDescripcion(),
                                material.getTipo()
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

    //----------------------------------------------------------------------------------------------------------------//




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
    public static void eliminarUsuario(Usuario usuario, RespuestaCallback callback) {

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Usuario a eliminar: " + usuario.getEmail() + "nombre: " + usuario.getFirst_name());

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

        Log.d("EliminarUsuario", "Usuario a eliminar: " + usuario.getEmail() + "nombre: " + usuario.getFirst_name());

// Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.eliminarUsuario(usuario.getEmail());
        Log.d("EliminarUsuario", "URL de la solicitud: " + call.request().url().toString());

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                Log.d("EliminarUsuario", "Código de respuesta: " + response.code());
                Log.d("EliminarUsuario", "Cuerpo de la respuesta: " + response.body());


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    if (recibido.isRespuesta()) {
                        Log.d("respuestaEliminar", "Eliminación exitosa");
                        callback.onResultado(true);
                    } else {
                        callback.onResultado(false);
                    }
                } else {
                    Log.d("EliminarUsuario", "Error en la respuesta HTTP: " + response.message());
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("EliminarUsuario", "Error al eliminar: " + t.getMessage());
                callback.onFailure(true);
            }
        });

    }
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
                                    material.getId(),
                                    material.getNombre(),
                                    material.getUnidades(),
                                    material.getAlmacen(),
                                    material.getArmario(),
                                    material.getEstante(),
                                    material.getUnidades_min(),
                                    material.getDescripcion(),
                                    material.getTipo()
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
     * Método que realiza una solicitud PUT para actualizar la contraseña de un usuario.
     * @param usuario Objeto Usuario que contiene user_id y la nueva contraseña.
     * @param callback Callback para manejar el resultado de la solicitud.
     */
    public static void actualizarContra(Usuario usuario, UsuarioCallback callback) {
        // 1. Crear Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 2. Crear instancia de ApiService
        ApiService api = retrofit.create(ApiService.class);

        // 3. Llamar al método definido en la interfaz (PUT)
        Call<Respuesta> call = api.actualizarContra(usuario); // Asumimos que el PHP devuelve un JSON con campos como "mensaje" o "error"

        // 4. Ejecutar llamada asíncrona
        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Respuesta respuesta = response.body();

                    if (respuesta.getMensaje() != null) {
                        Log.i("ActualizarPassword", "Éxito: " + respuesta.getMensaje());

                    } else {
                        Log.w("ActualizarPassword", "Error reportado: " + respuesta.getMensaje());

                    }
                } else {
                    Log.e("ActualizarPassword", "Respuesta no exitosa");
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("ActualizarPassword", "Fallo de red: " + t.getMessage());
            }
        });
}




    public static void usarMaterialesProfesor(int user_id,int material_id, int units, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Material a usar " + material_id + " usuario id: " + user_id);

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

// Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.usarMaterial_Profesor(user_id,material_id,units);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("MATERIALOG", recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        Log.d("MATERIALOG", "se uso correctamente");
                        callback.onResultado(true);
                    } else {
                        Log.d("MATERIALOG", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                     callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("MOverMaterial", "no se logro enviar la solicitud" + t.getMessage());
                callback.onFailure(true);
            }
        });

    }

    public static void quitarMaterialesUsuarios(int user_id,int material_id, int units, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Material a usar " + material_id + " usuario id: " + user_id);

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

// Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.quitar_Material_Usuario(user_id,material_id,units);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("MATERIALOG", recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        Log.d("MATERIALOG", "se uso correctamente");
                        callback.onResultado(true);
                    } else {
                        Log.d("MATERIALOG", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("MOverMaterial", "no se logro enviar la solicitud" + t.getMessage());
                callback.onFailure(true);
            }
        });

    }
    public static void crearActividadUsuarios(int user_id, String descripcion, String units,String  materiales, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "materiales a registrar " + materiales);

        Log.d("crearConexion", "cantidades a registrar " + materiales);
        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

// Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.crearActividadUsuario(user_id,descripcion,units,materiales);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();

                    if (recibido.isRespuesta()) {
                        Log.d("ActividadUsuario", recibido.getMensaje());
                        callback.onResultado(true);
                    } else {
                        Log.d("ActividadUsuario", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("ActividadUsuario", "no se logro enviar la solicitud" + t.getMessage());
                callback.onFailure(true);
            }
        });

    }
    public static void verActiviadadesUsuario(int user_id, RespuestaFinalCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<Respuesta> call = api.Ver_Actividades(user_id);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();

                    Log.d("Ver_Actividades", "Descripciones: " + (recibido.getDescripciones() != null ? recibido.getDescripciones() : "null"));
                    Log.d("Ver_Actividades", "Unidades: " + (recibido.getUnidades() != null ? recibido.getUnidades() : "null"));
                    Log.d("Ver_Actividades", "Materiales: " + (recibido.getMateriales() != null ? recibido.getMateriales() : "null"));
                    Log.d("Ver_Actividades", "Enviados: " + (recibido.getEnviados() != null ? recibido.getEnviados() : "null"));
                    Log.d("Ver_Actividades", "Mensaje: " + recibido.getMensaje());


                    callback.onResultado(recibido);
                } else {
                    Log.d("Ver_Actividades", "Respuesta no válida o cuerpo nulo");
                    callback.onFailure(true);
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("Ver_Actividades", "Error en solicitud: " + t.getMessage());
                callback.onFailure(true);
            }

            // Método auxiliar para evitar nulls en Log.d()
            private String safe(String s) {
                return s != null ? s : "null";
            }
        });
    }




    public static void eliminarActividadUsuario(int user_id,int activity_id, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Actividad id a eliminar " + activity_id + " usuario id: " + user_id);

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

// Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.eliminarActividadUsuario(user_id,activity_id);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("ELIMINAR_ACTIVIDAD", recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        Log.d("ELIMINAR_ACTIVIDAD", "se uso correctamente");
                        callback.onResultado(true);
                    } else {
                        Log.d("ELIMINAR_ACTIVIDAD", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("ELIMINAR_ACTIVIDAD", "no se logro enviar la solicitud" + t.getMessage());
                callback.onFailure(true);
            }
        });




    }



    public static void MostrarHistorial(RespuestaFinalCallback callback) {
        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

        // Llamar a la función para obtener el historial
        Log.d("MOSTRAR_HISTORIAL", "Realizando solicitud...");
        Call<List<Respuesta>> call = api.mostrarHistorial(); // Cambiar a List<Respuesta>

        call.enqueue(new Callback<List<Respuesta>>() {
            @Override
            public void onResponse(Call<List<Respuesta>> call, Response<List<Respuesta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obtener la lista de respuestas
                    List<Respuesta> recibido = response.body();
                    Log.d("MOSTRAR_HISTORIAL", "Respuesta exitosa con " + recibido.size() + " elementos.");

                    // Recorrer la lista de respuestas y procesarlas una por una
                    for (Respuesta r : recibido) {
                        if (r.isRespuesta()) {
                            // Si la respuesta es exitosa, logueamos el mensaje de éxito
                            Log.d("MOSTRAR_HISTORIAL", "Éxito: " + r.getNombre_usuario());
                            callback.onResultado(r); // Pasamos la respuesta exitosa al callback
                        } else {
                            // Si la respuesta es falsa, logueamos el mensaje de error
                            Log.e("MOSTRAR_HISTORIAL", "Error: " + r.getMensaje());
                            callback.onResultado(r); // Pasamos la respuesta con error al callback
                        }
                    }
                } else {
                    // Si la respuesta no fue exitosa
                    Log.e("MOSTRAR_HISTORIAL", "Respuesta no exitosa: " + response.message());
                    callback.onFailure(true); // Se envía el error al callback
                }
            }

            @Override
            public void onFailure(Call<List<Respuesta>> call, Throwable t) {
                Log.e("MOSTRAR_HISTORIAL", "Error al enviar la solicitud: " + t.getMessage());
                callback.onFailure(true); // Error en la conexión o la solicitud
            }
        });
    }

    public static void verActiviadadesProfesor( RespuestaFinalCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<Respuesta> call = api.Ver_Actividades_Profesor();

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("Ver_Actividades_Profesor", "nombre: " + (recibido.getNombre_usuario() != null ? recibido.getNombre_usuario() : "null"));
                    Log.d("Ver_Actividades_Profesor", "Descripciones: " + (recibido.getDescripciones() != null ? recibido.getDescripciones() : "null"));
                    Log.d("Ver_Actividades_Profesor", "Unidades: " + (recibido.getUnidades() != null ? recibido.getUnidades() : "null"));
                    Log.d("Ver_Actividades_Profesor", "Materiales: " + (recibido.getMateriales() != null ? recibido.getMateriales() : "null"));
                    Log.d("Ver_Actividades_Profesor", "Enviados: " + (recibido.getEnviados() != null ? recibido.getEnviados() : "null"));
                    Log.d("Ver_Actividades_Profesor", "Mensaje: " + recibido.getMensaje());


                    callback.onResultado(recibido);
                } else {
                    Log.d("Ver_Actividades_Profesor", "Respuesta no válida o cuerpo nulo");
                    callback.onFailure(true);
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("Ver_Actividades", "Error en solicitud: " + t.getMessage());
                callback.onFailure(true);
            }

            // Método auxiliar para evitar nulls en Log.d()
            private String safe(String s) {
                return s != null ? s : "null";
            }
        });
    }


    public static void RestarMaterialAdmin(int user_id,int material_id, String storage_type, int units, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Material a restar unidades " + material_id + " usuario id: " + user_id + "unidades restadas: " + units + "en: " + storage_type);

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

        // Llamar a la función de la api para restar undidades al material
        Call<Respuesta> call = api.Restar_material_admin(user_id, material_id, storage_type, units);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("MATERIALOG", recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        Log.d("MATERIALOG", "se uso correctamente");
                        callback.onResultado(true);
                    } else {
                        Log.d("MATERIALOG", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("RestarMaterial", "no se logro enviar la solicitud" + t.getMessage());
                callback.onFailure(true);
            }
        });
    }


    public static void SumarMaterialAdmin(int user_id,int material_id, String storage_type, int units, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Material a sumar unidades " + material_id + " usuario id: " + user_id + "unidades sumadas: " + units + "en: " + storage_type);

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

        // Llamar a la función de la api para sumar unidades al material
        Call<Respuesta> call = api.Sumar_material_admin(user_id, material_id, storage_type, units);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("Restar material: ", recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        Log.d("Restar material:", "se uso correctamente");
                        callback.onResultado(true);
                    } else {
                        Log.d("Restar material:", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("restarMaterial", "Fallo en la solicitud: " + t.getMessage());
                callback.onFailure(true);
            }
        });
    }

    public static void CrearMaterial(Material materia, RespuestaCallback callback)
    {
        // Paso 1: Crear instancia Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d("CrearMaterial", "Enviando material: " + materia.getNombre());

        // Paso 2: Crear instancia de ApiService
        ApiService api = retrofit.create(ApiService.class);

        // Paso 3: Llamada a la API
        Call<Respuesta> call = api.Crear_material(materia);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("CrearMaterial", "Respuesta: " + recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        callback.onResultado(true);
                    } else {
                        callback.onResultado(false);
                    }
                } else {
                    callback.onResultado(false);
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("CrearMaterial", "Fallo en la solicitud: " + t.getMessage());
                callback.onFailure(true);
            }
        });
    }

    public static void crearPeticiones(int user_id, String nombre_materiales, String units, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

         // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

// Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.Crear_Peticiones(user_id,units,nombre_materiales);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();

                    if (recibido.isRespuesta()) {
                        Log.d("Crear_Peticiones", recibido.getMensaje());
                        callback.onResultado(true);
                    } else {
                        Log.d("Crear_Peticiones", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("Crear_Peticiones", "no se logro enviar la solicitud" + t.getMessage());
                callback.onFailure(true);
            }
        });

    }


    public static void verPeticiones( RespuestaFinalCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        Call<Respuesta> call = api.VerPeticiones();

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("VerPeticiones", "nombre usuario: " + (recibido.getNombre_usuario() != null ? recibido.getNombre_usuario() : "null"));
                    Log.d("VerPeticiones", "materiales : " + (recibido.getMateriales() != null ? recibido.getMateriales() : "null"));
                    Log.d("VerPeticiones", "Unidades: " + (recibido.getUnidades() != null ? recibido.getUnidades() : "null"));
                    Log.d("VerPeticiones", "fechas " + (recibido.getFecha_modificacion() != null ? recibido.getFecha_modificacion() : "null"));


                    callback.onResultado(recibido);
                } else {
                    Log.d("VerPeticiones", "Respuesta no válida o cuerpo nulo");
                    callback.onFailure(true);
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("VerPeticiones", "Error en solicitud: " + t.getMessage());
                callback.onFailure(true);
            }

            // Método auxiliar para evitar nulls en Log.d()
            private String safe(String s) {
                return s != null ? s : "null";
            }
        });
    }


    public static void eliminarPeticiones(int peticion_id, RespuestaCallback callback){

        // Paso 1: Crear la instancia de Retrofit con configuración básica
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le decimos a Retrofit cuál es la URL base para todas las peticiones
                .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON en objetos Java automáticamente
                .build(); // Creamos la instancia final de Retrofit

        Log.d("crearConexion", "Peticion id a eliminar " + peticion_id );

        // Paso 2: Creamos un objeto que implementa automáticamente la interfaz ApiService
        ApiService api = retrofit.create(ApiService.class);

        // Llamar a la función para eliminar el usuario de la base de datos
        Call<Respuesta> call = api.eliminarPeticion(peticion_id);
        Log.d("ELIMINAR_PETICION", "antes de recibir la info");

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {


                if (response.isSuccessful() && response.body() != null) {
                    Respuesta recibido = response.body();
                    Log.d("ELIMINAR_PETICION", recibido.getMensaje());

                    if (recibido.isRespuesta()) {
                        Log.d("ELIMINAR_PETICION", "se uso correctamente");
                        callback.onResultado(true);
                    } else {
                        Log.d("ELIMINAR_PETICION", recibido.getMensaje());

                        callback.onResultado(false);
                    }
                }
                else {
                    callback.onResultado(false);
                }

            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Log.e("ELIMINAR_PETICION", "no se logro enviar la solicitud" + t.getMessage());
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

      /*
      LO QUE ESTA EN < > ES LA FORMA A LA QUE SERA CONVERTIDA LA RESPUESTA ENVIADA DE LA API

      LO QUE ESTA ENTRE PARENTESIS SERA LOS DATOS A ENVIAR A LA API

       */
          @POST("general/Login.php")
        Call<Usuario> verificarUsuario(@Body Usuario usuario);

        @GET("usuario/ObtenerMateriales.php")
        Call<List<Material>> obtenerMaterial();  // Esta vez esperamos un List<Material> directamente

        @POST("admin/AccionesSobreUsuario.php")
        Call<Respuesta>añadirUsuario(@Body Usuario nuevoUsuario);


        // Usamos DELETE y pasamos el 'email' como parte de la url
        @DELETE("admin/AccionesSobreUsuario.php")
        Call<Respuesta> eliminarUsuario(@Query("email") String emailUsuario);

        @GET("admin/AccionesSobreUsuario.php")

        Call<List<Usuario>> obtenerUsuarios();


        @PUT("admin/editarUsuario.php")
        Call<Respuesta> editarUsuarios(@Body Usuario nuevoUsuario,@Query("nombre_antiguo") String nombreUsuario);


        @PUT("admin/AccionesSobreUsuario.php")
        Call<Respuesta> actualizarContra(@Body Usuario usuario);

        @GET("profesor/UsarMateriales.php")
        Call<Respuesta>usarMaterial_Profesor(@Query("user_id")int user_id, @Query("material_id") int material_id,@Query("units")int unidades);

        @GET("usuario/Crear_Actividades.php")
        Call<Respuesta>crearActividadUsuario(@Query("user_id")int user_id, @Query("description") String descripcion,@Query("units")String unidades, @Query("materiales") String materiales );

        @GET("usuario/Ver_Actividades.php")
        Call<Respuesta>Ver_Actividades(@Query("user_id")int user_id );

        @GET("usuario/Quitar_Materiales.php")
        Call<Respuesta>quitar_Material_Usuario(@Query("user_id")int user_id, @Query("material_id") int material_id,@Query("units")int unidades);

        @DELETE("usuario/EliminarActividades.php")
        Call<Respuesta>eliminarActividadUsuario(@Query("user_id")int user_id, @Query("activity_id") int activity_id);

        @GET("profesor/Ver_Historial.php")
        Call<List<Respuesta>>mostrarHistorial();


        @GET("profesor/Ver_actividades_Profesor.php")
        Call<Respuesta>Ver_Actividades_Profesor();


        @GET("admin/Restar_material_admin.php")
        Call<Respuesta>Restar_material_admin(@Query("user_id")int user_id, @Query("material_id")int material_id, @Query("storage_type") String storage_type, @Query("units")int units);

        @GET("admin/Sumar_material_admin.php")
        Call<Respuesta>Sumar_material_admin(@Query("user_id")int user_id, @Query("material_id")int material_id, @Query("storage_type") String storage_type, @Query("units")int units);

        @POST("admin/CrearMaterial.php")
        Call<Respuesta>Crear_material(@Body Material material);

        @GET("profesor/Crear_Peticiones.php")
        Call<Respuesta>Crear_Peticiones(@Query("user_id")int user_id, @Query("units")String unidades, @Query("materiales") String materiales );

        @GET("profesor/Ver_Peticiones.php")
        Call<Respuesta>VerPeticiones();

        @DELETE("profesor/Eliminar_Peticiones.php")
        Call<Respuesta>eliminarPeticion( @Query("peticion_id") int activity_id);

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