package com.nickteck.inventariosanidad.Usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;

public class Inventario extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        TableRow tabinvenario = view.findViewById(R.id.Tablafilamaterial);
        tabinvenario.setOnClickListener(v -> {
            MostrarInventario ventanaocapa = new MostrarInventario();
            ventanaocapa.show(getParentFragmentManager(), "MostrarInventario");
        });

        return view;
    }
}
