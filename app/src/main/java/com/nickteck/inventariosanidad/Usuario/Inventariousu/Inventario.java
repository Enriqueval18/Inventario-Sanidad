package com.nickteck.inventariosanidad.Usuario.Inventariousu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventario extends Fragment {
    private RecyclerView recyclerInventario;
    private LinearLayout layoutError;
    private final List<Material> listamateriales = new ArrayList<>();
    int idUsuario;

    /**
     * Define todo lo del xml , el cuadro de busqueda, los recycleview de las listas, el mensaje de error, el boton de error
     * El boton de reintentar, tambien se hace el filtro de la busqueda y para obtener los materiales
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        SearchView busqueda = view.findViewById(R.id.searchView);
        recyclerInventario = view.findViewById(R.id.recyclerInventario);
        recyclerInventario.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutError = view.findViewById(R.id.layoutError);
        Button btnReintentar = view.findViewById(R.id.btnReintentar);

        btnReintentar.setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            obtenerDatosInventario();
        });


        if (busqueda != null) {
            busqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filtrarTabla(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filtrarTabla(newText);
                    return false;
                }
            });
        }
        ImageView btnDialogo = view.findViewById(R.id.btnRestarMateriales);

        btnDialogo.setOnClickListener(v -> mostrarDialogoRestarCantidad());




        Bundle args = getArguments();
        if (args != null) {
            idUsuario = args.getInt("idUsuario", -1);  // -1 si no está
            Log.d("InventarioFragment", "ID recibido: " + idUsuario);
        }
        Log.e("usuarioinicio", ""+idUsuario);
        obtenerDatosInventario();
        return view;
    }

    //--------------------------------------------------------------------------------------------------------------//

    /**
     * Metodo que obtiene los datos del inventario, primero lo que hace es limpiar la lista, llama desde la api, y lo añade, da un pequeño delay
     * Si es correcto lo muestra, si es incorrecto sale un boton de reintentar hasta que se muestre la tabla
     * Prueba = desconectar el intenet, y volver a conectar
     */
    private void obtenerDatosInventario() {
        layoutError.setVisibility(View.GONE);
        listamateriales.clear();

        Utilidades.obtenerMateriales(new MaterialCallback() {
            @Override
            public void onMaterialObtenido(int material_id,String nombre, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion,String tipo) {
                if (tipo.equals("use")) {
                    listamateriales.add(new Material(nombre, material_id, descripcion, unidades, unidades_min, almacen, armario, estante, "", tipo));
                }
            }

            @Override
            public void onFailure(boolean error) {
                requireActivity().runOnUiThread(() -> layoutError.setVisibility(View.VISIBLE));
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> actualizarTabla(listamateriales), 800);
    }

    //--------------------------------------------------------------------------------------------------------------//

    /**
     * Metodo que actualiza la tabla y pasa los datos de los materiales a MostrarInventario para que sean visto por el usuario
     * @param lista hace referencia a la lista de materiales que se le va a pasar
     */
    private void actualizarTabla(List<Material> lista) {
        MaterialAdapter adapter = new MaterialAdapter(lista, getContext(), item -> {
            MostrarInventario dialog = new MostrarInventario();
            Bundle args = new Bundle();
            args.putString("nombre", item.getNombre());
            args.putInt("unidades", item.getUnidades());
            args.putString("almacen", item.getAlmacen());
            args.putString("armario", item.getArmario());
            args.putString("estante", item.getEstante());
            args.putInt("unidadesm", item.getUnidades_min());
            args.putString("descripcion", item.getDescripcion());
            dialog.setArguments(args);
            dialog.show(getParentFragmentManager(), "MostrarInventario");
        });
        recyclerInventario.setAdapter(adapter);
    }

    //--------------------------------------------------------------------------------------------------------------//

    /**
     * Metodo que filtra la busqueda de la tabla por el nombre, ademas hace una actualizacion a la tabla
     * @param nombre hace referencia al nombre del material
     */
    private void filtrarTabla(String nombre) {
        nombre = nombre.toLowerCase();
        List<Material> listaFiltrada = new ArrayList<>();
        for (Material item : listamateriales) {
            if (item.getNombre().toLowerCase().contains(nombre)) {
                listaFiltrada.add(item);
            }
        }
        actualizarTabla(listaFiltrada);
    }

    //--------------------------------------------------------------------------------------------------------------//

    /**
     * Esto es para actualizar la tabla al hacer cambios
     */
    public abstract class TextWatcherAdapter implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }

    //--------------------------------------------------------------------------------------------------------------//


    /**
     * Metodo que dependiendo que si es profesor o alumno, permite cambiar en el inventario o no
     * Cogemos el tipo de usuario del login , atravez de preferencias y todo se modifica en el mismo inventario
     */
    private void mostrarDialogoRestarCantidad() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_restar_material, null);
        Button btnSeleccionar = dialogView.findViewById(R.id.btnSeleccionarMateriales);
        LinearLayout contenedorSeleccionados = dialogView.findViewById(R.id.contenedorSeleccionados);
        EditText etRestar = dialogView.findViewById(R.id.etRestar1);

        Map<String, View> filaViews = new HashMap<>();
        final Material[] materialActivo = {null};

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String tipo = prefs.getString("tipo_usuario", "");
        int idUsuario = prefs.getInt("user_id", -1);

        if (idUsuario == -1 || tipo.isEmpty()) {
            Toast.makeText(getContext(),
                    "Error al obtener usuario o tipo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listamateriales == null || listamateriales.isEmpty()) {
            Toast.makeText(getContext(),
                    "No hay materiales cargados", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSeleccionar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Elige un material");
            String[] nombres = new String[listamateriales.size()];
            for (int i = 0; i < listamateriales.size(); i++) {
                nombres[i] = listamateriales.get(i).getNombre();
            }
            builder.setItems(nombres, (dialog, which) -> {
                Material mat = listamateriales.get(which);
                if (filaViews.containsKey(mat.getNombre())) return;

                // Creamos la fila usando contenedorSeleccionados como padre
                View fila = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2,
                                contenedorSeleccionados, false);
                TextView tvNombre = fila.findViewById(android.R.id.text1);
                TextView tvCantidad = fila.findViewById(android.R.id.text2);

                tvNombre.setText(mat.getNombre());
                tvCantidad.setText("Cantidad actual: " + mat.getUnidades());
                filaViews.put(mat.getNombre(), fila);

                fila.setOnClickListener(f -> {
                    // Resetea fondo de todas
                    for (View otra : filaViews.values()) {
                        otra.setBackgroundColor(Color.TRANSPARENT);
                    }
                    fila.setBackgroundColor(ContextCompat.getColor(
                            requireContext(), R.color.primary_400));
                    materialActivo[0] = mat;

                    // Al seleccionar, si ya hay número en etRestar, actualizar
                    String texto = etRestar.getText().toString().trim();
                    try {
                        int resta = Integer.parseInt(texto);
                        int nuevaCant = Math.max(0, mat.getUnidades() - resta);
                        tvCantidad.setText("Cantidad nueva: " + nuevaCant);
                    } catch (NumberFormatException ex) {
                        tvCantidad.setText("Cantidad actual: " + mat.getUnidades());
                    }
                });

                contenedorSeleccionados.addView(fila);
            });
            builder.show();

            // Watcher que resta o restablece original si borras todo
            etRestar.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (materialActivo[0] == null) return;
                    View fila = filaViews.get(materialActivo[0].getNombre());
                    if (fila == null) return;
                    TextView tvCantidad = fila.findViewById(android.R.id.text2);

                    String txt = s.toString().trim();
                    try {
                        int resta = Integer.parseInt(txt);
                        int nuevaCant = Math.max(0, materialActivo[0].getUnidades() - resta);
                        tvCantidad.setText("Cantidad nueva: " + nuevaCant);
                    } catch (NumberFormatException e) {
                        // si txt es vacío o no numérico, volvemos al original
                        tvCantidad.setText("Cantidad actual: " + materialActivo[0].getUnidades());
                    }
                }
            });
        });

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Aceptar", (d, w) -> {
                    if (materialActivo[0] == null) {
                        Toast.makeText(getContext(),
                                "Debes seleccionar un material", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int resta;
                    try {
                        resta = Integer.parseInt(etRestar.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(),
                                "Introduce un número válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (resta < 0) {
                        Toast.makeText(getContext(),
                                "Cantidad inválida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Aplicamos cambio real al objeto
                    Material mat = materialActivo[0];
                    int nuevaCantidad = Math.max(0, mat.getUnidades() - resta);
                    mat.setUnidades(nuevaCantidad);

                    // Llamada según tipo
                    if (tipo.equalsIgnoreCase("student")) {
                        Utilidades.quitarMaterialesUsuarios(
                                idUsuario, mat.getId(), resta,
                                new RespuestaCallback() {
                                    @Override
                                    public void onResultado(boolean correcto) {
                                        if (correcto) {
                                            Toast.makeText(getContext(),
                                                    "Actualización aplicada", Toast.LENGTH_SHORT).show();
                                            actualizarTabla(listamateriales);
                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Stock insuficiente o material no encontrado",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(boolean error) {
                                        Toast.makeText(getContext(),
                                                "Fallo de red", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else { // teacher
                        Utilidades.usarMaterialesProfesor(
                                idUsuario, mat.getId(), resta,
                                new RespuestaCallback() {
                                    @Override
                                    public void onResultado(boolean correcto) {
                                        if (correcto) {
                                            Toast.makeText(getContext(),
                                                    "Actualización aplicada", Toast.LENGTH_SHORT).show();
                                            actualizarTabla(listamateriales);
                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Stock insuficiente o material no encontrado",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(boolean error) {
                                        Toast.makeText(getContext(),
                                                "Fallo de red", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }




}