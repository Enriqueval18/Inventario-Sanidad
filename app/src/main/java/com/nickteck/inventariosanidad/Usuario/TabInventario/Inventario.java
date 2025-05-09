package com.nickteck.inventariosanidad.Usuario.TabInventario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.nickteck.inventariosanidad.R;

import java.util.List;

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

    }

    private void actualizarTabla(List<InventarioItem> items) {
        for (InventarioItem item : items) {
            TableRow fila = new TableRow(getContext());
            fila.setPadding(4, 4, 4, 4);

            TextView tvNombre = new TextView(getContext());
            tvNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvNombre.setText(item.getNombre());
            tvNombre.setPadding(8, 8, 8, 8);

            TextView tvUnidades = new TextView(getContext());
            tvUnidades.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvUnidades.setText(String.valueOf(item.getUnidades()));
            tvUnidades.setPadding(8, 8, 8, 8);

            TextView tvAlmacen = new TextView(getContext());
            tvAlmacen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvAlmacen.setText(item.getAlmacen());
            tvAlmacen.setPadding(8, 8, 8, 8);

            TextView tvArmario = new TextView(getContext());
            tvArmario.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvArmario.setText(item.getArmario());
            tvArmario.setPadding(8, 8, 8, 8);


            fila.addView(tvNombre);
            fila.addView(tvUnidades);
            fila.addView(tvAlmacen);
            fila.addView(tvArmario);


            tablaInventario.addView(fila);
        }
    }
}
