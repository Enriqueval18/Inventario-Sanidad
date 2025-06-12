package com.nickteck.inventariosanidad.Administrador.InventarioAd;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
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
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dobleinventario extends Fragment {

    private LinearLayout tablaMateriales;
    private SearchView searchView;
    private List<Material> listaMateriales = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doblein, container, false);
        tablaMateriales = view.findViewById(R.id.tableRowMaterial001);
        searchView = view.findViewById(R.id.searchView2);
        ImageView btnAgregarMaterial = view.findViewById(R.id.btnAgregarMaterial);
        btnAgregarMaterial.setOnClickListener(v -> mostrarDialogoInventarioDestino());

        configurarBusqueda();
        obtenerDatosInventario();

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
        Utilidades.obtenerMateriales(new MaterialCallback() {
            @Override
            public void onMaterialObtenido(int material_id,String nombre, int unidades, String almacen, String armario, String estante, int unidadesMin, String descripcion) {
                Material material = new Material(nombre, descripcion, unidades, unidadesMin, almacen, armario, descripcion);
                listaMateriales.add(material);
                refreshTabla(listaMateriales);
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
        TextView tvArmario = crearCelda(item.getArmario());

        fila.addView(tvNombre);
        fila.addView(tvUnidades);
        fila.addView(tvAlmacen);
        fila.addView(tvArmario);

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
                android.R.layout.simple_spinner_item, new String[]{"CAE", "SAO"});
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
                        Material nuevo = new Material(nombre, descripcion, unidades, unidadesMin, almacen, armario, descripcion);
                        listaMateriales.add(nuevo);
                        refreshTabla(listaMateriales);
                        Toast.makeText(getContext(), "Material agregado", Toast.LENGTH_SHORT).show();
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
                        mostrarOpcionesModificacion("Principal");
                    } else if (which == 1) {
                        mostrarOpcionesModificacion("Secundario");
                    }
                })
                .show();
    }
    private void mostrarOpcionesModificacion(String tipoInventario) {
        String[] opciones = {"Añadir Material", "Añadir Cantidad", "Trasladar Material"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Acción sobre Inventario " + tipoInventario)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            mostrarDialogoAgregarMaterial(); // Ya implementado antes
                            break;
                        case 1:
                            mostrarDialogoSumarRestarCantidad();
                            break;
                        case 2:
                            mostrarDialogoTrasladarMaterial(tipoInventario); // ya tienes "Principal" o "Secundario"
                            break;
                    }
                })
                .show();
    }

    private void mostrarDialogoSumarRestarCantidad() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sumar_restar_material, null);
        Button btnSeleccionar = dialogView.findViewById(R.id.btnSeleccionarMateriales);
        LinearLayout contenedorSeleccionados = dialogView.findViewById(R.id.contenedorSeleccionados);
        EditText etSumar = dialogView.findViewById(R.id.etSumar);
        EditText etRestar = dialogView.findViewById(R.id.etRestar);

        List<Material> seleccionados = new ArrayList<>();
        Map<String, View> filaViews = new HashMap<>();
        final Material[] materialActivo = {null}; // Solo uno activo

        btnSeleccionar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Elige un material");

            String[] nombres = new String[listaMateriales.size()];
            for (int i = 0; i < listaMateriales.size(); i++) {
                nombres[i] = listaMateriales.get(i).getNombre();
            }

            builder.setItems(nombres, (dialog, which) -> {
                Material mat = listaMateriales.get(which);
                if (filaViews.containsKey(mat.getNombre())) return;

                seleccionados.add(mat);

                View fila = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, null);
                TextView tvNombre = fila.findViewById(android.R.id.text1);
                TextView tvCantidad = fila.findViewById(android.R.id.text2);

                tvNombre.setText(mat.getNombre());
                tvCantidad.setText("Cantidad actual: " + mat.getUnidades());
                filaViews.put(mat.getNombre(), fila);

                fila.setOnClickListener(f -> {
                    // Marcar este como seleccionado visualmente
                    for (View otraFila : filaViews.values()) {
                        otraFila.setBackgroundColor(Color.TRANSPARENT);
                    }
                    fila.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_100));

                    materialActivo[0] = mat;
                });

                contenedorSeleccionados.addView(fila);
            });

            builder.show();
        });

        // Cambio en tiempo real
        TextWatcher watcher = new TextWatcherAdapter() {
            public void afterTextChanged(Editable s) {
                if (materialActivo[0] == null) return;

                int suma = etSumar.getText().toString().isEmpty() ? 0 : Integer.parseInt(etSumar.getText().toString());
                int resta = etRestar.getText().toString().isEmpty() ? 0 : Integer.parseInt(etRestar.getText().toString());

                int nuevaCantidad = materialActivo[0].getUnidades() + suma - resta;
                nuevaCantidad = Math.max(0, nuevaCantidad);

                View fila = filaViews.get(materialActivo[0].getNombre());
                if (fila != null) {
                    TextView tvCantidad = fila.findViewById(android.R.id.text2);
                    tvCantidad.setText("Cantidad nueva: " + nuevaCantidad);
                }
            }
        };

        etSumar.addTextChangedListener(watcher);
        etRestar.addTextChangedListener(watcher);

        new AlertDialog.Builder(getContext())
                .setTitle("Modificar cantidades")
                .setView(dialogView)
                .setPositiveButton("Aceptar", (d, w) -> {
                    if (materialActivo[0] != null) {
                        int suma = etSumar.getText().toString().isEmpty() ? 0 : Integer.parseInt(etSumar.getText().toString());
                        int resta = etRestar.getText().toString().isEmpty() ? 0 : Integer.parseInt(etRestar.getText().toString());
                        int nuevaCantidad = materialActivo[0].getUnidades() + suma - resta;
                        materialActivo[0].setUnidades(Math.max(0, nuevaCantidad));
                    }
                    refreshTabla(listaMateriales);
                    Toast.makeText(getContext(), "Actualización aplicada", Toast.LENGTH_SHORT).show();
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
