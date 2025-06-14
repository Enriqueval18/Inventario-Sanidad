package com.nickteck.inventariosanidad.Profesor.HistorialPro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaFinalCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private RecyclerView recyclerHistorial;
    private HistorialAdapter adapter;
    private List<HistorialItem> historialItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        recyclerHistorial = view.findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(getContext()));

        historialItemList = new ArrayList<>();
        adapter = new HistorialAdapter(historialItemList);
        recyclerHistorial.setAdapter(adapter);

        // Llamada a Utilidades para obtener historial desde la API
        Utilidades.MostrarHistorial(new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                if (respuesta.isRespuesta()) {
                    // Convertimos el objeto Respuesta en un HistorialItem
                    HistorialItem item = new HistorialItem(
                            respuesta.getFecha_modificacion(),
                            respuesta.getNombre_usuario(),
                            respuesta.getMateriales(),
                            String.valueOf(respuesta.getUnidades_modificacion())
                    );

                    historialItemList.add(item);
                    adapter.notifyItemInserted(historialItemList.size() - 1);
                } else {
                    Toast.makeText(getContext(), "Error: " + respuesta.getMensaje(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(boolean error) {
                Toast.makeText(getContext(), "Fallo en la conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
