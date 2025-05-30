package com.nickteck.inventariosanidad.Administrador;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.Usuario;
import com.nickteck.inventariosanidad.sampledata.UsuarioCallback;
import com.nickteck.inventariosanidad.sampledata.UsuarioCallback2;
import com.nickteck.inventariosanidad.sampledata.UsuarioListCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.List;

public class Administrar_usuarios extends Fragment {

    private LinearLayout tabla;
    private View seleccionar_usuario = null;
    private Usuario usuarioSeleccionado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_usuarios, container, false);

        tabla = view.findViewById(R.id.tabla_usuarios);

        Button btnAnadir = view.findViewById(R.id.btnanausua);
        Button btnEliminar = view.findViewById(R.id.btneliminarusu);
        Button btnCambiar = view.findViewById(R.id.btnmodiusu);

        btnAnadir.setOnClickListener(v -> mostrarDialogoAgregarUsuario());

        btnEliminar.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                eliminarUsuarioSeleccionado();
            } else {
                Toast.makeText(getContext(), "Seleccione un usuario primero", Toast.LENGTH_SHORT).show();
            }
        });

        btnCambiar.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                mostrarDialogoEditarUsuario();
            } else {
                Toast.makeText(getContext(), "Seleccione un usuario primero", Toast.LENGTH_SHORT).show();
            }
        });

        cargarUsuariosExistentes();
        return view;
    }

    private void mostrarDialogoAgregarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Añadir Nuevo Usuario");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialogo_anadir_user, null);
        builder.setView(dialogView);

        EditText etNombre = dialogView.findViewById(R.id.et_nombre);
        EditText etApellido = dialogView.findViewById(R.id.et_apellido);
        EditText etEmail = dialogView.findViewById(R.id.et_email);
        EditText etPassword = dialogView.findViewById(R.id.et_password);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinner_tipo);

        // Configurar spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.tipos_usuario,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        builder.setPositiveButton("Añadir", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String tipoTraducido = spinnerTipo.getSelectedItem().toString();
            String tipoOriginal = traducirTipoUsuarioInverso(tipoTraducido);

            // Validación básica
            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear objeto Usuario (usando el constructor correcto)
            Usuario nuevoUsuario = new Usuario(nombre, apellido, email, password, tipoOriginal);

            Utilidades.añadirUsuario(nuevoUsuario, new RespuestaCallback() {
                @Override
                public void onResultado(boolean exito) {
                    requireActivity().runOnUiThread(() -> {
                        if (exito) {
                            Toast.makeText(getContext(), "Usuario añadido con éxito", Toast.LENGTH_SHORT).show();
                            cargarUsuariosExistentes();
                        }
                    });
                }

                @Override
                public void onFailure(boolean error) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void eliminarUsuarioSeleccionado() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de eliminar a " +
                        usuarioSeleccionado.getFirst_name() + " " +
                        usuarioSeleccionado.getLast_name() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Llamar al método de utilidades para eliminar
                    Utilidades.eliminarUsuario(usuarioSeleccionado, new RespuestaCallback() {
                        @Override
                        public void onResultado(boolean exito) {
                            requireActivity().runOnUiThread(() -> {
                                if (exito) {
                                    Toast.makeText(
                                            getContext(),
                                            "Usuario eliminado",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Resetear selección
                                    usuarioSeleccionado = null;
                                    if (seleccionar_usuario != null) {
                                        seleccionar_usuario.setBackgroundResource(R.drawable.background_white_square);
                                        seleccionar_usuario = null;
                                    }

                                    // Actualizar lista
                                    cargarUsuariosExistentes();
                                }
                            });
                        }

                        @Override
                        public void onFailure(boolean error) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(
                                        getContext(),
                                        "Error de conexión",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void mostrarDialogoEditarUsuario() {
        // Crear el diseño del diálogo
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        // Crear los campos de texto
        final EditText etNuevaContrasena = new EditText(getContext());
        etNuevaContrasena.setHint("Nueva contraseña");
        etNuevaContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText etConfirmarContrasena = new EditText(getContext());
        etConfirmarContrasena.setHint("Confirmar contraseña");
        etConfirmarContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Añadir campos al layout
        layout.addView(etNuevaContrasena);
        layout.addView(etConfirmarContrasena);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cambiar contraseña de " + usuarioSeleccionado.getFirst_name());
        builder.setView(layout);

        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String nuevaContrasena = etNuevaContrasena.getText().toString().trim();
            String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();

            // Validaciones
            if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(getContext(), "Ambos campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nuevaContrasena.equals(confirmarContrasena)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear objeto Usuario con los datos necesarios
            Usuario usuarioActualizado = new Usuario(
                    usuarioSeleccionado.getUser_id(),
                    nuevaContrasena
            );

            // Llamar al método para actualizar contraseña
            Utilidades.actualizarContra(usuarioActualizado, new UsuarioCallback() {
                @Override
                public void onResultado(String tipo) {
                    // Manejar respuesta exitosa en el hilo principal
                    getActivity().runOnUiThread(() -> {
                    });
                }

                @Override
                public void onFailure(boolean error) {
                    // Manejar error en el hilo principal
                    getActivity().runOnUiThread(() -> {
                    });
                }
            });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void cargarUsuariosExistentes() {
        Utilidades.mostrarUsuarios(new UsuarioListCallback() {
            @Override
            public void onUsuariosObtenidos(List<Usuario> usuarios) {
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

        // Configuración común para las celdas
        LinearLayout.LayoutParams lpCelda = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        lpCelda.setMargins(4, 4, 4, 4);

        // Celda 1: Nombre completo
        TextView tvNombre = new TextView(getContext());
        tvNombre.setLayoutParams(lpCelda);
        tvNombre.setPadding(8, 16, 8, 16);
        tvNombre.setText(usuario.getFirst_name() + " " + usuario.getLast_name());

        // Celda 2: Correo
        TextView tvCorreo = new TextView(getContext());
        tvCorreo.setLayoutParams(lpCelda);
        tvCorreo.setPadding(8, 16, 8, 16);
        tvCorreo.setText(usuario.getEmail());

        // Celda 3: Tipo de usuario (traducido)
        TextView tvTipo = new TextView(getContext());
        tvTipo.setLayoutParams(lpCelda);
        tvTipo.setPadding(8, 16, 8, 16);
        tvTipo.setText(traducirTipoUsuario(usuario.getUser_type()));

        // Campo oculto para almacenar el ID del usuario
        TextView tvId = new TextView(getContext());
        tvId.setVisibility(View.GONE);
        tvId.setText(String.valueOf(usuario.getUser_id()));

        // Configurar clic para selección
        fila.setOnClickListener(v -> {
            if (seleccionar_usuario != null) {
                seleccionar_usuario.setBackgroundResource(R.drawable.background_white_square);
            }

            v.setBackgroundResource(R.drawable.rounded_indicator);
            seleccionar_usuario = v;

            usuarioSeleccionado = usuario;
        });

        // Añadir elementos a la fila
        fila.addView(tvNombre);
        fila.addView(tvCorreo);
        fila.addView(tvTipo);
        fila.addView(tvId);

        tabla.addView(fila);
    }

    // Para mostrar en la interfaz
    private String traducirTipoUsuario(String tipo) {
        if (tipo == null) return "Desconocido";

        switch (tipo.toLowerCase()) {
            case "teacher": return "Profesor";
            case "student": return "Usuario";
            case "admin": return "Administrador";
            default: return tipo;
        }
    }

    // Para enviar al servidor
    private String traducirTipoUsuarioInverso(String tipoTraducido) {
        if (tipoTraducido == null) return "student";

        switch (tipoTraducido) {
            case "Profesor": return "teacher";
            case "Usuario": return "student";
            case "Administrador": return "admin";
            default: return "student";
        }
    }
}