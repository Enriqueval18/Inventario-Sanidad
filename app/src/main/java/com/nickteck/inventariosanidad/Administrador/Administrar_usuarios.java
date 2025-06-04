package com.nickteck.inventariosanidad.Administrador;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.Usuario;
import com.nickteck.inventariosanidad.sampledata.UsuarioCallback;
import com.nickteck.inventariosanidad.sampledata.UsuarioListCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;
import java.util.ArrayList;
import java.util.List;

public class Administrar_usuarios extends Fragment {
    private LinearLayout tabla;
    private View seleccionar_usuario = null;
    private Usuario usuarioSeleccionado;
    private List<Usuario> listaUsuariosOriginal = new ArrayList<>();

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
                Toast.makeText(getContext(), "Seleccione un usuario primero para eliminar", Toast.LENGTH_SHORT).show();
            }
        });
        btnCambiar.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                mostrarDialogoEditarUsuario();
            } else {
                Toast.makeText(getContext(), "Seleccione un usuario primero para modificar", Toast.LENGTH_SHORT).show();
            }
        });

        EditText buscador = view.findViewById(R.id.busqueda_usuario);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarUsuarios(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.tipos_usuario, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTipo.setAdapter(adapter);

        builder.setPositiveButton("Añadir", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String tipoTraducido = spinnerTipo.getSelectedItem().toString();
            String tipoOriginal = traducirTipoUsuarioInverso(tipoTraducido);

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!esEmailValido(email)) {
                Toast.makeText(getContext(), "Correo no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario nuevoUsuario = new Usuario(nombre, apellido, email, password, tipoOriginal);

            Utilidades.añadirUsuario(nuevoUsuario, new RespuestaCallback() {
                @Override
                public void onResultado(boolean exito) {
                    requireActivity().runOnUiThread(() -> {
                        if (exito) {
                            Toast.makeText(getContext(), "Usuario añadido con éxito", Toast.LENGTH_SHORT).show();
                            tabla.postDelayed(() -> cargarUsuariosExistentes(), 300);
                        }
                    });
                }
                @Override
                public void onFailure(boolean error) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show());
                }
            });
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }
    private void eliminarUsuarioSeleccionado() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de eliminar a " + usuarioSeleccionado.getFirst_name() + " " + usuarioSeleccionado.getLast_name() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    Utilidades.eliminarUsuario(usuarioSeleccionado, new RespuestaCallback() {
                        @Override
                        public void onResultado(boolean exito) {
                            requireActivity().runOnUiThread(() -> {
                                if (exito) {
                                    Toast.makeText(getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();

                                    usuarioSeleccionado = null;
                                    if (seleccionar_usuario != null) {
                                        seleccionar_usuario.setBackgroundResource(R.drawable.background_white_square);
                                        seleccionar_usuario = null;
                                    }
                                    cargarUsuariosExistentes();
                                }
                            });
                        }
                        @Override
                        public void onFailure(boolean error) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void mostrarDialogoEditarUsuario() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        final EditText etNuevaContrasena = new EditText(getContext());
        etNuevaContrasena.setHint("Nueva contraseña");
        etNuevaContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText etConfirmarContrasena = new EditText(getContext());
        etConfirmarContrasena.setHint("Confirmar contraseña");
        etConfirmarContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(etNuevaContrasena);
        layout.addView(etConfirmarContrasena);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cambiar contraseña de " + usuarioSeleccionado.getFirst_name());
        builder.setView(layout);

        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String nuevaContrasena = etNuevaContrasena.getText().toString().trim();
            String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();

            if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(getContext(), "Ambos campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nuevaContrasena.equals(confirmarContrasena)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuarioActualizado = new Usuario(
                    usuarioSeleccionado.getUser_id(),
                    nuevaContrasena);

            Utilidades.actualizarContra(usuarioActualizado, new UsuarioCallback() {
                @Override
                public void onResultado(String tipo) {
                    getActivity().runOnUiThread(() -> {});
                }

                @Override
                public void onFailure(boolean error) {
                    getActivity().runOnUiThread(() -> {});
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
                listaUsuariosOriginal = usuarios;
                tabla.removeAllViews();
                for (Usuario usuario : usuarios) {
                    anadir_usuario_item(usuario);}
            }
            @Override
            public void onFailure(boolean error) {
                Toast.makeText(getContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void anadir_usuario_item(Usuario usuario) {
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setElevation(4f);
        fila.setPadding(16, 16, 16, 16);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        GradientDrawable fondo = new GradientDrawable();
        fondo.setCornerRadius(24f);
        fondo.setStroke(2, ContextCompat.getColor(getContext(), R.color.gray_500));
        fondo.setColor(Color.WHITE);
        fila.setBackground(fondo);

        TextView avatar = new TextView(getContext());
        LinearLayout.LayoutParams lpAvatar = new LinearLayout.LayoutParams(100, 100);
        lpAvatar.setMargins(0, 0, 24, 0);
        avatar.setLayoutParams(lpAvatar);
        avatar.setGravity(Gravity.CENTER);
        avatar.setTextColor(Color.WHITE);
        avatar.setTextSize(18);
        avatar.setTypeface(Typeface.DEFAULT_BOLD);
        avatar.setBackgroundResource(R.drawable.bg_avatar);

        String iniciales = "";
        if (usuario.getFirst_name() != null && !usuario.getFirst_name().isEmpty()) {
            iniciales += usuario.getFirst_name().substring(0, 1).toUpperCase();
        }
        if (usuario.getLast_name() != null && !usuario.getLast_name().isEmpty()) {
            iniciales += usuario.getLast_name().substring(0, 1).toUpperCase();
        }
        avatar.setText(iniciales);

        LinearLayout contenedorTextos = new LinearLayout(getContext());
        contenedorTextos.setOrientation(LinearLayout.VERTICAL);
        contenedorTextos.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));

        TextView tvNombre = new TextView(getContext());
        tvNombre.setText(usuario.getFirst_name() + " " + usuario.getLast_name());
        tvNombre.setTextColor(getResources().getColor(R.color.black));
        tvNombre.setTextSize(15);
        tvNombre.setTypeface(Typeface.DEFAULT_BOLD);
        tvNombre.setSingleLine(true);
        tvNombre.setEllipsize(TextUtils.TruncateAt.END);

        TextView tvCorreoTipo = new TextView(getContext());
        tvCorreoTipo.setText(usuario.getEmail() + " • " + traducirTipoUsuario(usuario.getUser_type()));
        tvCorreoTipo.setTextColor(getResources().getColor(R.color.black));
        tvCorreoTipo.setTextSize(13);
        tvCorreoTipo.setSingleLine(true);
        tvCorreoTipo.setEllipsize(TextUtils.TruncateAt.END);

        TextView tvId = new TextView(getContext());
        tvId.setVisibility(View.GONE);
        tvId.setText(String.valueOf(usuario.getUser_id()));

        contenedorTextos.addView(tvNombre);
        contenedorTextos.addView(tvCorreoTipo);

        fila.addView(avatar);
        fila.addView(contenedorTextos);
        fila.addView(tvId);

        fila.setOnClickListener(v -> {
            if (seleccionar_usuario != null) {
                seleccionar_usuario.setBackground(fondo);}

            GradientDrawable fondoSel = new GradientDrawable();
            fondoSel.setColor(ContextCompat.getColor(getContext(), R.color.white));
            fondoSel.setCornerRadius(24f);
            fondoSel.setStroke(4, ContextCompat.getColor(getContext(), R.color.primary_300));

            v.setBackground(fondoSel);
            seleccionar_usuario = v;
            usuarioSeleccionado = usuario;
        });

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.animacion_entrada_item);
        fila.startAnimation(anim);

        tabla.addView(fila);
    }
    private void filtrarUsuarios(String filtro) {
        tabla.removeAllViews();
        for (Usuario usuario : listaUsuariosOriginal) {
            String nombreCompleto = (usuario.getFirst_name() + " " + usuario.getLast_name()).toLowerCase();
            if (nombreCompleto.contains(filtro.toLowerCase()) ||
                    usuario.getEmail().toLowerCase().contains(filtro.toLowerCase()) ||
                    traducirTipoUsuario(usuario.getUser_type()).toLowerCase().contains(filtro.toLowerCase())) {
                anadir_usuario_item(usuario);
            }
        }
    }
    private String traducirTipoUsuario(String tipo) {
        if (tipo == null) return "Desconocido";
        switch (tipo.toLowerCase()) {
            case "teacher": return "Profesor";
            case "student": return "Usuario";
            case "admin": return "Administrador";
            default: return tipo;
        }
    }
    private String traducirTipoUsuarioInverso(String tipoTraducido) {
        if (tipoTraducido == null) return "student";
        switch (tipoTraducido) {
            case "Profesor": return "teacher";
            case "Usuario": return "student";
            case "Administrador": return "admin";
            default: return "student";
        }
    }
    private boolean esEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}