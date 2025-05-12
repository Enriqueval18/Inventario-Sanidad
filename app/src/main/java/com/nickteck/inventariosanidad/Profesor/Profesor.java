package com.nickteck.inventariosanidad.Profesor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nickteck.inventariosanidad.Login;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.Usuario.Inventario;

public class Profesor extends Fragment {
    private View indicador;
    private TextView tvNombreUsuario, tvRolUsuario;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profesor, container, false);
        View navLayout = view.findViewById(R.id.navegacionpro);
        ImageView navHistorial  = navLayout.findViewById(R.id.btnHistorial);
        ImageView navInventario = navLayout.findViewById(R.id.btnInventariopro);
        ImageView navActividades = navLayout.findViewById(R.id.btnActividadespro);
        ImageView navMateriales  = navLayout.findViewById(R.id.btnMaterialespro);
        indicador = navLayout.findViewById(R.id.indicadorpesadmin);

        tvNombreUsuario = view.findViewById(R.id.NombreUsuario);
        tvRolUsuario = view.findViewById(R.id.RolUsuario);

        // Recogemos los argumentos enviados desde el login
        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
            String rol = args.getString("rol");
            //Sirve para poner la primera en mayuscula
            tvNombreUsuario.setText(nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
            tvRolUsuario.setText(rol.substring(0, 1).toUpperCase() + rol.substring(1));
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.contenedorpro, new HistorialFragment());
            transaction.commit();

            navHistorial.post(() -> {
                float targetCenterX = navHistorial.getX() + (navHistorial.getWidth() / 2f);
                float newX = targetCenterX - (indicador.getWidth() / 2f);
                indicador.setX(newX);
            });
        }

        navHistorial.setOnClickListener(v -> {
            animacion_indicador(v);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new HistorialFragment());
            transaction.commit();
        });

        navInventario.setOnClickListener(v -> {
            animacion_indicador(v);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new Inventario());
            transaction.commit();
        });


        navActividades.setOnClickListener(v -> {
            animacion_indicador(v);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new Actividades_profesor());
            transaction.commit();
        });

        navMateriales.setOnClickListener(v -> {
            animacion_indicador(v);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new Materiales());
            transaction.commit();
        });

        ImageView btnSalir = view.findViewById(R.id.BtnSalir);
        btnSalir.setOnClickListener(v -> Tarjeta_salida());

        return view;
    }

    /**
     * Metodo que hace que aparezca al usuario una tarjeta para decidir si desea cerrar sesión o no
     */
    private void Tarjeta_salida() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.tarjeta_salir, null);
        Button btnQuedarme = dialogView.findViewById(R.id.btnQuedarme);
        Button btnSalirDialog = dialogView.findViewById(R.id.btnSalir);
        AlertDialog logoutDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        Animation bounce = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce);

        btnQuedarme.setOnClickListener(v -> {
            v.startAnimation(bounce);
            v.postDelayed(() -> logoutDialog.dismiss(), bounce.getDuration());
        });
        btnSalirDialog.setOnClickListener(v -> {
            v.startAnimation(bounce);
            v.postDelayed(() -> {
                Login loginFragment = new Login();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(android.R.id.content, loginFragment);
                transaction.commit();
                logoutDialog.dismiss();
            }, bounce.getDuration());
        });
        logoutDialog.show();
    }

    /**
     * Mueve el indicador con una animación para posicionarlo debajo del icono pulsado.
     */
    private void animacion_indicador(View targetView) {
        targetView.post(() -> {
            float targetCenterX = targetView.getX() + (targetView.getWidth() / 2f);
            float newX = targetCenterX - (indicador.getWidth() / 2f);
            indicador.animate().x(newX).setDuration(150).start();
        });
    }
}
