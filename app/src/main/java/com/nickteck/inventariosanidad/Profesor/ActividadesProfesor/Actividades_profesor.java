package com.nickteck.inventariosanidad.Profesor.ActividadesProfesor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nickteck.inventariosanidad.R;

import java.util.ArrayList;
import java.util.List;

public class Actividades_profesor extends Fragment {
    private EditText Cbusqueda;
    private RecyclerView recyclerActividades;
    private ActividadesAdapter adapter;
    private List<ActividadItem> listaActividades = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Actividades_profesor", "onCreateView: Fragmento cargado");

        View view = inflater.inflate(R.layout.fragment_actividades_profesor, container, false);

        Cbusqueda = view.findViewById(R.id.cuadrobusqueda);
        recyclerActividades = view.findViewById(R.id.recyclerActividades);

        recyclerActividades.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ActividadesAdapter(listaActividades, getContext());
        recyclerActividades.setAdapter(adapter);

        cargarDatosDeEjemplo();

        Cbusqueda.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filtrar(s.toString());
            }
        });

        return view;
    }


    private void cargarDatosDeEjemplo() {
        Log.d("Actividades_profesor", "cargarDatosDeEjemplo: Agregando datos de ejemplo");

        List<String> materiales1 = new ArrayList<>();
        materiales1.add("Guantes");
        materiales1.add("Mascarilla");

        List<Integer> cantidades1 = new ArrayList<>();
        cantidades1.add(10);
        cantidades1.add(5);

        List<String> materiales2 = new ArrayList<>();
        materiales2.add("Alcohol");
        materiales2.add("Termómetro");

        List<Integer> cantidades2 = new ArrayList<>();
        cantidades2.add(7);
        cantidades2.add(3);

        listaActividades.add(new ActividadItem(1, "Actividad 1", materiales1, cantidades1, false));
        listaActividades.add(new ActividadItem(2, "Actividad 2", materiales2, cantidades2, true));

        Log.d("Fragment", "Lista tamaño: " + listaActividades.size());
        adapter.actualizarListaCompleta(listaActividades);
    }
}

