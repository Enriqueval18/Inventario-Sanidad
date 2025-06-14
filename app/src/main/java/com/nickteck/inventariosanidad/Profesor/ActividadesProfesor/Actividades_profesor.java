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
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaFinalCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.Arrays;
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

        cargarActividadesDesdeApi();

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
    private void cargarActividadesDesdeApi() {
        Utilidades.verActiviadadesProfesor(new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                Log.d("Actividades_profesor", "Datos recibidos de API");

                listaActividades.clear();

                List<String> descripciones = parsearLista(respuesta.getDescripciones());
                List<String> materialesPorActividad = parsearLista(respuesta.getMateriales());
                List<String> unidadesPorActividad = parsearLista(respuesta.getUnidades());
                List<String> enviadosPorActividad = parsearLista(respuesta.getEnviados());

                for (int i = 0; i < descripciones.size(); i++) {
                    String descripcion = descripciones.get(i);

                    // Parsear materiales y unidades con |
                    List<String> materiales = new ArrayList<>();
                    List<Integer> cantidades = new ArrayList<>();

                    if (i < materialesPorActividad.size() && i < unidadesPorActividad.size()) {
                        String[] mats = materialesPorActividad.get(i).split("\\|");
                        String[] units = unidadesPorActividad.get(i).split("\\|");

                        for (int j = 0; j < mats.length; j++) {
                            materiales.add(mats[j]);
                            if (j < units.length) {
                                try {
                                    cantidades.add(Integer.parseInt(units[j]));
                                } catch (NumberFormatException e) {
                                    cantidades.add(0); // valor por defecto
                                }
                            } else {
                                cantidades.add(0);
                            }
                        }
                    }

                    boolean enviado = false;
                    if (i < enviadosPorActividad.size()) {
                        enviado = enviadosPorActividad.get(i).equals("1"); // o true/false según API
                    }

                    listaActividades.add(new ActividadItem(i, descripcion, materiales, cantidades, enviado));
                }

                adapter.actualizarListaCompleta(listaActividades);
            }

            @Override
            public void onFailure(boolean error) {
                Log.e("Actividades_profesor", "Error al cargar actividades");
            }
        });
    }
    private List<String> parsearLista(String data) {
        if (data == null || data.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(data.split(",")));
    }

}

