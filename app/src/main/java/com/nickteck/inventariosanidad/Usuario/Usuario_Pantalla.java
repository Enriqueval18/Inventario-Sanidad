package com.nickteck.inventariosanidad.Usuario;

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
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.Usuario.ActividadesUsu.Actividades;
import com.nickteck.inventariosanidad.Usuario.Inventariousu.Inventario;

public class Usuario_Pantalla extends Fragment {
    int id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        ImageView navInventario = view.findViewById(R.id.btnInventario);
        ImageView navActividades = view.findViewById(R.id.btnActividades);

        TextView nombreusuario = view.findViewById(R.id.NombreUsuario);
        TextView rolusuario = view.findViewById(R.id.RolUsuario);

        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
            assert nombre != null;
            nombreusuario.setText(String.format("%s%s", nombre.substring(0, 1).toUpperCase(), nombre.substring(1)));
            id = args.getInt("id");
            rolusuario.setText(R.string.estudiante_usua);
        }
        Log.e("usuarioinicio", ""+id);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.contenedorFragmento, new Inventario());
            transaction.commit();
            resaltarBotonActivo(navInventario, navActividades);

        }
        navInventario.setOnClickListener(v -> {

            Inventario inventarioFragment = new Inventario();

            Bundle bundle = new Bundle();
            bundle.putInt("idUsuario", id);
            inventarioFragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragmento, inventarioFragment);
            transaction.commit();

            resaltarBotonActivo(navInventario, navActividades);
        });


        navActividades.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragmento, new Actividades());
            transaction.commit();
            resaltarBotonActivo(navActividades, navInventario);
        });

        ImageView btnSalir = view.findViewById(R.id.BtnSalir);

        btnSalir.setOnClickListener(v ->  mostrarTarjetaSalida(this));
        return view;
    }
}