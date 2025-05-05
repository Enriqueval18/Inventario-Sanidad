package com.nickteck.inventariosanidad;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class Usuario extends Fragment {
    private View indicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        ImageView navInventario = view.findViewById(R.id.btnInventario);
        ImageView navActividades = view.findViewById(R.id.btnActividades);

        // Referencia al indicador que se movera
        indicator = view.findViewById(R.id.indicator);



//        // Si es la primera vez que se crea el fragmento, cargar el fragmento "Cerrars" por defecto
//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//            transaction.replace(R.id.contenedorFragmento, new Cerrars());
//            transaction.commit();
//
//            // Posicionar el indicador en el botón Home (btnOpcion12)
//            navHome.post(() -> {
//                float targetCenterX = navHome.getX() + (navHome.getWidth() / 2f);
//                float newX = targetCenterX - (indicator.getWidth() / 2f);
//                indicator.setX(newX);
//            });
//        }
//
//        // Listener para el ícono "Home"
//        navHome.setOnClickListener(v -> {
//            animateIndicatorTo(v);
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.contenedorFragmento, new Cerrars());
//            transaction.commit();
//        });
//
//        // Listener para el ícono "Notificaciones"
//        navNotif.setOnClickListener(v -> {
//            animateIndicatorTo(v);
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.contenedorFragmento, new Actividades());
//            transaction.commit();
//        });
//
//        // Listener para el ícono "Perfil"
//        navUser.setOnClickListener(v -> {
//            animateIndicatorTo(v);
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.contenedorFragmento, new Inventario());
//            transaction.commit();
//        });

        return view;
    }

    /**
     * Metodo de animacion que hace que se mueva la parte inferior de la pantalla
     * @param targetView hace referencia a los botones de la navegacion
     */
    private void animacion_indicador(View targetView) {
        targetView.post(() -> {
            float targetCenterX = targetView.getX() + (targetView.getWidth() / 2f);
            float newX = targetCenterX - (indicator.getWidth() / 2f);
            indicator.animate().x(newX).setDuration(150).start();
        });
    }
}