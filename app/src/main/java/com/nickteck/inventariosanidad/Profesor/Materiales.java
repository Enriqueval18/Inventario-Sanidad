package com.nickteck.inventariosanidad.Profesor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;

public class Materiales extends Fragment {
    private Button bntAnanir;
    private LinearLayout contenedorg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_materiales, container, false);
        bntAnanir = view.findViewById(R.id.btnPedirMateriales);
        contenedorg = view.findViewById(R.id.contenedortarjetas);

        bntAnanir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.pedir_material, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pedir Nuevo Item");
                builder.setView(dialogView)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etNombre = dialogView.findViewById(R.id.etNombre);
                                EditText etMateriales = dialogView.findViewById(R.id.etMateriales);

                                // Capturar los datos ingresados en el diálogo
                                String nombre = etNombre.getText().toString().trim();
                                String materiales = etMateriales.getText().toString().trim();

                                if (!nombre.isEmpty() && !materiales.isEmpty()) {
                                    // Se añade la nueva card al contenedor
                                    addNewSection(nombre, materiales);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null);

                builder.create().show();
            }
        });

        return view;
    }

    /**
     * Infla el layout de la card  y asigna los valores
     * @param nombre Título o nombre del material.
     * @param materiales Información adicional (por ejemplo, código y cantidad).
     */
    private void addNewSection(String nombre, String materiales) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_material_custom, contenedorg, false);
        TextView tvTitle = cardView.findViewById(R.id.tvTitle);
        TextView tvDetail = cardView.findViewById(R.id.tvDetail);
        tvTitle.setText(nombre);
        tvDetail.setText(materiales);
        contenedorg.addView(cardView);
    }
}
