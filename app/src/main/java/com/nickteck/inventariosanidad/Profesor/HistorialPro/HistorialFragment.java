package com.nickteck.inventariosanidad.Profesor.HistorialPro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaFinalCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistorialFragment extends Fragment {

    private HistorialAdapter adapter;
    private Date desde, hasta;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerHistorial);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistorialAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        Button btnDesde = view.findViewById(R.id.btnFechaDesde);
        Button btnHasta = view.findViewById(R.id.btnFechaHasta);

        btnDesde.setOnClickListener(v -> mostrarDatepiker(date -> {
            desde = date;
            btnDesde.setText(String.format("Desde: %s", sdf.format(date)));
            adapter.filtrarfecha(desde, hasta);
        }));

        btnHasta.setOnClickListener(v -> mostrarDatepiker(date -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            hasta = cal.getTime();
            btnHasta.setText(String.format("Hasta: %s", sdf.format(date)));
            adapter.filtrarfecha(desde, hasta);
        }));

        // Cargar historial
        Utilidades.MostrarHistorial(new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                if (respuesta.isRespuesta()) {
                    HistorialItem item = new HistorialItem(
                            respuesta.getFecha_modificacion(),
                            respuesta.getNombre_usuario(),
                            respuesta.getMateriales(),
                            String.valueOf(respuesta.getUnidades_modificacion())
                    );
                    adapter.addItem(item);
                } else {
                    Toast.makeText(getContext(),
                            "Error: " + respuesta.getMensaje(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(boolean error) {
                Toast.makeText(getContext(),
                        "Fallo en la conexión con el servidor",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Muestra un DatePickerDialog y devuelve la fecha seleccionada
     */
    private void mostrarDatepiker(OnDateSelectedListener listener) {
        Calendar ahora = Calendar.getInstance();
        new DatePickerDialog(getContext(),
                (picker, year, month, day) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, day, 0, 0, 0);
                    listener.onDateSelected(cal.getTime());
                },
                ahora.get(Calendar.YEAR),
                ahora.get(Calendar.MONTH),
                ahora.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }
}
