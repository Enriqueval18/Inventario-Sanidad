package com.nickteck.inventariosanidad.Usuario;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;
import com.nickteck.inventariosanidad.sampledata.MaterialListCallback;
import com.nickteck.inventariosanidad.sampledata.MaterialSelectionListener;
import com.nickteck.inventariosanidad.sampledata.Utilidades;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Actividades extends Fragment {
    private SearchView cuadrobus;
    private ImageView btnananir;
    private LinearLayout sectionsContainer;
    final LinearLayout[] selectedSection = new LinearLayout[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);

        //----------------------- Cuadro de busqueda-------------------------------------
        cuadrobus = view.findViewById(R.id.busquedatabla);
        if (cuadrobus != null) {
            cuadrobus.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String texto = query;
                    int totalSecciones = sectionsContainer.getChildCount();
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
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    String texto = newText.toString();
                    int totalSecciones = sectionsContainer.getChildCount();
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
                    return false;
                }
            });
        }

        //------------------------Boton de añadir----------------------------------------
        btnananir = view.findViewById(R.id.btnana);
        btnananir.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.anadir_item_cabecera);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.parseColor("#001a33"))
                );
            }

            // 3) Referencias a vistas y lógica interna…
            EditText etNombre    = dialog.findViewById(R.id.etNombre);
            Button  btnSelectMat = dialog.findViewById(R.id.btnSelectMaterial);
            EditText etCantidad  = dialog.findViewById(R.id.etCantidad);
            Button  btnCancel    = dialog.findViewById(R.id.btnCancel);
            Button  btnAccept    = dialog.findViewById(R.id.btnAccept);
            final String[] matSel= new String[1];

            btnSelectMat.setOnClickListener(x -> {
                showMaterialSelectionDialog(material -> {
                    matSel[0] = material;
                    btnSelectMat.setText(material);
                });
            });
            btnCancel.setOnClickListener(x -> dialog.dismiss());
            btnAccept.setOnClickListener(x -> {
                String nombre   = etNombre.getText().toString().trim();
                String cantidad = etCantidad.getText().toString().trim();
                String material = matSel[0];
                if (nombre.isEmpty() || cantidad.isEmpty()
                        || material == null || material.isEmpty()) {
                    Toast.makeText(getContext(),
                            "Rellena todos los campos y selecciona un material",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                anandir_nueva_tabla(nombre, material, cantidad);
                dialog.dismiss();
            });

            // 4) Mostramos el diálogo
            dialog.show();

            // 5) Forzamos el tamaño de la ventana: 90% del ancho, alto WRAP_CONTENT
            Window window = dialog.getWindow();
            if (window != null) {
                // Ancho = 90% del ancho de pantalla
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int ancho = (int) (dm.widthPixels * 0.90);
                // Alto a WRAP_CONTENT (o fija un valor en px/dp si prefieres)
                window.setLayout(ancho, WindowManager.LayoutParams.WRAP_CONTENT);
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
                try {
                    if (selectedSection[0] != null) {
                        // Obtenemos la sección (tabla)
                        LinearLayout section = selectedSection[0];

                        // Buscamos dinámicamente el TextView del header (para evitar errores de cast)
                        LinearLayout headerLayout = (LinearLayout) section.getChildAt(0);
                        TextView headerTitle = null;
                        for (int i = 0; i < headerLayout.getChildCount(); i++) {
                            View child = headerLayout.getChildAt(i);
                            if (child instanceof TextView) {
                                headerTitle = (TextView) child;
                                break;
                            }
                        }
                        if (headerTitle == null) {
                            Toast.makeText(getContext(), "Error: no se encontró el título", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String titulo = headerTitle.getText().toString();
                        Log.d("btnSend", "Título obtenido: " + titulo);

                        // Obtenemos el contenedor de filas (content)
                        LinearLayout contentLayout = (LinearLayout) section.getChildAt(1);
                        int childCount = contentLayout.getChildCount();

                        ArrayList<String> materiales = new ArrayList<>();
                        ArrayList<String> cantidades = new ArrayList<>();

                        // Recorremos todas las filas del content
                        for (int j = 0; j < childCount; j++) {
                            View rowView = contentLayout.getChildAt(j);
                            if (rowView instanceof LinearLayout) {
                                LinearLayout row = (LinearLayout) rowView;
                                if (row.getChildCount() >= 2) {
                                    // Comprobamos que los dos hijos sean TextView
                                    View viewMaterial = row.getChildAt(0);
                                    View viewCantidad = row.getChildAt(1);
                                    if (viewMaterial instanceof TextView && viewCantidad instanceof TextView) {
                                        TextView tvMaterial = (TextView) viewMaterial;
                                        TextView tvCantidad = (TextView) viewCantidad;
                                        materiales.add(tvMaterial.getText().toString());
                                        cantidades.add(tvCantidad.getText().toString());
                                    } else {
                                        Log.e("btnSend", "Fila " + j + ": elemento 0 o 1 no es TextView");
                                    }
                                }
                            }
                        }

                        StringBuilder fileContent = new StringBuilder();
                        fileContent.append("Titulo: ").append(titulo).append("\n");
                        for (int i = 0; i < materiales.size(); i++) {
                            fileContent.append(materiales.get(i))
                                    .append(",")
                                    .append(cantidades.get(i))
                                    .append("\n");
                        }
                        // Agregamos un separador para distinguir entre tablas
                        fileContent.append("\n");

                        Log.d("btnSend", "Contenido a guardar:\n" + fileContent.toString());

                        // Usamos MODE_APPEND para agregar al archivo y no sobrescribirlo
                        FileOutputStream fos = getContext().openFileOutput("tabla_guardada.txt", Context.MODE_APPEND);
                        fos.write(fileContent.toString().getBytes());
                        fos.close();
                        Log.d("btnSend", "Archivo guardado correctamente.");

                        // Bloqueamos la edición de la tabla: deshabilitamos la interacción en cada fila
                        for (int j = 0; j < contentLayout.getChildCount(); j++) {
                            View rowView = contentLayout.getChildAt(j);
                            rowView.setClickable(false);
                            rowView.setEnabled(false);
                        }

                        // Actualizamos el header para indicar visualmente que la tabla ha sido enviada
                        headerLayout.setBackgroundColor(getResources().getColor(R.color.green));

                        Toast.makeText(getContext(), "Datos enviados de la tabla: " + titulo, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "No se ha seleccionado ninguna tabla", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("btnSend", "Error en btnSend: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Error al enviar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        sectionsContainer = view.findViewById(R.id.Contenedor_actividades);
        loadSavedTable();
        return view;

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
                // Inflamos el layout del diálogo "Añadir Nuevo Material"
                final View cuadroAnadir = LayoutInflater.from(getContext()).inflate(R.layout.anadir_item_material, null);

                // Obtenemos la referencia del botón para seleccionar el material y del EditText para la cantidad
                final Button btnSelectMaterial = cuadroAnadir.findViewById(R.id.btnSelectMaterial);
                final EditText etCantidad = cuadroAnadir.findViewById(R.id.item_Cant);

                // Variable para almacenar el material seleccionado (usamos un array para poder modificar su valor desde el inner class)
                final String[] selectedMaterial = new String[1];

                // Al pulsar el botón, se muestra el diálogo con la lista filtrable de materiales
                btnSelectMaterial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMaterialSelectionDialog(new MaterialSelectionListener() {
                            @Override
                            public void onMaterialSelected(String material) {
                                selectedMaterial[0] = material;
                                // Actualizamos el texto del botón para mostrar el material seleccionado
                                btnSelectMaterial.setText(material);
                            }
                        });
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(cuadroAnadir)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Obtenemos el valor de cantidad y el material seleccionado
                                String cantidad = etCantidad.getText().toString().trim();
                                String material = selectedMaterial[0];

                                if (material != null && !material.isEmpty() && !cantidad.isEmpty()) {
                                    // Validamos que el material exista llamando a la API
                                    Utilidades.validarMaterial(getContext(), material, new MaterialCallback() {
                                        @Override
                                        public void onMaterialObtenido(int material_id,String nombreMaterial, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion) {
                                            // Verificamos si el material ya ha sido añadido en contentLayout.
                                            // Se recorre contentLayout y se compara el nombre del material.
                                            boolean existe = false;
                                            int childCount = contentLayout.getChildCount();
                                            for (int i = 0; i < childCount; i++) {
                                                View child = contentLayout.getChildAt(i);
                                                // Excluimos el contenedor del icono para añadir
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

                                            // Creamos una nueva fila para agregar el material y la cantidad
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

                                            // Insertamos la nueva fila antes del contenedor del icono si se encuentra
                                            int addIconIndex = contentLayout.indexOfChild(addMaterialIconContainer);
                                            if (addIconIndex != -1) {
                                                contentLayout.addView(newRow, addIconIndex);
                                            } else {
                                                contentLayout.addView(newRow);
                                            }

                                            // Configuramos la selección de la fila
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
                                } else {
                                    Toast.makeText(getContext(), "Rellena todos los campos y selecciona un material", Toast.LENGTH_SHORT).show();
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
    private void showMaterialSelectionDialog(final MaterialSelectionListener listener) {
        // Inflamos el layout del diálogo con el buscador y ListView
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_material, null);
        final SearchView searchView = dialogView.findViewById(R.id.searchView);
        final ListView listView = dialogView.findViewById(R.id.listViewMaterials);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona un Material")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null);

        final AlertDialog dialog = builder.create();

        // Obtén la lista de materiales desde la API (método en Utilidades)
        Utilidades.getMaterialList(new MaterialListCallback() {
            @Override
            public void onSuccess(List<Material> materialList) {
                // Extraemos los nombres de los materiales
                final List<String> materialNames = new ArrayList<>();
                for (Material m : materialList) {
                    materialNames.add(m.getNombre());
                }
                // Configuramos un ArrayAdapter para el ListView
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        materialNames
                );
                listView.setAdapter(adapter);

                // Configuramos el SearchView para filtrar el contenido del adapter
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String query){
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText){
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

                // Al pulsar sobre un item se retorna el material seleccionado
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedMaterial = adapter.getItem(position);
                        listener.onMaterialSelected(selectedMaterial);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Error al cargar los materiales", Toast.LENGTH_SHORT).show();
            }
        });
    }
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
    private void loadSavedTable() {
        Log.d("LoadSavedTable", "Iniciando loadSavedTable()...");

        // Verificar si el archivo existe en el almacenamiento interno
        File file = getContext().getFileStreamPath("tabla_guardada.txt");
        if (file == null || !file.exists()) {
            Log.d("LoadSavedTable", "El archivo 'tabla_guardada.txt' no existe.");
            Toast.makeText(getContext(), "Tabla guardada no encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FileInputStream fis = getContext().openFileInput("tabla_guardada.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            // Recorrer todo el archivo. Cada bloque (tabla) se separa por una línea vacía:
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Saltamos líneas vacías
                }

                // La primera línea del bloque es el título
                String headerLine = line.trim();
                String titulo;
                if (headerLine.startsWith("Titulo: ")) {
                    // "Titulo: " ocupa 8 caracteres (0-7)
                    titulo = headerLine.substring(8).trim();
                } else {
                    titulo = headerLine;
                }
                Log.d("LoadSavedTable", "Cargando tabla con título: " + titulo);

                // Se descarta la siguiente línea que es el encabezado de columnas
                if ((line = reader.readLine()) != null) {
                    Log.d("LoadSavedTable", "Saltando encabezado: " + line);
                }

                // Leer las filas de datos (omitiendo la fila de encabezado) hasta encontrar una línea vacía
                ArrayList<String> materialRows = new ArrayList<>();
                ArrayList<String> cantidadRows = new ArrayList<>();
                while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                    Log.d("LoadSavedTable", "Leyendo fila: " + line);
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        materialRows.add(parts[0].trim());
                        cantidadRows.add(parts[1].trim());
                    } else {
                        Log.e("LoadSavedTable", "La línea tiene formato incorrecto: " + line);
                    }
                }

                // Combinar las filas usando salto de línea
                String materialesCombined = android.text.TextUtils.join("\n", materialRows);
                String cantidadesCombined = android.text.TextUtils.join("\n", cantidadRows);

                // Crear la tabla usando tu método personalizado
                // Se espera que anandir_nueva_tabla agregue la nueva sección a sectionsContainer
                anandir_nueva_tabla(titulo, materialesCombined, cantidadesCombined);

                // Una vez creada la tabla, la deshabilitamos: asumimos que se agregó al final del contenedor.
                int count = sectionsContainer.getChildCount();
                if (count > 0) {
                    View lastSection = sectionsContainer.getChildAt(count - 1);
                    lastSection.setClickable(false);
                    lastSection.setEnabled(false);
                    // También aseguramos que el header tenga fondo verde
                    if (lastSection instanceof LinearLayout) {
                        LinearLayout sec = (LinearLayout) lastSection;
                        if (sec.getChildCount() > 0) {
                            View headerView = sec.getChildAt(0);
                            if (headerView instanceof LinearLayout) {
                                ((LinearLayout) headerView).setBackgroundColor(getResources().getColor(R.color.green));
                            }
                        }
                    }
                }
                Log.d("LoadSavedTable", "Tabla '" + titulo + "' cargada y deshabilitada.");
            }
            reader.close();
            Log.d("LoadSavedTable", "Finalizada carga de tablas desde el archivo.");
        } catch (FileNotFoundException e) {
            Log.e("LoadSavedTable", "Archivo no encontrado: " + e.getMessage());
            Toast.makeText(getContext(), "No se encontró la tabla guardada", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("LoadSavedTable", "Error de lectura: " + e.getMessage());
            Toast.makeText(getContext(), "Error al cargar la tabla", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }





}
