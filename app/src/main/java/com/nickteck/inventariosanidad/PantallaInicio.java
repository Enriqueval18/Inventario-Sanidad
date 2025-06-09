package com.nickteck.inventariosanidad;

import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class PantallaInicio extends AppCompatActivity {

    /**
     * Esta asociado a su Xml en donde despues de pasar 3.5 segundos pasa al siguiente fragmento
     * que es el Login, este mismo contiene animaciones de entrada y salida
     * @param savedInstanceState  Sirve para llamar a una actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_inicio);
        new Handler().postDelayed(() -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);  //Animacion a la hora de pasar al siguiente Fragmento
            transaction.replace(android.R.id.content, new Login());
            transaction.commit();
        }, 3500);
    }
}
