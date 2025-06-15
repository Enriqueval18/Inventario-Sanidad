package com.nickteck.inventariosanidad.Administrador;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaFinalCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.Arrays;
import java.util.List;

public class Peticionesusuario extends Fragment {

    private LinearLayout contenedor_peticiones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peticiones, container, false);
        contenedor_peticiones = view.findViewById(R.id.contenedor_peticionesg);

        Utilidades.verPeticiones(new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                Log.d("Peticionesusuario", "Datos recibidos correctamente");

                List<String> usuarios = Arrays.asList(respuesta.getNombre_usuario().split(","));
                List<String> materiales = Arrays.asList(respuesta.getMateriales().split(","));
                List<String> cantidades = Arrays.asList(respuesta.getUnidades().split(","));
                List<String> fechas = Arrays.asList(respuesta.getFecha_modificacion().split(","));

                for (int i = 0; i < materiales.size(); i++) {
                    View cardView = inflater.inflate(R.layout.tarjeta_peticion, contenedor_peticiones, false);
                    TextView tvUser = cardView.findViewById(R.id.Usuario_peticion);
                    TextView tvMaterial = cardView.findViewById(R.id.peticion_material);
                    TextView tvQuantity = cardView.findViewById(R.id.peticion_cantidad);
                    TextView tvFecha = cardView.findViewById(R.id.peticion_fecha);

                    tvUser.setText("Usuario: " + usuarios.get(i));
                    tvMaterial.setText("Material: " + materiales.get(i));
                    tvQuantity.setText("Cantidad: " + cantidades.get(i));
                    tvFecha.setText("Fecha: " + fechas.get(i));

                    contenedor_peticiones.addView(cardView);
                }
            }

            @Override
            public void onFailure(boolean error) {
                Toast.makeText(getContext(), "Error al cargar peticiones", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
