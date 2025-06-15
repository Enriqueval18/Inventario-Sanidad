package com.nickteck.inventariosanidad.Administrador;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.RespuestaFinalCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.Arrays;
import java.util.List;

public class Peticionesusuario extends Fragment {

    private LinearLayout contenedor_peticiones;
    private LayoutInflater layoutInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peticiones, container, false);
        contenedor_peticiones = view.findViewById(R.id.contenedor_peticionesg);
        layoutInflater = inflater;

        Utilidades.verPeticiones(new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                List<String> usuarios = Arrays.asList(respuesta.getNombre_usuario().split(","));
                List<String> materiales = Arrays.asList(respuesta.getMateriales().split(","));
                List<String> cantidades = Arrays.asList(respuesta.getUnidades().split(","));
                List<String> fechas = Arrays.asList(respuesta.getFecha_modificacion().split(","));
                List<String> ids = Arrays.asList(respuesta.getPeticiones_ids().split(","));

                int total = Math.min(Math.min(usuarios.size(), materiales.size()), Math.min(cantidades.size(), fechas.size()));
                total = Math.min(total, ids.size());

                for (int i = 0; i < total; i++) {
                    String usuario = usuarios.get(i);
                    String material = materiales.get(i);
                    String cantidad = cantidades.get(i);
                    String fecha = fechas.get(i);
                    int id;

                    try {
                        id = Integer.parseInt(ids.get(i));
                    } catch (NumberFormatException e) {
                        Log.e("PeticionError", "ID inválido: " + ids.get(i));
                        continue;
                    }

                    View cardView = layoutInflater.inflate(R.layout.tarjeta_peticion, contenedor_peticiones, false);
                    TextView tvUser = cardView.findViewById(R.id.Usuario_peticion);
                    TextView tvMaterial = cardView.findViewById(R.id.peticion_material);
                    TextView tvQuantity = cardView.findViewById(R.id.peticion_cantidad);
                    TextView tvFecha = cardView.findViewById(R.id.peticion_fecha);
                    ImageButton btnEliminar = cardView.findViewById(R.id.btnEliminarPeticion);

                    if (btnEliminar == null) {
                        Log.e("PeticionError", "No se encontró el botón btnEliminarPeticion en la tarjeta");
                        continue;
                    }

                    tvUser.setText("Usuario: " + usuario);
                    tvMaterial.setText("Material: " + material);
                    tvQuantity.setText("Cantidad: " + cantidad);
                    tvFecha.setText("Fecha: " + fecha);

                    int finalId = id;
                    btnEliminar.setOnClickListener(v -> {
                        Utilidades.eliminarPeticiones(finalId, new RespuestaCallback() {
                            @Override
                            public void onResultado(boolean exito) {
                                if (exito) {
                                    contenedor_peticiones.removeView(cardView);
                                    Toast.makeText(getContext(), "Petición eliminada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(boolean error) {
                                Toast.makeText(getContext(), "Error de red al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

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

