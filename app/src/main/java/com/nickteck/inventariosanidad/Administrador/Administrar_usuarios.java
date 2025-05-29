package com.nickteck.inventariosanidad.Administrador;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.Usuario;
import com.nickteck.inventariosanidad.sampledata.UsuarioCallback2;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

public class Administrar_usuarios extends Fragment {
    private Button ananirusuario, borrarusuario, modificarusu;
    private LinearLayout tabla;
    private View seleccionar_usuario = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_usuarios, container, false);

        ananirusuario = view.findViewById(R.id.btnanausua);
        borrarusuario = view.findViewById(R.id.btneliminarusu);
        modificarusu = view.findViewById(R.id.btnmodiusu);
        tabla = view.findViewById(R.id.tabla_usuarios);

        ananirusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = inflater.inflate(R.layout.dialogo_anadir_user, container, false);
                final EditText etUsername = dialogView.findViewById(R.id.Nombreusuario);
                final EditText etPassword = dialogView.findViewById(R.id.Contrasena);
                final Spinner spinnerRole = dialogView.findViewById(R.id.Seleccionarrol);

                String[] roles = {"Profesor", "Administrador", "Usuario"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, roles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRole.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Añadir Usuario")
                        .setView(dialogView)
                        .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String username = etUsername.getText().toString().trim();
                                String password = etPassword.getText().toString().trim();
                                String role = spinnerRole.getSelectedItem().toString();

                                if (!username.isEmpty() && !password.isEmpty() && !role.isEmpty()) {

                                    if(role.equals("Profesor")){
                                        role = "teacher";
                                    } else if (role.equals("Administrador")) {
                                        role = "admin";
                                    }
                                    else{
                                        role = "student";
                                    }

                                    String finalRole = role;
                                    Utilidades.añadirUsuario(new Usuario(username, password, role), new RespuestaCallback() {
                                        @Override
                                        public void onResultado(boolean correcto) {
                                            if(correcto){
                                                ananir_usuario_item(username, finalRole);
                                            }
                                        }

                                        @Override
                                        public void onFailure(boolean error) {

                                        }
                                    });


                                } else {
                                    Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        borrarusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seleccionar_usuario != null) {
                    Log.e("Eliminar","antes de seleccionar el view");
                    // Asegurarse de que el 'seleccionar_usuario' es un TextView
                    TextView textViewUsuario = (TextView) seleccionar_usuario;

                    Log.e("Eliminar","luego de seleccionar");
                    // Extraer el texto del TextView seleccionado
                    String usuarioSeleccionado = textViewUsuario.getText().toString();

                    // Obtener solo el nombre del usuario. Aquí asumimos que el nombre está antes del " - "
                    String[] partes = usuarioSeleccionado.split(" - ");
                    String nombreUsuario = partes[0]; // El nombre del usuario es la primera parte

                    Log.e("Eliminar",nombreUsuario);
                    // Llamar a la función para eliminar el usuario de la base de datos
                    Utilidades.eliminarUsuario(new Usuario(nombreUsuario), new RespuestaCallback() {
                        @Override
                        public void onResultado(boolean correcto) {
                            if(correcto){
                                // Eliminar el usuario de la interfaz (Vista)
                                tabla.removeView(seleccionar_usuario);
                                seleccionar_usuario = null; // Resetear la selección
                                // Mostrar un mensaje de confirmación
                                Toast.makeText(getContext(), "Usuario eliminado: " + nombreUsuario, Toast.LENGTH_SHORT).show();
                            }
                            }

                        @Override
                        public void onFailure(boolean error) {

                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Selecciona un usuario para quitar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        modificarusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seleccionar_usuario == null) {
                    Toast.makeText(getContext(), "Selecciona un usuario para modificar", Toast.LENGTH_SHORT).show();
                    return;
                }

                View dialogView = inflater.inflate(R.layout.dialogo_anadir_user, container, false);
                final EditText etUsername = dialogView.findViewById(R.id.Nombreusuario);
                final EditText etPassword = dialogView.findViewById(R.id.Contrasena);
                final Spinner spinnerRole = dialogView.findViewById(R.id.Seleccionarrol);
                TextView textViewUsuario = (TextView) seleccionar_usuario;
                String usuarioSeleccionado = textViewUsuario.getText().toString();

                // Obtener solo el nombre del usuario. Aquí asumimos que el nombre está antes del " - "
                String[] partes = usuarioSeleccionado.split(" - ");
                String nombreUsuario = partes[0]; // El nombre del usuario es la primera parte

                String[] roles = {"Profesor", "Administrador", "Usuario"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, roles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRole.setAdapter(adapter);

                String[] parts = ((TextView) seleccionar_usuario).getText().toString().split(" - ");
                if (parts.length >= 2) {
                    etUsername.setText(parts[0].trim());

                    String currentRole = parts[1].trim();
                    for (int i = 0; i < roles.length; i++) {
                        if (roles[i].equalsIgnoreCase(currentRole)) {
                            spinnerRole.setSelection(i);
                            break;
                        }
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Modificar Usuario")
                        .setView(dialogView)
                        .setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newUsername = etUsername.getText().toString().trim();
                                String newPassword = etPassword.getText().toString().trim();
                                String newRole = spinnerRole.getSelectedItem().toString();

                                if (!newUsername.isEmpty() && !newRole.isEmpty()) {

                                    Utilidades.editarUsuarios(new Usuario(newUsername, newPassword, newRole), nombreUsuario, new RespuestaCallback() {
                                        @Override
                                        public void onResultado(boolean correcto) {
                                            Log.i("EditarUsuario","usuario editado");
                                        }

                                        @Override
                                        public void onFailure(boolean error) {

                                        }
                                    });
                                    ((TextView) seleccionar_usuario).setText(newUsername + " - " + newRole);
                                    Toast.makeText(getContext(), "Usuario modificado", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        cargarUsuariosExistentes();

        return view;
    }
    private void cargarUsuariosExistentes() {
        Utilidades.mostrarUsuarios(new UsuarioCallback2() {
            @Override
            public void onUsuarioObtenido(Usuario usuario) {
                Log.d("Usuarios", "correo a mostrar en pantalla "+usuario.getEmail());

                ananir_usuario_item(usuario.getFirst_name(), usuario.getUser_type());
            }

            @Override
            public void onFailure(boolean error) {
                Log.d("Usuarios", "error obtener usuarios");

            }
        }); // Este método debes tenerlo definido

    }

    /**
     * Agrega un nuevo ítem de usuario al contenedor 'user_list_container'.
     * Cada ítem se crea como un TextView y se le asigna un OnClickListener para permitir su selección.
     * @param username Nombre del usuario.
     * @param role     Rol asignado al usuario.
     */
    private void ananir_usuario_item(String username, String role) {
        // 1. Contenedor de la fila
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpFila =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lpFila.setMargins(0, 8, 0, 8);
        fila.setLayoutParams(lpFila);
        fila.setBackgroundResource(R.drawable.background_white_square);

        // 2. Celda Nombre
        TextView tvNombre = new TextView(getContext());
        LinearLayout.LayoutParams lpCelda =
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvNombre.setLayoutParams(lpCelda);
        tvNombre.setPadding(16, 16, 16, 16);
        tvNombre.setText(username);

        // 3. Celda Rol
        TextView tvRol = new TextView(getContext());
        tvRol.setLayoutParams(lpCelda);
        tvRol.setPadding(16, 16, 16, 16);
        tvRol.setText(role);

        // 4. Click / selección sobre toda la fila
        fila.setOnClickListener(v -> {
            if (seleccionar_usuario != null) {
                seleccionar_usuario.setBackgroundResource(
                        R.drawable.background_white_square);
            }
            if (seleccionar_usuario == v) {
                seleccionar_usuario = null;
            } else {
                v.setBackgroundResource(R.drawable.rounded_indicator);
                seleccionar_usuario = v;
            }
        });

        // 5. Añade las dos celdas y la fila al ScrollView
        fila.addView(tvNombre);
        fila.addView(tvRol);
        tabla.addView(fila);
    }


}
