package com.nickteck.inventariosanidad.Administrador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;

public class Dobleinventario extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doblein, container, false);
        TableRow tableRowMaterial01 = view.findViewById(R.id.tableRowMaterial001);
        tableRowMaterial01.setOnClickListener(v -> {
            MostarDobleinventario dialog = new MostarDobleinventario();
            dialog.show(getParentFragmentManager(), "MostarDobleinventario");
        });
        return view;
    }
}
