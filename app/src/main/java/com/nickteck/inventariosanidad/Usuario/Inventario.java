package com.nickteck.inventariosanidad.Usuario;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.nickteck.inventariosanidad.R;
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
            public void onMaterialObtenido(String nombre, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion) {
                agregarFila(nombre, unidades, almacen, armario, estante,unidades_min,descripcion);
            }

            @Override
            public void onFailure(boolean error) {
                Log.e("Inventario", "Error al obtener inventario");
                Toast.makeText(getContext(), "Error al obtener inventario", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void agregarFila(String nombre, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion) {
        TableRow fila = new TableRow(getContext());
        fila.setPadding(4, 4, 4, 4);

        TextView tvNombre = new TextView(getContext());
        tvNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvNombre.setText(nombre);
        tvNombre.setPadding(8, 8, 8, 8);

        TextView tvUnidades = new TextView(getContext());
        tvUnidades.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvUnidades.setText(String.valueOf(unidades));
        tvUnidades.setPadding(8, 8, 8, 8);
        tvUnidades.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tvAlmacen = new TextView(getContext());
        tvAlmacen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvAlmacen.setText(almacen);
        tvAlmacen.setPadding(8, 8, 8, 8);

        TextView tvArmario = new TextView(getContext());
        tvArmario.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tvArmario.setText(armario);
        tvArmario.setPadding(8, 8, 8, 8);

        fila.addView(tvNombre);
        fila.addView(tvUnidades);
        fila.addView(tvAlmacen);
        fila.addView(tvArmario);

        fila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarInventario dialog = new MostrarInventario();
                Bundle args = new Bundle();
                args.putString("nombre", nombre);
                args.putInt("unidades", unidades);
                args.putString("almacen",almacen);
                args.putString("armario", armario);
                args.putString("estante", estante);
                args.putInt("unidadesm", unidades_min);
                args.putString("descripcion", descripcion);
                dialog.setArguments(args);
                dialog.show(getParentFragmentManager(), "MostrarInventario");
            }
        });

        tablaInventario.addView(fila);
    }

}
