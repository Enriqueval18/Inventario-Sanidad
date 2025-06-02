package com.nickteck.inventariosanidad.Usuario;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.List;

public class Inventario extends Fragment {

    private TableLayout tablaInventario;
    private SearchView searchView;
    private List<MaterialItem> materialesList = new ArrayList<>();

    private class MaterialItem {
        String nombre;
        int unidades;
        String almacen;
        String armario;
        String estante;
        int unidadesMin;
        String descripcion;

        MaterialItem(String nombre, int unidades, String almacen, String armario,
                     String estante, int unidadesMin, String descripcion) {
            this.nombre = nombre;
            this.unidades = unidades;
            this.almacen = almacen;
            this.armario = armario;
            this.estante = estante;
            this.unidadesMin = unidadesMin;
            this.descripcion = descripcion;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        tablaInventario = view.findViewById(R.id.TablaInventario);
        searchView      = view.findViewById(R.id.searchView);

        // Listener para filtrar
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

        obtenerDatosInventario();
        return view;
    }

    private void obtenerDatosInventario() {
        Utilidades.obtenerMateriales(new MaterialCallback() {
            @Override
            public void onMaterialObtenido(String nombre, int unidades, String almacen,
                                           String armario, String estante, int unidades_min,
                                           String descripcion) {
                materialesList.add(
                        new MaterialItem(nombre, unidades, almacen, armario,
                                estante, unidades_min, descripcion)
                );
                // Siempre refrescamos con TODO lo cargado
                refreshTabla(materialesList);
            }

            @Override
            public void onFailure(boolean error) {
                Log.e("Inventario", "Error al obtener inventario");
                Toast.makeText(getContext(),
                        "Error al obtener inventario",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshTabla(List<MaterialItem> lista) {
        // 1) Limpiamos filas antiguas (dejamos solo la cabecera)
        int childCount = tablaInventario.getChildCount();
        if (childCount > 1) {
            tablaInventario.removeViews(1, childCount - 1);
        }

        // 2) Recorremos la lista y construimos filas
        for (int i = 0; i < lista.size(); i++) {
            MaterialItem item = lista.get(i);
            TableRow fila = crearFila(item);

            // Solo al primer dato (i == 0) le damos margen superior
            if (i == 0) {
                // Convertimos 12 dp a pixeles
                int margenSuperiorPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        12,
                        getResources().getDisplayMetrics()
                );
                LayoutParams lp = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(0, margenSuperiorPx, 0, 0);
                fila.setLayoutParams(lp);
            }

            tablaInventario.addView(fila);
        }
    }

    private TableRow crearFila(final MaterialItem item) {
        TableRow fila = new TableRow(getContext());
        fila.setPadding(4, 4, 4, 4);

        TextView tvNombre = new TextView(getContext());
        tvNombre.setLayoutParams(
                new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f)
        );
        tvNombre.setText(item.nombre);
        tvNombre.setPadding(8, 8, 8, 8);

        TextView tvUnidades = new TextView(getContext());
        tvUnidades.setLayoutParams(
                new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f)
        );
        tvUnidades.setText(String.valueOf(item.unidades));
        tvUnidades.setPadding(8, 8, 8, 8);
        tvUnidades.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tvAlmacen = new TextView(getContext());
        tvAlmacen.setLayoutParams(
                new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f)
        );
        tvAlmacen.setText(item.almacen);
        tvAlmacen.setPadding(8, 8, 8, 8);

        TextView tvArmario = new TextView(getContext());
        tvArmario.setLayoutParams(
                new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f)
        );
        tvArmario.setText(item.armario);
        tvArmario.setPadding(8, 8, 8, 8);

        fila.addView(tvNombre);
        fila.addView(tvUnidades);
        fila.addView(tvAlmacen);
        fila.addView(tvArmario);

        fila.setOnClickListener(v -> {
            // Opcional: deshabilitamos el click para evitar dobles pulsaciones
            v.setEnabled(false);

            // Postea un Runnable con un delay (200 ms en este ejemplo)
            v.postDelayed(() -> {
                // Construye y muestra el diálogo
                MostrarInventario dialog = new MostrarInventario();
                Bundle args = new Bundle();
                args.putString("nombre",      item.nombre);
                args.putInt   ("unidades",    item.unidades);
                args.putString("almacen",     item.almacen);
                args.putString("armario",     item.armario);
                args.putString("estante",     item.estante);
                args.putInt   ("unidadesm",   item.unidadesMin);
                args.putString("descripcion", item.descripcion);
                dialog.setArguments(args);
                dialog.show(getParentFragmentManager(), "MostrarInventario");

                // Reactivamos el click
                v.setEnabled(true);
            }, 200);
        });


        return fila;
    }

    private void filtrarTabla(String query) {
        query = query.toLowerCase();
        List<MaterialItem> listaFiltrada = new ArrayList<>();
        for (MaterialItem item : materialesList) {
            if (item.nombre.toLowerCase().contains(query)) {
                listaFiltrada.add(item);
            }
        }
        refreshTabla(listaFiltrada);
    }
}
