package com.nickteck.inventariosanidad.Administrador;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Usuario;
import com.nickteck.inventariosanidad.sampledata.UsuarioListCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.List;

public class Administrar_usuarios extends Fragment {

    private LinearLayout tabla; // Contenedor de filas de usuarios
    private View seleccionar_usuario = null;
    private Usuario usuarioSeleccionado; // Para almacenar el usuario seleccionado

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_usuarios, container, false);

        // Referencia al contenedor de usuarios
        tabla = view.findViewById(R.id.tabla_usuarios);

        // Configurar botones
        Button btnAnadir = view.findViewById(R.id.btnanausua);
        Button btnEliminar = view.findViewById(R.id.btneliminarusu);
        Button btnCambiar = view.findViewById(R.id.btnmodiusu);

        btnAnadir.setOnClickListener(v -> {
            // Lógica para añadir nuevo usuario
            Toast.makeText(getContext(), "Funcionalidad añadir usuario", Toast.LENGTH_SHORT).show();
        });

        btnEliminar.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                // Lógica para eliminar usuario seleccionado
                Toast.makeText(getContext(), "Eliminar usuario: " + usuarioSeleccionado.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Seleccione un usuario primero", Toast.LENGTH_SHORT).show();
            }
        });

        btnCambiar.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                // Lógica para modificar usuario seleccionado
                Toast.makeText(getContext(), "Modificar usuario: " + usuarioSeleccionado.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Seleccione un usuario primero", Toast.LENGTH_SHORT).show();
            }
        });

        cargarUsuariosExistentes();
        return view;
    }

    private void cargarUsuariosExistentes() {
        Utilidades.mostrarUsuarios(new UsuarioListCallback() {
            @Override
            public void onUsuariosObtenidos(List<Usuario> usuarios) {
                // Limpiar tabla antes de agregar nuevos usuarios
                tabla.removeAllViews();

                for (Usuario usuario : usuarios) {
                    anadir_usuario_item(usuario);
                }
            }

            @Override
            public void onFailure(boolean error) {
                Toast.makeText(getContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void anadir_usuario_item(Usuario usuario) {
        // Crear contenedor de fila
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpFila = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lpFila.setMargins(0, 8, 0, 8);
        fila.setLayoutParams(lpFila);
        fila.setBackgroundResource(R.drawable.background_white_square);

        // Celda para el nombre
        TextView tvNombre = new TextView(getContext());
        LinearLayout.LayoutParams lpCelda = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        tvNombre.setLayoutParams(lpCelda);
        tvNombre.setPadding(16, 16, 16, 16);
        tvNombre.setText(usuario.getFirst_name() + " " + usuario.getLast_name());

        // Celda para el rol
        TextView tvRol = new TextView(getContext());
        tvRol.setLayoutParams(lpCelda);
        tvRol.setPadding(16, 16, 16, 16);
        tvRol.setText(usuario.getUser_type());

        // Celda para el email (oculta, para uso interno)
        TextView tvEmail = new TextView(getContext());
        tvEmail.setVisibility(View.GONE);
        tvEmail.setText(usuario.getEmail());

        // Configurar clic para selección
        fila.setOnClickListener(v -> {
            // Restablecer selección anterior
            if (seleccionar_usuario != null) {
                seleccionar_usuario.setBackgroundResource(R.drawable.background_white_square);
            }

            // Establecer nueva selección
            v.setBackgroundResource(R.drawable.rounded_indicator);
            seleccionar_usuario = v;

            // Almacenar usuario seleccionado
            usuarioSeleccionado = usuario;
        });

        // Añadir elementos a la fila
        fila.addView(tvNombre);
        fila.addView(tvRol);
        fila.addView(tvEmail); // Email oculto para referencia

        // Añadir fila a la tabla
        tabla.addView(fila);
    }
}