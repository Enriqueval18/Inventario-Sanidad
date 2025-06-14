package com.nickteck.inventariosanidad.Profesor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nickteck.inventariosanidad.R;

public class Actividades_profesor extends Fragment {
    private EditText Cbusqueda;
    private Button botonan;
    private LinearLayout contenedorgeneral;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_actividades_profesor, container, false);
        Cbusqueda = view.findViewById(R.id.cuadrobusqueda);

        Cbusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //contenedorgeneral = view.findViewById(R.id.contenedorgeneral);
        return view;
    }

    /**
     * Filtra el resultado
     * @param header donde busca
     * @param content que busca
     * @param query referencia
     */
    private void filtrarhijos(LinearLayout header, LinearLayout content, String query) {
        if (header.getChildCount() > 0 && header.getChildAt(0) instanceof TextView) {
            String headerText = ((TextView) header.getChildAt(0)).getText().toString().toLowerCase();
            if (query.isEmpty() || headerText.contains(query)) {
                header.setVisibility(View.VISIBLE);
                content.setVisibility(View.VISIBLE);
            } else {
                header.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Crea y añade dinámicamente una nueva sección tipo accordion a partir de los parámetros.
     * Se espera que 'materiales' sea una cadena separada por comas.
     */
    private void ananirnuevarama(final String sectionName, String materiales, final String cantidad) {
        LinearLayout newSectionContainer = new LinearLayout(getContext());
        newSectionContainer.setOrientation(LinearLayout.VERTICAL);
        newSectionContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newSectionContainer.setPadding(0, 0, 0, 16); // margen inferior

        final LinearLayout headerLayout = new LinearLayout(getContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headerLayout.setPadding(16, 16, 16, 16);
        headerLayout.setBackgroundColor(getResources().getColor(R.color.blueblack));

        TextView headerTitle = new TextView(getContext());
        headerTitle.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        headerTitle.setText(sectionName);
        headerTitle.setTextSize(16);
        headerTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        headerTitle.setTextColor(getResources().getColor(R.color.white));

        final ImageView arrowIcon = new ImageView(getContext());
        arrowIcon.setImageResource(android.R.drawable.arrow_down_float);
        arrowIcon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        arrowIcon.setColorFilter(getResources().getColor(R.color.white));

        headerLayout.addView(headerTitle);
        headerLayout.addView(arrowIcon);

        final LinearLayout contentLayout = new LinearLayout(getContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentLayout.setPadding(16, 16, 16, 16);
        contentLayout.setBackgroundColor(getResources().getColor(R.color.white));
        contentLayout.setVisibility(View.VISIBLE);

        String[] materialArray = materiales.split(",");
        for (String material : materialArray) {
            material = material.trim();

            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            itemLayout.setPadding(0, 5, 0, 5);

            TextView tvName = new TextView(getContext());
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(material);
            tvName.setTextColor(getResources().getColor(R.color.black));

            TextView tvQuantity = new TextView(getContext());
            tvQuantity.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvQuantity.setText(cantidad);
            tvQuantity.setTextColor(getResources().getColor(R.color.black));
            tvQuantity.setPadding(8, 0, 0, 0);

            itemLayout.addView(tvName);
            itemLayout.addView(tvQuantity);

            contentLayout.addView(itemLayout);
        }

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentLayout.getVisibility() == View.VISIBLE) {
                    contentLayout.setVisibility(View.GONE);
                    arrowIcon.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    contentLayout.setVisibility(View.VISIBLE);
                    arrowIcon.setImageResource(android.R.drawable.arrow_up_float);
                }
                contentLayout.requestLayout();
                contentLayout.invalidate();
            }
        });

        newSectionContainer.addView(headerLayout);
        newSectionContainer.addView(contentLayout);
        contenedorgeneral.addView(newSectionContainer);
    }


}
