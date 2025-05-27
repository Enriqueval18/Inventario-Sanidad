package com.nickteck.inventariosanidad.Usuario;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nickteck.inventariosanidad.R;

public class MostrarInventario extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.PantallaCompleta);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.propiedades_tabla_inventario, container, false);

        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvUnidades = view.findViewById(R.id.tvUnidades);
        TextView tvAlmacen = view.findViewById(R.id.tvAlmacen);
        TextView tvArmarios = view.findViewById(R.id.tvArmarios);
        TextView tvEstante = view.findViewById(R.id.tvEstante);
        TextView tvUnidadesMinimas = view.findViewById(R.id.tvUnidadesMinimas);
        TextView tvDescripcion = view.findViewById(R.id.tvDescripcion);

        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre", "");
            String descripcion = args.getString("descripcion", "");
            String almacen = args.getString("almacen", "");
            String armario = args.getString("armario", "");
            String balda = args.getString("estante", "");
            int unidades = args.getInt("unidades", 0);
            int unidadesMin = args.getInt("unidadesm", 0);

            // Usamos el método auxiliar para cada campo con su label en negrita
            tvNombre.setText(createBoldLabel("Nombre: ", nombre));
            tvUnidades.setText(createBoldLabel("Unidades: ", String.valueOf(unidades)));
            tvAlmacen.setText(createBoldLabel("Almacén: ", almacen));
            tvArmarios.setText(createBoldLabel("Armario: ", armario));
            tvEstante.setText(createBoldLabel("Estante: ", balda));
            tvUnidadesMinimas.setText(createBoldLabel("Unidades mínimas: ", String.valueOf(unidadesMin)));
            tvDescripcion.setText(createBoldLabel("Descripción: ", descripcion));
        }

        return view;
    }

    private SpannableStringBuilder createBoldLabel(String label, String value) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(label);
        builder.setSpan(new StyleSpan(Typeface.BOLD),
                0, label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(value);
        return builder;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
        }
    }
}


