package com.nickteck.inventariosanidad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.os.Vibrator;
import android.content.Context;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.nickteck.inventariosanidad.Administrador.Administrador;
import com.nickteck.inventariosanidad.Profesor.Profesor;
import com.nickteck.inventariosanidad.Usuario.Inventariousu.Inventario;
import com.nickteck.inventariosanidad.Usuario.Usuario_Pantalla;
import com.nickteck.inventariosanidad.sampledata.Usuario;
import com.nickteck.inventariosanidad.sampledata.UsuarioCallback2;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

public class Login extends  Fragment{
    private EditText contrasena;
    private EditText nombre_usuario;
    private ImageView ver_contrasena;
    private Button boton_entrar;
    private CardView tarjeta_error;
    private Vibrator vibracion;
    private boolean estado_contrasena = false;
    private LottieAnimationView errorAnimation;
    private TextView texto_bienvenida;
    private TextView subtexto ;
    private TextView errortexto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        Asociarvaribles(view);

        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in); //Animacion de aparecer
        Animation bounce = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce); //Animacion de rebote del boton

        Boton_login(bounce);
        Animacionesentrada(fadeIn);
        BotonHabilitar();
        MostrarContra();

        return view;
    }

    /**
     * Configura el botón de login, validando los datos
     * @param bounce realiza las transicciones
     */
    private void Boton_login(Animation bounce) {
        boton_entrar.setOnClickListener(v -> {
            v.startAnimation(bounce);
            String correo = nombre_usuario.getText().toString().trim();
            String con    = contrasena.getText().toString().trim();

            Utilidades.verificarUsuario(new Usuario(correo, con), new UsuarioCallback2() {
                @Override
                public void onUsuarioObtenido(Usuario usuario) {

                    String tipo = usuario == null ? null : usuario.getUser_type();
                    SharedPreferences prefs2 = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    prefs2.edit().putString("tipo_usuario", usuario.getUser_type()).apply();



                    Log.e("usuarioinicio", "" + usuario.getUser_id());

                    SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    assert usuario != null;
                    editor.putString("nombreProfesor", usuario.getFirst_name());
                    editor.putInt("user_id", usuario.getUser_id());
                    editor.apply();



                    switch (tipo) {
                        case "admin":
                            Administrador adm = new Administrador();
                            Bundle a1 = new Bundle();
                            a1.putString("nombre", usuario.getFirst_name() + " " + usuario.getLast_name());
                            a1.putString("rol", tipo);
                            a1.putInt("id", usuario.getUser_id());
                            adm.setArguments(a1);
                            Cambiarfragmento(adm);
                            break;

                        case "student":
                            Usuario_Pantalla usp = new Usuario_Pantalla();
                            Bundle a2 = new Bundle();
                            a2.putString("nombre", usuario.getFirst_name() + " " + usuario.getLast_name());
                            a2.putString("rol", tipo);
                            a2.putInt("id", usuario.getUser_id());
                            usp.setArguments(a2);
                            Cambiarfragmento(usp);
                            break;

                        case "teacher":
                            Profesor prof = new Profesor();
                            Bundle a3 = new Bundle();
                            a3.putString("nombre", usuario.getFirst_name() + " " + usuario.getLast_name());
                            a3.putString("rol", tipo);
                            a3.putInt("id", usuario.getUser_id());
                            prof.setArguments(a3);
                            Cambiarfragmento(prof);
                            break;
                    }
                }

                @Override
                public void onFailure(boolean error) {}
            }, this::mostrarerror);
        });
    }

    /**
     * Muestra el error si el usuario o contraseña estan incorrectos atravez de una tarjeta
     */
    private void mostrarerror(int resId) {
        errortexto.setText(resId);
        tarjeta_error.setVisibility(View.VISIBLE);
        errorAnimation.playAnimation();

        if (vibracion != null && vibracion.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibracion.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibracion.vibrate(300);
            }
        }

        new Handler().postDelayed(() -> tarjeta_error.setVisibility(View.GONE), 1500);
    }

    /**
     * Metodo para pasar al fragmento correspondiente despues de la validacion
     * @param targetFragment Para hacer la transiccion al fragmento correspondiente
     */
    private void Cambiarfragmento(Fragment targetFragment) {
        boton_entrar.setText("✓");
        boton_entrar.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.green)
        );

        new Handler().postDelayed(() -> {
            if (isAdded()) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(android.R.id.content, targetFragment)
                        .commit();
            }
        }, 2800);
    }

    /**
     * Metodo que inicia las variables del layout
     * @param view permite iniciar
     */
    private void Asociarvaribles(View view) {
        contrasena = view.findViewById(R.id.casillacontrasena);
        nombre_usuario = view.findViewById(R.id.casillausuario);
        ver_contrasena = view.findViewById(R.id.vistacontrena);
        boton_entrar = view.findViewById(R.id.botonlogin);
        vibracion = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        texto_bienvenida = view.findViewById(R.id.textobienvenida);
        subtexto = view.findViewById(R.id.textodeinicio);
        errortexto = view.findViewById(R.id.textoerror);
        tarjeta_error = view.findViewById(R.id.tarjetaerror);
        errorAnimation = view.findViewById(R.id.animacionerror);
    }

    /**
     * Metodo que inicia las animaciones de cada texto
     * @param fadeIn animacion de entrada
     */
    private void Animacionesentrada(final Animation fadeIn) {
        //Texto bienvenida
        new Handler().postDelayed(() -> {
            texto_bienvenida.setAlpha(1f);
            texto_bienvenida.startAnimation(fadeIn);
        }, 500);
        //Subtexto
        new Handler().postDelayed(() -> {
            subtexto.setAlpha(1f);
            subtexto.startAnimation(fadeIn);
        }, 700);
        //Nombre de usuario
        new Handler().postDelayed(() -> {
            nombre_usuario.setAlpha(1f);
            nombre_usuario.startAnimation(fadeIn);
        }, 900);
        //Contraseña
        new Handler().postDelayed(() -> {
            contrasena.setAlpha(1f);
            contrasena.startAnimation(fadeIn);
        }, 1100);
        //Boton de entrar
        new Handler().postDelayed(() -> {
            boton_entrar.setAlpha(1f);
            boton_entrar.startAnimation(fadeIn);
        }, 1300);
    }

    /**
     * Metodo que Habilita o deshabilita el botón según campos vacíos
     */
    private void BotonHabilitar() {
        boton_entrar.setEnabled(false);
        boton_entrar.setAlpha(0.5f);

        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean camposLlenos = !nombre_usuario.getText().toString().trim().isEmpty() && !contrasena.getText().toString().trim().isEmpty();
                boton_entrar.setEnabled(camposLlenos);
                boton_entrar.setAlpha(camposLlenos ? 1f : 0.5f);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        nombre_usuario.addTextChangedListener(formWatcher);
        contrasena.addTextChangedListener(formWatcher);
    }

    /**
     * Alterna la visibilidad de la contraseña y actualiza el icono correspondiente hace llamada
     */
    private void MostrarContra() {
        ver_contrasena.setOnClickListener(v -> Cambioimagenes());
    }

    /**
     * Mostrar o Ocultar contraseña
     */
    private void Cambioimagenes() {
        if (estado_contrasena) {
            contrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ver_contrasena.setImageResource(R.drawable.ojo_c);
        } else {
            contrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ver_contrasena.setImageResource(R.drawable.ojo_a);
        }
        estado_contrasena = !estado_contrasena;
        contrasena.setSelection(contrasena.getText().length());
    }

}
