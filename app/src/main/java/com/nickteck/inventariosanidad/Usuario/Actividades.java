package com.nickteck.inventariosanidad.Usuario;

import static com.nickteck.inventariosanidad.sampledata.Utilidades.validarMaterial;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;

import java.util.ArrayList;

public class Actividades extends Fragment {
    private EditText cuadro_busqueda;
    private ImageView btnananir;
    private LinearLayout sectionsContainer;
    final LinearLayout[] selectedSection = new LinearLayout[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_actividades, container, false);
        //-----------------Cuadro de busqueda-------------------------------------
        cuadro_busqueda = view.findViewById(R.id.cuadrobus);
        cuadro_busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString();
                int totalSecciones = sectionsContainer.getChildCount(); //Busca entre las secciones
                for (int i = 0; i < totalSecciones; i++) {
                    View seccion = sectionsContainer.getChildAt(i);
                    if (seccion instanceof LinearLayout) {
                        LinearLayout seccionLayout = (LinearLayout) seccion;
                        if (seccionLayout.getChildCount() >= 2) {
                            LinearLayout header = (LinearLayout) seccionLayout.getChildAt(0);
                            LinearLayout content = (LinearLayout) seccionLayout.getChildAt(1);
                            Filtrar(header, content, texto);
                        }
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //-----------------Boton de añadir----------------------------------------
        btnananir = view.findViewById(R.id.btnana);
        btnananir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View cuadroañadir = LayoutInflater.from(getContext()).inflate(R.layout.anadir_item_cabecera, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Añadir Nueva Peticion");
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
                                    if (!nombre.matches("^[^-]+\\s*-\\s*[^-]+$")) {  //Hace que el formato sea: Material - Nombre del alumno
                                        Toast.makeText(getContext(), "Formato de Nombre incorrecto", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    validarMaterial(getContext(), materiales, new MaterialCallback() {
                                        @Override
                                        public void onMaterialObtenido(Material material) {
                                            anandir_nueva_tabla(nombre, materiales, cantidad);
                                        }
                                        @Override
                                        public void onFailure(boolean error) { }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        //-----------------Boton de borrar---------------------------------------
        ImageButton btnDeleteSection = view.findViewById(R.id.btnDelete);
        btnDeleteSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSection[0] != null) {
                    sectionsContainer.removeView(selectedSection[0]);
                    selectedSection[0] = null;
                } else {
                    Toast.makeText(getContext(), "No se ha seleccionado ninguna tabla", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //-----------------Boton de enviar-------------------------------------
        ImageButton btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSection[0] != null) {
                    LinearLayout section = selectedSection[0];
                    LinearLayout headerLayout = (LinearLayout) section.getChildAt(0);
                    TextView headerTitle = (TextView) headerLayout.getChildAt(0);
                    String titulo = headerTitle.getText().toString();
                    LinearLayout contentLayout = (LinearLayout) section.getChildAt(1);
                    int contentChildCount = contentLayout.getChildCount();

                    ArrayList<String> materiales = new ArrayList<>();
                    ArrayList<String> cantidades = new ArrayList<>();

                    if (contentChildCount > 2) {
                        for (int j = 1; j < contentChildCount - 1; j++) {
                            View rowView = contentLayout.getChildAt(j);
                            if (rowView instanceof LinearLayout) {
                                LinearLayout row = (LinearLayout) rowView;
                                if (row.getChildCount() >= 2) {
                                    TextView tvMaterial = (TextView) row.getChildAt(0);
                                    TextView tvCantidad = (TextView) row.getChildAt(1);
                                    materiales.add(tvMaterial.getText().toString());
                                    cantidades.add(tvCantidad.getText().toString());
                                }
                            }
                        }
                    }

                    Toast.makeText(getContext(), "Datos enviados de la tabla: " + titulo, Toast.LENGTH_LONG).show();
                    headerLayout.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    Toast.makeText(getContext(), "No se ha seleccionado ninguna tabla", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sectionsContainer = view.findViewById(R.id.Contenedor_actividades);
        return view;
    }

    /**
     * Recorre cada hijo de la sección y muestra sólo aquellos cuyo texto contiene la consulta.
     * Si query está vacío, se muestran todos los ítems.
     */
    private void Filtrar(LinearLayout header, LinearLayout content, String query) {
        String filtro = query.toLowerCase().trim();
        if (header.getChildCount() > 0 && header.getChildAt(0) instanceof TextView) {
            String headerText = ((TextView) header.getChildAt(0)).getText().toString().toLowerCase();
            if (filtro.isEmpty() || headerText.contains(filtro)) {
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
    private void anandir_nueva_tabla(final String sectionName, String materiales, final String cantidades) {

        //------------------Contenedor principal de la sección------------------------------
        LinearLayout newSectionContainer = new LinearLayout(getContext());
        newSectionContainer.setOrientation(LinearLayout.VERTICAL);
        newSectionContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newSectionContainer.setPadding(0, 0, 0, 16);


        //-----------------Parte azul de arriba en donde esta-------------------------------
        final LinearLayout headerLayout = new LinearLayout(getContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headerLayout.setPadding(16, 16, 16, 16);
        headerLayout.setBackgroundColor(getResources().getColor(R.color.blueblack));

        //------------------------Título del encabezado-------------------------------------
        TextView headerTitle = new TextView(getContext());
        LinearLayout.LayoutParams headerTitleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        headerTitle.setLayoutParams(headerTitleParams);

        if (sectionName != null && !sectionName.isEmpty()) {
            String formattedName = capitalizeWords(sectionName);
            headerTitle.setText(formattedName);
        } else {
            headerTitle.setText("");
        }

        headerTitle.setTextSize(18);
        headerTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        headerTitle.setTextColor(getResources().getColor(R.color.white));


        //------------------------Flecha para mostrar/ocultar contenido-------------------------------------
        final ImageView arrowIcon = new ImageView(getContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        iconParams.gravity = Gravity.CENTER_VERTICAL;
        arrowIcon.setLayoutParams(iconParams);
        arrowIcon.setImageResource(android.R.drawable.arrow_down_float);
        arrowIcon.setColorFilter(getResources().getColor(R.color.white));
        headerLayout.addView(headerTitle);
        headerLayout.addView(arrowIcon);


        //-----------------------Hace que se puede seleccionar la tabla-------------------------------------
        newSectionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSection[0] != null) {
                    selectedSection[0].setBackgroundColor(Color.TRANSPARENT);
                }
                selectedSection[0] = newSectionContainer;
                newSectionContainer.setBackgroundColor(getResources().getColor(R.color.blueblack));
            }
        });


        //------------------------------Contenido------------------------------------------------------
        final LinearLayout contentLayout = new LinearLayout(getContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentLayout.setPadding(16, 16, 16, 16);
        contentLayout.setBackgroundColor(getResources().getColor(R.color.white));
        contentLayout.setVisibility(View.VISIBLE);


        //------------------------Agregar nombre y cantidad a la fila----------------------------------
        LinearLayout headerRow = new LinearLayout(getContext());
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headerRow.setPadding(0, 0, 0, 8);
        headerRow.setBackgroundColor(getResources().getColor(R.color.white));



        //------------------------Nombre----------------------------------
        TextView tvHeaderNombre = new TextView(getContext());
        LinearLayout.LayoutParams headerParams1 = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvHeaderNombre.setLayoutParams(headerParams1);
        tvHeaderNombre.setText(R.string.nombre_del_material);
        tvHeaderNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        tvHeaderNombre.setTextColor(getResources().getColor(R.color.black));

        //------------------------Cantidad----------------------------------
        TextView tvHeaderCantidad = new TextView(getContext());
        LinearLayout.LayoutParams headerParams2 = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvHeaderCantidad.setLayoutParams(headerParams2);
        tvHeaderCantidad.setText(R.string.cantidad);
        tvHeaderCantidad.setTypeface(null, android.graphics.Typeface.BOLD);
        tvHeaderCantidad.setTextColor(getResources().getColor(R.color.black));
        tvHeaderCantidad.setGravity(Gravity.END);

        headerRow.addView(tvHeaderNombre);
        headerRow.addView(tvHeaderCantidad);
        contentLayout.addView(headerRow);


        //----------------------Ingresar Datos-------------------------------------------------------------
        String[] materialArray = materiales.split(",");
        String[] cantidadArray = cantidades.split(",");

        for (int i = 0; i < materialArray.length; i++) {
            String material = materialArray[i].trim();
            String cantidadItem = (i < cantidadArray.length) ? cantidadArray[i].trim() : "";
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            itemLayout.setPadding(0, 8, 0, 8);

            TextView tvName = new TextView(getContext());
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(material);
            tvName.setTextColor(getResources().getColor(R.color.black));

            TextView tvQuantity = new TextView(getContext());
            LinearLayout.LayoutParams quantityParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvQuantity.setLayoutParams(quantityParams);
            tvQuantity.setText(cantidadItem);
            tvQuantity.setTextColor(getResources().getColor(R.color.black));
            tvQuantity.setGravity(Gravity.END);


            itemLayout.addView(tvName);
            itemLayout.addView(tvQuantity);
            contentLayout.addView(itemLayout);
        }


        //-------------------------Boton de añadir y borrar dentro de la misma tabla----------------------------------

        final LinearLayout[] selectedRow = new LinearLayout[1];

        final LinearLayout addMaterialIconContainer = new LinearLayout(getContext());
        addMaterialIconContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.gravity = Gravity.CENTER_HORIZONTAL;
        addMaterialIconContainer.setLayoutParams(containerParams);

        //------------------- Ícono '+' para agregar un nuevo material-------------
        final ImageView addMaterialIcon = new ImageView(getContext());
        LinearLayout.LayoutParams addIconParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        addIconParams.gravity = Gravity.CENTER_VERTICAL;
        addMaterialIcon.setLayoutParams(addIconParams);
        addMaterialIcon.setImageResource(android.R.drawable.ic_menu_add);
        addMaterialIcon.setColorFilter(getResources().getColor(R.color.blueblack));

        //------------Al pulsar el '+' se abre un diálogo para ingresar el nuevo material y cantidad. --------------
        addMaterialIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View cuadroAnadir = LayoutInflater.from(getContext()).inflate(R.layout.anadir_item_material, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Añadir Nuevo Material");
                builder.setView(cuadroAnadir)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etMaterial = cuadroAnadir.findViewById(R.id.item_Mat);
                                EditText etCantidad = cuadroAnadir.findViewById(R.id.item_Cant);
                                final String material = etMaterial.getText().toString().trim();
                                final String cantidad = etCantidad.getText().toString().trim();

                                if (!material.isEmpty() && !cantidad.isEmpty()) {
                                    validarMaterial(getContext(), material, new MaterialCallback() {
                                        @Override
                                        public void onMaterialObtenido(Material materialValidado) {
                                            boolean existe = false;
                                            int childCount = contentLayout.getChildCount();
                                            for (int i = 0; i < childCount; i++) {
                                                View child = contentLayout.getChildAt(i);
                                                if (child instanceof LinearLayout && child != addMaterialIconContainer) {
                                                    LinearLayout fila = (LinearLayout) child;
                                                    if (fila.getChildCount() > 0 && fila.getChildAt(0) instanceof TextView) {
                                                        String nombreExistente = ((TextView) fila.getChildAt(0)).getText().toString().trim();
                                                        if (nombreExistente.equalsIgnoreCase(material)) {
                                                            existe = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            if (existe) {
                                                Toast.makeText(getContext(), "El material ya ha sido añadido", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            final LinearLayout newRow = new LinearLayout(getContext());
                                            newRow.setOrientation(LinearLayout.HORIZONTAL);
                                            newRow.setLayoutParams(new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            newRow.setPadding(0, 8, 0, 8);

                                            TextView tvNewMaterial = new TextView(getContext());
                                            LinearLayout.LayoutParams newNameParams = new LinearLayout.LayoutParams(
                                                    0,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                                            tvNewMaterial.setLayoutParams(newNameParams);
                                            tvNewMaterial.setText(material);
                                            tvNewMaterial.setTextColor(getResources().getColor(R.color.black));

                                            TextView tvNewCantidad = new TextView(getContext());
                                            LinearLayout.LayoutParams newQuantityParams = new LinearLayout.LayoutParams(
                                                    0,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                                            tvNewCantidad.setLayoutParams(newQuantityParams);
                                            tvNewCantidad.setText(cantidad);
                                            tvNewCantidad.setTextColor(getResources().getColor(R.color.black));
                                            tvNewCantidad.setGravity(Gravity.END);

                                            newRow.addView(tvNewMaterial);
                                            newRow.addView(tvNewCantidad);

                                            int addIconIndex = contentLayout.indexOfChild(addMaterialIconContainer);
                                            if (addIconIndex != -1) {
                                                contentLayout.addView(newRow, addIconIndex);
                                            } else {
                                                contentLayout.addView(newRow);
                                            }
                                            newRow.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (selectedRow[0] != null) {
                                                        selectedRow[0].setBackgroundColor(Color.TRANSPARENT);
                                                    }
                                                    selectedRow[0] = newRow;
                                                    newRow.setBackgroundColor(getResources().getColor(R.color.lijero));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(boolean error) {
                                            Toast.makeText(getContext(), "El material ingresado no existe", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        //------------------------------- Icono de basura para borrar el item seleccionado-----------------------
        final ImageView trashIcon = new ImageView(getContext());
        LinearLayout.LayoutParams trashIconParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        trashIconParams.gravity = Gravity.CENTER_VERTICAL;
        int dpToPx = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
        trashIconParams.setMargins(dpToPx, 0, 0, 0);
        trashIcon.setLayoutParams(trashIconParams);
        trashIcon.setImageResource(android.R.drawable.ic_menu_delete);
        trashIcon.setColorFilter(getResources().getColor(R.color.black));
        trashIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRow[0] != null) {
                    contentLayout.removeView(selectedRow[0]);
                    selectedRow[0] = null;
                } else {
                    Toast.makeText(getContext(), "No se ha seleccionado ningún ítem", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //----------------Variables finales para añadir ----------------------------------------
        addMaterialIconContainer.addView(addMaterialIcon);
        addMaterialIconContainer.addView(trashIcon);
        contentLayout.addView(addMaterialIconContainer);
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


    /**
     * Metodo que transfoma la primera en Mayuscula y el resto en minisculas
     * @param str la palabra que se introduce
     * @return la palabra ya convertida
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String rest = word.length() > 1 ? word.substring(1).toLowerCase() : "";
                sb.append(firstChar).append(rest).append(" ");
            }
        }
        return sb.toString().trim();
    }



}
