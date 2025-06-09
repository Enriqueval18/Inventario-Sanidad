package com.nickteck.inventariosanidad.Administrador;

import static com.nickteck.inventariosanidad.General.mostrarTarjetaSalida;
import static com.nickteck.inventariosanidad.General.resaltarBotonActivo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.nickteck.inventariosanidad.Administrador.InventarioAd.Dobleinventario;
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

        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
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
        btnSalir.setOnClickListener(v -> mostrarTarjetaSalida(this));

        return view;
    }
}
