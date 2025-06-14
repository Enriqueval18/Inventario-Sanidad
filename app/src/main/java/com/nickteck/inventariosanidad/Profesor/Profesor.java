package com.nickteck.inventariosanidad.Profesor;

import static com.nickteck.inventariosanidad.General.mostrarTarjetaSalida;
import static com.nickteck.inventariosanidad.General.resaltarBotonActivo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nickteck.inventariosanidad.Profesor.ActividadesProfesor.Actividades_profesor;
import com.nickteck.inventariosanidad.Profesor.HistorialPro.HistorialFragment;
import com.nickteck.inventariosanidad.Profesor.MaterialesPro.Materiales;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.Usuario.Inventariousu.Inventario;

public class Profesor extends Fragment {
    int id;
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

        TextView nombreusuario = view.findViewById(R.id.NombreUsuario);
        TextView rolusuarios = view.findViewById(R.id.RolUsuario);

        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
            assert nombre != null;
            nombreusuario.setText(String.format("%s%s", nombre.substring(0, 1).toUpperCase(), nombre.substring(1)));
             id = args.getInt("id");
            rolusuarios.setText(R.string.profesor);
        }
        Log.e("usuarioinicio", ""+id);
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

            Inventario inventarioFragment = new Inventario();
            Bundle bundle = new Bundle();
            bundle.putInt("idUsuario", id);
            inventarioFragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorpro, inventarioFragment);
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
        btnSalir.setOnClickListener(v -> mostrarTarjetaSalida(this));

        return view;
    }

}
