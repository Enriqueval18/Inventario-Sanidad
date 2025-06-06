package com.nickteck.inventariosanidad.Administrador;

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

import com.nickteck.inventariosanidad.Administrador.InventarioAd.Dobleinventario;
import com.nickteck.inventariosanidad.Login;
import com.nickteck.inventariosanidad.R;

public class Administrador extends Fragment {
    private TextView tvNombreUsuario, tvRolUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administrador, container, false);
        View navadmin = view.findViewById(R.id.navegacionadming);

        ImageView navAgregar = navadmin.findViewById(R.id.btnAgregar);
        ImageView navInventario = navadmin.findViewById(R.id.btnInventarioAdmin);
        ImageView navActividades = navadmin.findViewById(R.id.btnActividadesAdmin);
        ImageView ftousuario = view.findViewById(R.id.ftouser);
        ftousuario.setImageResource(R.drawable.user_admin);

        tvNombreUsuario = view.findViewById(R.id.NombreUsuario);
        tvRolUsuario = view.findViewById(R.id.RolUsuario);

        // Recogemos los argumentos enviados desde el login
        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
            String rol = args.getString("rol");
            //Sirve para poner la primera en mayuscula
            tvNombreUsuario.setText(nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
            tvRolUsuario.setText("Administrador");
        }



        if (savedInstanceState == null) {
            resaltarBotonActivo(navAgregar, navInventario, navActividades);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.contenedoradmin, new Administrar_usuarios());
            transaction.commit();

            navAgregar.post(() -> {
                float targetCenterX = navAgregar.getX() + (navAgregar.getWidth() / 2f);
            });
        }

        navAgregar.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedoradmin, new Administrar_usuarios());
            transaction.commit();
            resaltarBotonActivo(navAgregar, navInventario, navActividades);
        });

        navInventario.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedoradmin, new Peticionesusuario());
            transaction.commit();
            resaltarBotonActivo(navInventario, navAgregar, navActividades);

        });


        navActividades.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedoradmin, new Dobleinventario());
            transaction.commit();
            resaltarBotonActivo(navActividades, navAgregar, navInventario);
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
    private void resaltarBotonActivo(ImageView activo, ImageView... otros) {
        activo.setAlpha(1f);
        for (ImageView otro : otros) {
            otro.setAlpha(0.5f);
        }
    }

}
