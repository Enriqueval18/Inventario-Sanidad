package com.nickteck.inventariosanidad.Usuario.TabInventario;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.Usuario.MostrarInventario;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;
import com.nickteck.inventariosanidad.sampledata.Material;

public class Inventario extends Fragment {

    private TableLayout tablaInventario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        tablaInventario = view.findViewById(R.id.TablaInventario);
        obtenerDatosInventario();
        return view;
    }

    private void obtenerDatosInventario() {
        Utilidades.obtenerMateriales(new MaterialCallback() {
            @Override
            public void onMaterialObtenido(Material material) {
                agregarFila(material);
            }

            @Override
            public void onFailure(boolean error) {
                Log.e("Inventario", "Error al obtener inventario");
                Toast.makeText(getContext(), "Error al obtener inventario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarFila(final Material material) {
        TableRow fila = new TableRow(getContext());
        fila.setPadding(4, 4, 4, 4);

        TextView tvNombre = new TextView(getContext());
        tvNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvNombre.setText(material.getNombre());
        tvNombre.setPadding(8, 8, 8, 8);

        TextView tvDescripcion = new TextView(getContext());
        tvDescripcion.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvDescripcion.setText(material.getDescripcion());
        tvDescripcion.setPadding(8, 8, 8, 8);


        TextView tvUnidades = new TextView(getContext());
        tvUnidades.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvUnidades.setText(""); // O material.getUnidades() si existe
        tvUnidades.setPadding(8, 8, 8, 8);

        TextView tvExtra = new TextView(getContext());
        tvExtra.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvExtra.setText(""); // O material.getArmario() u otro dato
        tvExtra.setPadding(8, 8, 8, 8);

        fila.addView(tvNombre);
        fila.addView(tvDescripcion);
        fila.addView(tvUnidades);
        fila.addView(tvExtra);

        fila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarInventario dialog = new MostrarInventario();
                Bundle args = new Bundle();
                args.putString("nombre", material.getNombre());
                args.putString("descripcion", material.getDescripcion());
                dialog.setArguments(args);
                dialog.show(getParentFragmentManager(), "MostrarInventario");
            }
        });

        tablaInventario.addView(fila);
    }
}
