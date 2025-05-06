package com.nickteck.inventariosanidad;

import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.nickteck.inventariosanidad.Profesor.Profesor;
import com.nickteck.inventariosanidad.Usuario.Usuario;


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
        boton_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                String usuario = nombre_usuario.getText().toString().trim();
                String con = contrasena.getText().toString().trim();

                if (usuario.equals("usuario") && con.equals("usuario")) {
                    Cambiarfragmento(new Usuario());
                } else if (usuario.equals("profesor") && con.equals("profesor")) {
                    Cambiarfragmento(new Profesor());
                } else if (usuario.equals("admin") && con.equals("admin")) {
                    Cambiarfragmento(new Administrador());
                } else {
                    mostrarerror(usuario, con);
                }
            }
        });
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(android.R.id.content, targetFragment)
                        .commit();
            }
        }, 2500);
    }

    /**
     * Muestra diferentes tipos de errores, dependiendo del campo en el cual consultemos, vibra en el caso que sea un error
     * @param usuario hace referencia al usuario
     * @param password hace referencia a la contraseña
     */
    private void mostrarerror(String usuario, String password) {
        if (usuario.isEmpty() && password.isEmpty()) {
            errortexto.setText("Introduce los datos");
        } else {
            if (!usuario.equals("usuario")) {
                errortexto.setText("Usuario Incorrecto");
            }
            if (!password.equals("usuario")) {
                errortexto.setText("Contraseña Incorrecta");
            }
        }

        tarjeta_error.setVisibility(View.VISIBLE);
        errorAnimation.playAnimation();

        if (vibracion != null && vibracion.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibracion.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibracion.vibrate(300);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tarjeta_error.setVisibility(View.GONE);
            }
        }, 1500);
    }



    //----------------------MODIFICAR SI ES NECESARIO---------------------------------

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
        ver_contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cambioimagenes();
            }
        });
    }

    /**
     * Mostrar o Ocultar contraseña
     */
    private void Cambioimagenes() {
        if (estado_contrasena) {
            contrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ver_contrasena.setImageResource(R.drawable.ojo_cerrado);
        } else {
            contrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ver_contrasena.setImageResource(R.drawable.ojo_abierto);
        }
        estado_contrasena = !estado_contrasena;
        contrasena.setSelection(contrasena.getText().length());
    }

}
