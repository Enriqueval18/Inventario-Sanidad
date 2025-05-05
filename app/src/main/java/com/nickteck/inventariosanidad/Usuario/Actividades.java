package com.nickteck.inventariosanidad.Usuario;

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

public class Actividades extends Fragment {
    private EditText cuadro_busqueda;
    private Button btnananir;
    private LinearLayout sectionsContainer;
    private LinearLayout LimpiezaCabecera, LimpiezaContenido;
    private ImageView FlechaLimpieza;
    private LinearLayout ComprasCabecera, ComprasContenido;
    private ImageView ComprasFlecha;
    private LinearLayout Mantenimientoca, Manteniminetocont;
    private ImageView Mantenimientofle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);

        cuadro_busqueda = view.findViewById(R.id.cuadrobus);

        cuadro_busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase().trim();
                Filtrar(LimpiezaContenido, query);
                Filtrar(ComprasContenido, query);
                Filtrar(Manteniminetocont, query);
            }
        });

        btnananir = view.findViewById(R.id.btnana);
        btnananir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View cuadroañadir = LayoutInflater.from(getContext()).inflate(R.layout.anadir_item, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Añadir Nuevo Item");
                builder.setView(cuadroañadir)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etNombre = cuadroañadir.findViewById(R.id.etNombre);
                                EditText etMateriales = cuadroañadir.findViewById(R.id.etMateriales);
                                EditText etCantidad = cuadroañadir.findViewById(R.id.etCantidad);
                                String nombre = etNombre.getText().toString().trim();
                                String materiales = etMateriales.getText().toString().trim();
                                String cantidad = etCantidad.getText().toString().trim();
                                if (!nombre.isEmpty() && !materiales.isEmpty() && !cantidad.isEmpty()) {
                                    addNewSection(nombre, materiales, cantidad);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        sectionsContainer = view.findViewById(R.id.Contenedor_actividades);

        LimpiezaCabecera = view.findViewById(R.id.CabLimp);
        LimpiezaContenido = view.findViewById(R.id.Contlim);
        FlechaLimpieza = view.findViewById(R.id.fleca);

        LimpiezaCabecera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LimpiezaContenido.getVisibility() == View.VISIBLE) {
                    LimpiezaContenido.setVisibility(View.GONE);
                    FlechaLimpieza.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    LimpiezaContenido.setVisibility(View.VISIBLE);
                    FlechaLimpieza.setImageResource(android.R.drawable.arrow_up_float);
                }
                LimpiezaContenido.requestLayout();
                LimpiezaContenido.invalidate();
            }
        });


        ComprasCabecera = view.findViewById(R.id.Comprasca);
        ComprasContenido = view.findViewById(R.id.Comprascon);
        ComprasFlecha = view.findViewById(R.id.Comprasfle);

        ComprasCabecera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ComprasContenido.getVisibility() == View.VISIBLE) {
                    ComprasContenido.setVisibility(View.GONE);
                    ComprasFlecha.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    ComprasContenido.setVisibility(View.VISIBLE);
                    ComprasFlecha.setImageResource(android.R.drawable.arrow_up_float);
                }
                ComprasContenido.requestLayout();
                ComprasContenido.invalidate();
            }
        });

        Mantenimientoca = view.findViewById(R.id.Manteca);
        Manteniminetocont = view.findViewById(R.id.Mancon);
        Mantenimientofle = view.findViewById(R.id.Manfle);

        Mantenimientoca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Manteniminetocont.getVisibility() == View.VISIBLE) {
                    Manteniminetocont.setVisibility(View.GONE);
                    Mantenimientofle.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    Manteniminetocont.setVisibility(View.VISIBLE);
                    Mantenimientofle.setImageResource(android.R.drawable.arrow_up_float);
                }
                Manteniminetocont.requestLayout();
                Manteniminetocont.invalidate();
            }
        });


        return view;
    }

    /**
     * Recorre cada hijo de la sección y muestra sólo aquellos cuyo texto contiene la consulta.
     * Si query está vacío, se muestran todos los ítems.
     */
    private void Filtrar(LinearLayout section, String query) {
        int count = section.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = section.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout itemLayout = (LinearLayout) child;
                if (itemLayout.getChildCount() > 0 && itemLayout.getChildAt(0) instanceof TextView) {
                    TextView tvItem = (TextView) itemLayout.getChildAt(0);
                    String itemText = tvItem.getText().toString().toLowerCase();
                    if (query.isEmpty() || itemText.contains(query)) {
                        itemLayout.setVisibility(View.VISIBLE);
                    } else {
                        itemLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    /**
     * Crea y añade dinámicamente una nueva sección tipo accordion a partir de los parámetros.
     * Se espera que 'materiales' sea una cadena separada por comas.
     */
    private void addNewSection(final String sectionName, String materiales, final String cantidad) {

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

            // TextView para la cantidad (utilizando el valor capturado en el diálogo)
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

        sectionsContainer.addView(newSectionContainer);
    }


}
