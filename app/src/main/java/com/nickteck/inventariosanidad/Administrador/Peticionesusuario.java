package com.nickteck.inventariosanidad.Administrador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;

import java.util.ArrayList;
import java.util.List;

public class Peticionesusuario extends Fragment {

    private LinearLayout contenedor_peticiones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peticiones, container, false);
        contenedor_peticiones = view.findViewById(R.id.contenedor_peticionesg);

        List<Request> requestList = new ArrayList<>();
        requestList.add(new Request("Pepe", "Detergente", "10"));
        requestList.add(new Request("Juan", "Jabón", "5"));
        requestList.add(new Request("Ana", "Escoba", "2"));

        for (Request req : requestList) {
            View cardView = inflater.inflate(R.layout.tarjeta_peticion, contenedor_peticiones, false);
            TextView tvUser = cardView.findViewById(R.id.Usuario_peticion);
            TextView tvMaterial = cardView.findViewById(R.id.peticion_material);
            TextView tvQuantity = cardView.findViewById(R.id.peticion_cantidad);

            tvUser.setText("Usuario: " + req.getUsuario());
            tvMaterial.setText("Material: " + req.getMaterial());
            tvQuantity.setText("Cantidad: " + req.getCantidad());

            contenedor_peticiones.addView(cardView);
        }
        return view;
    }

    /**
     * Clase interna para representar una petición.
     */
    public static class Request {
        private String usuario;
        private String material;
        private String cantidad;

        public Request(String user, String material, String quantity) {
            this.usuario = user;
            this.material = material;
            this.cantidad = quantity;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getMaterial() {
            return material;
        }

        public String getCantidad() {
            return cantidad;
        }
    }
}
