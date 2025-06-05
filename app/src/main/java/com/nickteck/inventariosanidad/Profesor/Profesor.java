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
import com.nickteck.inventariosanidad.Profesor.HistorialPro.HistorialFragment;
import com.nickteck.inventariosanidad.Profesor.MaterialesPro.Materiales;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.Usuario.Inventario;

public class Profesor extends Fragment {
    private TextView tvNombreUsuario, tvRolUsuario;

    private void resaltarBotonActivo(ImageView activo, ImageView... otros) {
        activo.setAlpha(1f);
        for (ImageView otro : otros) {
            otro.setAlpha(0.5f);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profesor, container, false);
        View navLayout = view.findViewById(R.id.navegacionpro);

        ImageView navHistorial    = navLayout.findViewById(R.id.btnHistorial);
        ImageView navInventario   = navLayout.findViewById(R.id.btnInventariopro);
        ImageView navActividades  = navLayout.findViewById(R.id.btnActividadespro);
        ImageView navMateriales   = navLayout.findViewById(R.id.btnMaterialespro);

        ImageView ftousuario = view.findViewById(R.id.ftouser);
        ftousuario.setImageResource(R.drawable.user_pro);

        tvNombreUsuario = view.findViewById(R.id.NombreUsuario);
        tvRolUsuario = view.findViewById(R.id.RolUsuario);

        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
            tvNombreUsuario.setText(nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
            tvRolUsuario.setText("Profesor");
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.contenedorpro, new HistorialFragment());
            transaction.commit();

            resaltarBotonActivo(navHistorial, navInventario, navActividades, navMateriales);
        }

        navHistorial.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new HistorialFragment());
            transaction.commit();

            resaltarBotonActivo(navHistorial, navInventario, navActividades, navMateriales);
        });

        navInventario.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new Inventario());
            transaction.commit();

            resaltarBotonActivo(navInventario, navHistorial, navActividades, navMateriales);
        });

        navActividades.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new Actividades_profesor());
            transaction.commit();

            resaltarBotonActivo(navActividades, navHistorial, navInventario, navMateriales);
        });

        navMateriales.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, new Materiales());
            transaction.commit();

            resaltarBotonActivo(navMateriales, navHistorial, navInventario, navActividades);
        });

        ImageView btnSalir = view.findViewById(R.id.BtnSalir);
        btnSalir.setOnClickListener(v -> Tarjeta_salida());

        return view;
    }

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
}
