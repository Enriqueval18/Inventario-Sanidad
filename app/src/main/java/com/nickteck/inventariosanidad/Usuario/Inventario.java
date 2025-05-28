package com.nickteck.inventariosanidad.Usuario;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TableLayout;
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
    // Lista para almacenar todos los materiales recibidos
    private List<MaterialItem> materialesList = new ArrayList<>();

    // Clase interna para almacenar los datos de cada material
    private class MaterialItem {
        String nombre;
        int unidades;
        String almacen;
        String armario;
        String estante;
        int unidadesMin;
        String descripcion;

        MaterialItem(String nombre, int unidades, String almacen, String armario, String estante, int unidadesMin, String descripcion) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Se asume que en fragment_inventario.xml tienes el SearchView con id "searchView"
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        tablaInventario = view.findViewById(R.id.TablaInventario);
        searchView = view.findViewById(R.id.searchView);

        // Configuramos el listener del SearchView para filtrar la tabla
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
            public void onMaterialObtenido(String nombre, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion) {
                // Creamos un objeto MaterialItem con los datos recibidos
                MaterialItem item = new MaterialItem(nombre, unidades, almacen, armario, estante, unidades_min, descripcion);
                materialesList.add(item);
                // Actualizamos la tabla con la lista completa (sin filtro)
                refreshTabla(materialesList);
            }

            @Override
            public void onFailure(boolean error) {
                Log.e("Inventario", "Error al obtener inventario");
                Toast.makeText(getContext(), "Error al obtener inventario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para reconstruir la tabla con la lista de materiales indicada
    private void refreshTabla(List<MaterialItem> lista) {
        // Suponiendo que el primer TableRow es la cabecera, removemos todos los datos a partir del índice 1.
        int childCount = tablaInventario.getChildCount();
        if (childCount > 1) {
            tablaInventario.removeViews(1, childCount - 1);
        }
        // Recorremos la lista y agregamos un TableRow para cada material
        for (MaterialItem item : lista) {
            TableRow fila = crearFila(item);
            tablaInventario.addView(fila);
        }
    }

    // Crea y retorna un TableRow para un MaterialItem dado
    private TableRow crearFila(final MaterialItem item) {
        TableRow fila = new TableRow(getContext());
        fila.setPadding(4, 4, 4, 4);

        TextView tvNombre = new TextView(getContext());
        tvNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvNombre.setText(item.nombre);
        tvNombre.setPadding(8, 8, 8, 8);

        TextView tvUnidades = new TextView(getContext());
        tvUnidades.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvUnidades.setText(String.valueOf(item.unidades));
        tvUnidades.setPadding(8, 8, 8, 8);
        tvUnidades.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tvAlmacen = new TextView(getContext());
        tvAlmacen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvAlmacen.setText(item.almacen);
        tvAlmacen.setPadding(8, 8, 8, 8);

        TextView tvArmario = new TextView(getContext());
        tvArmario.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvArmario.setText(item.armario);
        tvArmario.setPadding(8, 8, 8, 8);

        // Agregamos cada TextView a la fila
        fila.addView(tvNombre);
        fila.addView(tvUnidades);
        fila.addView(tvAlmacen);
        fila.addView(tvArmario);

        // Al presionar la fila se muestra un diálogo con los detalles
        fila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarInventario dialog = new MostrarInventario();
                Bundle args = new Bundle();
                args.putString("nombre", item.nombre);
                args.putInt("unidades", item.unidades);
                args.putString("almacen", item.almacen);
                args.putString("armario", item.armario);
                args.putString("estante", item.estante);
                args.putInt("unidadesm", item.unidadesMin);
                args.putString("descripcion", item.descripcion);
                dialog.setArguments(args);
                dialog.show(getParentFragmentManager(), "MostrarInventario");
            }
        });

        return fila;
    }

    // Método que filtra la lista de materiales por el nombre y refresca la tabla
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
