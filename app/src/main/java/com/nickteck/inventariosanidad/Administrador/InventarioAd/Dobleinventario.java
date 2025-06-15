package com.nickteck.inventariosanidad.Administrador.InventarioAd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dobleinventario extends Fragment {

    private LinearLayout tablaMateriales;
    private SearchView searchView;
    private List<Material> listaMateriales = new ArrayList<>();
    int userId;
    String storagetype;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doblein, container, false);
        tablaMateriales = view.findViewById(R.id.tableRowMaterial001);
        searchView = view.findViewById(R.id.searchView2);
        ImageView btnAgregarMaterial = view.findViewById(R.id.btnAgregarMaterial);
        btnAgregarMaterial.setOnClickListener(v -> mostrarOpcionesModificacion());

        configurarBusqueda();
        obtenerDatosInventario();
        SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(requireContext(), "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("USER_ID", "ID obtenido: " + userId);
        }
        return view;
    }
    private void configurarBusqueda() {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }
    private void obtenerDatosInventario() {
        listaMateriales.clear();
        Handler handler = new Handler();
        final Runnable refrescar = () -> refreshTabla(listaMateriales);

        Utilidades.obtenerMateriales(new MaterialCallback() {
            @Override
            public void onMaterialObtenido(int material_id, String nombre, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion,String tipo) {
                listaMateriales.add(new Material(nombre, material_id, descripcion,  unidades,  unidades_min,    almacen,  armario,  estante,  "",tipo));

                // Reprograma el refresco con un pequeño retraso para evitar múltiples llamadas
                handler.removeCallbacks(refrescar);
                handler.postDelayed(refrescar, 100); // 100ms después del último
            }

            @Override
            public void onFailure(boolean error) {
                Log.e("Inventario", "Error al obtener inventario");
                Toast.makeText(getContext(), "Error al obtener inventario", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void refreshTabla(List<Material> materiales) {
        int childCount = tablaMateriales.getChildCount();
        if (childCount > 1) {
            tablaMateriales.removeViews(1, childCount - 1);
        }

        for (Material item : materiales) {
            tablaMateriales.addView(crearFila(item));
        }
    }
    private TableRow crearFila(Material item) {
        TableRow fila = new TableRow(getContext());
        fila.setPadding(4, 4, 4, 4);

        TextView tvNombre = crearCelda(item.getNombre());
        TextView tvUnidades = crearCelda(String.valueOf(item.getUnidades()));
        tvUnidades.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView tvAlmacen = crearCelda(item.getAlmacen());
        TextView tvTipo = crearCelda(item.getTipo());

        fila.addView(tvNombre);
        fila.addView(tvUnidades);
        fila.addView(tvAlmacen);
        fila.addView(tvTipo);

        fila.setOnClickListener(v -> mostrarDialogoDetalle(item));

        return fila;
    }
    private TextView crearCelda(String texto) {
        TextView celda = new TextView(getContext());
        celda.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        celda.setText(texto);
        celda.setPadding(8, 8, 8, 8);
        return celda;
    }
    private void mostrarDialogoDetalle(Material item) {
        MostarDobleinventario dialog = new MostarDobleinventario();
        Bundle args = new Bundle();
        args.putString("nombre", item.getNombre());
        args.putInt("unidades", item.getUnidades());
        args.putString("almacen", item.getAlmacen());
        args.putString("armario", item.getArmario());
        args.putString("estante", item.getEstante());
        args.putInt("unidadesm", item.getUnidades_min());
        args.putString("descripcion", item.getDescripcion());
        args.putString("tipo",item.getTipo());
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "MostarDobleinventario");
    }
    private void filtrarTabla(String query) {
        String lowerQuery = query.toLowerCase();
        List<Material> filtrada = new ArrayList<>();
        for (Material item : listaMateriales) {
            if (item.getNombre().toLowerCase().contains(lowerQuery)) {
                filtrada.add(item);
            }
        }
        refreshTabla(filtrada);
    }

    private void mostrarDialogoAgregarMaterial() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nuevo_material, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etUnidades = dialogView.findViewById(R.id.etUnidades);
        Spinner spinnerAlmacen = dialogView.findViewById(R.id.spinnerAlmacen);
        EditText etArmario = dialogView.findViewById(R.id.etArmario);
        EditText etEstante = dialogView.findViewById(R.id.etEstante);
        EditText etUnidadesMin = dialogView.findViewById(R.id.etUnidadesMin);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"CAE", "odontología"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlmacen.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nuevo Material")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String unidadesStr = etUnidades.getText().toString().trim();
                    String almacen = spinnerAlmacen.getSelectedItem().toString();
                    String armario = etArmario.getText().toString().trim();
                    String estante = etEstante.getText().toString().trim();
                    String unidadesMinStr = etUnidadesMin.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();

                    if (!nombre.isEmpty() && !unidadesStr.isEmpty() && !unidadesMinStr.isEmpty()) {
                        int unidades = Integer.parseInt(unidadesStr);
                        int unidadesMin = Integer.parseInt(unidadesMinStr);

                        Material nuevo = new Material(nombre, descripcion, unidades, unidadesMin, almacen, armario, estante);

                        Utilidades.CrearMaterial(nuevo, new RespuestaCallback() {
                            @Override
                            public void onResultado(boolean correcto) {
                                if (correcto) {
                                    requireActivity().runOnUiThread(() -> {
                                        listaMateriales.add(nuevo);
                                        refreshTabla(listaMateriales);
                                        Toast.makeText(getContext(), "Material creado correctamente", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(getContext(), "Error al crear el material", Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onFailure(boolean error) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(getContext(), "Fallo de red al crear material", Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Completa los campos requeridos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoInventarioDestino() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("¿Qué inventario deseas modificar?")
                .setItems(new String[]{"Modificar Inventario Principal", "Modificar Inventario Secundario"}, (dialog, which) -> {
                    if (which == 0) {
                        storagetype = "use";
                        mostrarDialogoSumarRestarCantidad();
                    } else if (which == 1) {
                        storagetype = "reserve";
                        mostrarDialogoSumarRestarCantidad();
                    }
                })
                .show();
    }
    private void mostrarOpcionesModificacion() {
        String[] opciones = {"Añadir Material", "Modificar Cantidad"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("¿Que deseas realizar?")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            mostrarDialogoAgregarMaterial(); // Ya implementado antes
                            break;
                        case 1:
                            mostrarDialogoInventarioDestino();
                            break;
                    }
                })
                .show();
    }

    private void mostrarDialogoSumarRestarCantidad() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_sumar_restar_material, null);

        Button btnSeleccionar = dialogView.findViewById(R.id.btnSeleccionarMateriales);
        LinearLayout contenedorSeleccionados = dialogView.findViewById(R.id.contenedorSeleccionados);
        EditText etOperacion = dialogView.findViewById(R.id.etOperacion);
        ImageView imageViewSumar = dialogView.findViewById(R.id.imageViewSumar);
        ImageView imageViewRestar = dialogView.findViewById(R.id.imageViewRestar);

        List<Material> seleccionados = new ArrayList<>();
        Map<String, View> filaViews = new HashMap<>();
        final Material[] materialActivo = {null};

        btnSeleccionar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Elige un material");

            List<String> nombresList = new ArrayList<>();
            List<Material> materialesFiltrados = new ArrayList<>();

            for (Material material : listaMateriales) {
                if (material.getTipo().equalsIgnoreCase(storagetype)) {
                    Log.d("tipo", "mostrarDialogoSumarRestarCantidad: " + material.getTipo());
                    nombresList.add(material.getNombre());
                    materialesFiltrados.add(material);
                }
            }

            String[] nombres = nombresList.toArray(new String[0]);

            builder.setItems(nombres, (dialog, which) -> {
                Material mat = materialesFiltrados.get(which);
                if (filaViews.containsKey(mat.getNombre())) return;

                seleccionados.add(mat);

                View fila = LayoutInflater.from(requireContext()).inflate(android.R.layout.simple_list_item_2, null);
                TextView tvNombre = fila.findViewById(android.R.id.text1);
                TextView tvCantidad = fila.findViewById(android.R.id.text2);

                tvNombre.setText(mat.getNombre());
                tvCantidad.setText("Cantidad actual: " + mat.getUnidades());
                filaViews.put(mat.getNombre(), fila);

                fila.setOnClickListener(f -> {
                    for (View otraFila : filaViews.values()) {
                        otraFila.setBackgroundColor(Color.TRANSPARENT);
                    }
                    fila.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_100));
                    materialActivo[0] = mat;

                    // Mostrar la cantidad actual
                    tvCantidad.setText("Cantidad actual: " + mat.getUnidades());
                });

                contenedorSeleccionados.addView(fila);
            });

            builder.show();
        });

        imageViewSumar.setOnClickListener(v -> {
            if (materialActivo[0] == null) {
                Toast.makeText(requireContext(), "Selecciona un material", Toast.LENGTH_SHORT).show();
                return;
            }

            int unidades;
            try {
                unidades = Integer.parseInt(etOperacion.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Introduce una cantidad válida", Toast.LENGTH_SHORT).show();
                return;
            }

            Utilidades.SumarMaterialAdmin(userId, materialActivo[0].getId(), storagetype, unidades, new RespuestaCallback() {
                @Override
                public void onResultado(boolean correcto) {
                    if (correcto) {
                        materialActivo[0].setUnidades(materialActivo[0].getUnidades() + unidades);
                        View fila = filaViews.get(materialActivo[0].getNombre());
                        if (fila != null) {
                            TextView tvCantidad = fila.findViewById(android.R.id.text2);
                            tvCantidad.setText("Cantidad nueva: " + materialActivo[0].getUnidades());
                            Toast.makeText(requireContext(), "Sumado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "No se pudo sumar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(boolean error) {
                    Toast.makeText(requireContext(), "Fallo de red al sumar", Toast.LENGTH_SHORT).show();
                }
            });
        });

        imageViewRestar.setOnClickListener(v -> {
            if (materialActivo[0] == null) {
                Toast.makeText(requireContext(), "Selecciona un material", Toast.LENGTH_SHORT).show();
                return;
            }

            int unidades;
            try {
                unidades = Integer.parseInt(etOperacion.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Introduce una cantidad válida", Toast.LENGTH_SHORT).show();
                return;
            }

            Utilidades.RestarMaterialAdmin(userId, materialActivo[0].getId(), storagetype, unidades, new RespuestaCallback() {
                @Override
                public void onResultado(boolean correcto) {
                    if (correcto) {
                        int nuevaCantidad = Math.max(0, materialActivo[0].getUnidades() - unidades);
                        materialActivo[0].setUnidades(nuevaCantidad);
                        View fila = filaViews.get(materialActivo[0].getNombre());
                        if (fila != null) {
                            TextView tvCantidad = fila.findViewById(android.R.id.text2);
                            tvCantidad.setText("Cantidad nueva: " + nuevaCantidad);
                            Toast.makeText(requireContext(), "Restado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "No se pudo restar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(boolean error) {
                    Toast.makeText(requireContext(), "Fallo de red al restar", Toast.LENGTH_SHORT).show();
                }
            });
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Modificar cantidades")
                .setView(dialogView)
                .setPositiveButton("Aceptar", (d, w) -> {
                    Toast.makeText(requireContext(), "Cambios aplicados", Toast.LENGTH_SHORT).show();
                    refreshTabla(listaMateriales);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    public abstract class TextWatcherAdapter implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }

    private void mostrarDialogoTrasladarMaterial(String inventarioActual) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_trasladar_material, null);
        TextView tvInventario = dialogView.findViewById(R.id.tvInventarioActual);
        tvInventario.setText("Inventario Actual: " + inventarioActual);

        Button btnSeleccionar = dialogView.findViewById(R.id.btnSeleccionarMateriales);
        Button btnMoverDerecha = dialogView.findViewById(R.id.btnMoverDerecha);
        ListView listaOrigen = dialogView.findViewById(R.id.listaOrigen);
        ListView listaDestino = dialogView.findViewById(R.id.listaDestino);

        List<String> materialesOrigen = new ArrayList<>();
        List<String> materialesDestino = new ArrayList<>();

        ArrayAdapter<String> adapterOrigen = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, materialesOrigen);
        ArrayAdapter<String> adapterDestino = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, materialesDestino);

        listaOrigen.setAdapter(adapterOrigen);
        listaDestino.setAdapter(adapterDestino);

        btnSeleccionar.setOnClickListener(v -> {
            // Diálogo multiselección
            boolean[] seleccionados = new boolean[listaMateriales.size()];
            String[] nombres = new String[listaMateriales.size()];
            for (int i = 0; i < listaMateriales.size(); i++) {
                nombres[i] = listaMateriales.get(i).getNombre();
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("Selecciona materiales")
                    .setMultiChoiceItems(nombres, seleccionados, (dialog, which, isChecked) -> {
                        // Nada aquí, manejamos en positiveButton
                    })
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        materialesOrigen.clear();
                        for (int i = 0; i < seleccionados.length; i++) {
                            if (seleccionados[i]) {
                                materialesOrigen.add(nombres[i]);
                            }
                        }
                        adapterOrigen.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnMoverDerecha.setOnClickListener(v -> {
            SparseBooleanArray checked = listaOrigen.getCheckedItemPositions();
            List<String> mover = new ArrayList<>();

            for (int i = 0; i < materialesOrigen.size(); i++) {
                if (checked.get(i)) {
                    String nombre = materialesOrigen.get(i);
                    if (!materialesDestino.contains(nombre)) {
                        materialesDestino.add(nombre);
                        mover.add(nombre);
                    }
                }
            }

            // Eliminar de origen
            materialesOrigen.removeAll(mover);
            adapterOrigen.notifyDataSetChanged();
            adapterDestino.notifyDataSetChanged();
            listaOrigen.clearChoices();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Trasladar Material")
                .setView(dialogView)
                .setPositiveButton("Finalizar", (dialog, which) -> {
                    Toast.makeText(getContext(), "Materiales trasladados: " + materialesDestino.size(), Toast.LENGTH_SHORT).show();
                    // Aquí podrías actualizar inventarios reales
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }




}
