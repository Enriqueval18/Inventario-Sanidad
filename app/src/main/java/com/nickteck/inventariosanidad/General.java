package com.nickteck.inventariosanidad;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class General {

    /**
     * Resalta los botones de navegacion
     * @param activo opacidad al 100%
     * @param otros opacidad al 50%
     */
    public static void resaltarBotonActivo(ImageView activo, ImageView... otros) {
        activo.setAlpha(1f);
        for (ImageView otro : otros) {
            otro.setAlpha(0.5f);
        }
    }

    /**
     * Metodo que hace que aparezca al usuario una tarjeta para decidir si desea cerrar sesión o no
     * @param fragment hace referencia al fragmento al cual se le va a aplicar
     */
    public static void mostrarTarjetaSalida(Fragment fragment) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View dialogView = inflater.inflate(R.layout.tarjeta_salir, null);
        Button btnQuedarme = dialogView.findViewById(R.id.btnQuedarme);
        Button btnSalirDialog = dialogView.findViewById(R.id.btnSalir);

        AlertDialog logoutDialog = new AlertDialog.Builder(fragment.getContext())
                .setView(dialogView)
                .create();

        Animation bounce = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.bounce);

        btnQuedarme.setOnClickListener(v -> {
            v.startAnimation(bounce);
            v.postDelayed(logoutDialog::dismiss, bounce.getDuration());
        });

        btnSalirDialog.setOnClickListener(v -> {
            v.startAnimation(bounce);
            v.postDelayed(() -> {
                FragmentTransaction transaction = fragment.requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(android.R.id.content, new Login());
                transaction.commit();
                logoutDialog.dismiss();
            }, bounce.getDuration());
        });

        logoutDialog.show();
    }







}
