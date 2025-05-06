package com.nickteck.inventariosanidad.Profesor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nickteck.inventariosanidad.R;

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
        historialItemList.add(new HistorialItem("08:30 AM", "Juan Pérez", "Flores", "42"));
        historialItemList.add(new HistorialItem("09:15 AM", "Ana Gómez", "Herramientas", "32"));
        historialItemList.add(new HistorialItem("10:00 AM", "Carlos Rodríguez", "Detergente","21"));
        adapter = new HistorialAdapter(historialItemList);
        recyclerHistorial.setAdapter(adapter);
        return view;
    }
}
