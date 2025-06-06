package com.nickteck.inventariosanidad.Profesor.MaterialesPro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import java.lang.reflect.Type;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialListCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Materiales extends Fragment {
    private static final String ARCHIVO_MATERIALES = "materiales.txt"; // Nombre del archivo
    private List<MaterialItem> listaMateriales = new ArrayList<>();
    private Button bntAnanir;
    private LinearLayout contenedorg;
    private String nombreArchivoMateriales;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiales, container, false);
        ScrollView scrollView = view.findViewById(R.id.scrollViewMateriales);

        bntAnanir = view.findViewById(R.id.btnPedirMateriales);
        contenedorg = view.findViewById(R.id.contenedortarjetas);
        contenedorg.setLayoutTransition(new android.animation.LayoutTransition());

        bntAnanir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.pedir_material, null);
                TextView tvNombre = dialogView.findViewById(R.id.tvNombreMaterial);
                EditText etMateriales = dialogView.findViewById(R.id.etMateriales);

                final String[] nombreSeleccionado = {null};

                tvNombre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMaterialSelectionDialog(new MaterialSelectionListener() {
                            @Override
                            public void onMaterialSelected(String nombre) {
                                nombreSeleccionado[0] = nombre;
                                tvNombre.setText(nombre);
                            }
                        });
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pedir Nuevo Material");
                builder.setView(dialogView)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cantidad = etMateriales.getText().toString().trim();
                                String nombre = nombreSeleccionado[0];

                                if (nombre != null && !cantidad.isEmpty()) {
                                    addNewSection(nombre, cantidad);
                                } else {
                                    Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        ImageView imgEnviar = view.findViewById(R.id.imgEnviar);
        imgEnviar.setOnClickListener(v -> {
            boolean algunoSeleccionado = false;
            List<View> tarjetasAEliminar = new ArrayList<>();
            List<String> nombresAEliminar = new ArrayList<>();

            for (int i = 0; i < contenedorg.getChildCount(); i++) {
                View card = contenedorg.getChildAt(i);
                Boolean seleccionada = (Boolean) card.getTag();
                if (seleccionada != null && seleccionada) {
                    algunoSeleccionado = true;
                    TextView tvTitle = card.findViewById(R.id.tvTitle);
                    if (tvTitle != null) {
                        nombresAEliminar.add(tvTitle.getText().toString());
                    }
                    tarjetasAEliminar.add(card);
                }
            }

            if (algunoSeleccionado) {
                for (int i = 0; i < tarjetasAEliminar.size(); i++) {
                    contenedorg.removeView(tarjetasAEliminar.get(i));
                    eliminarDeLista(nombresAEliminar.get(i));
                }
                guardarMaterialesEnArchivo();
                Toast.makeText(getContext(), "Se ha enviado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Debes seleccionar al menos un material", Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String nombreProfesor = prefs.getString("nombreProfesor", "profesor"); // Usa un valor real
        nombreArchivoMateriales = "materiales_" + nombreProfesor + ".txt";

        cargarMaterialesDesdeArchivo();
        return view;
    }

    /**
     * Infla el layout de la card  y asigna los valores
     * @param nombre Nombre del material seleccionado.
     * @param materiales Información adicional (por ejemplo, cantidad).
     */
    private void addNewSection(String nombre, String materiales) {
        addNewSection(nombre, materiales, true);
    }

    private void addNewSection(String nombre, String materiales, boolean guardar) {

        // Evitar duplicados
        for (int i = 0; i < contenedorg.getChildCount(); i++) {
            View child = contenedorg.getChildAt(i);
            TextView tvTitle = child.findViewById(R.id.tvTitle);
            if (tvTitle != null && tvTitle.getText().toString().equalsIgnoreCase(nombre)) {
                Toast.makeText(getContext(), "Este material ya fue añadido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_material_custom, contenedorg, false);
        TextView tvTitle = cardView.findViewById(R.id.tvTitle);
        TextView tvDetail = cardView.findViewById(R.id.tvDetail);
        ImageButton btnEliminar = cardView.findViewById(R.id.btnEliminar);

        tvTitle.setText(nombre);
        tvDetail.setText("Cantidad: " + materiales);

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenedorg.removeView(cardView);
                eliminarDeLista(nombre);
                guardarMaterialesEnArchivo();
                Toast.makeText(getContext(), "Material eliminado", Toast.LENGTH_SHORT).show();
            }
        });

        contenedorg.addView(cardView);
        cardView.setTag(false); // no seleccionada inicialmente

        cardView.setOnClickListener(v -> {
            boolean seleccionada = !(Boolean) cardView.getTag();
            cardView.setTag(seleccionada);
            MaterialCardView card = (MaterialCardView) cardView;

            if (seleccionada) {
                card.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.primary_300)); // o tu color
                card.setStrokeWidth(4);
            } else {
                card.setStrokeColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
                card.setStrokeWidth(0);
            }

        });


        if (guardar) {
            listaMateriales.add(new MaterialItem(nombre, materiales));
            guardarMaterialesEnArchivo();
        }
    }

    private void eliminarDeLista(String nombre) {
        Iterator<MaterialItem> it = listaMateriales.iterator();
        while (it.hasNext()) {
            if (it.next().getNombre().equalsIgnoreCase(nombre)) {
                it.remove();
                break;
            }
        }
    }


    /**
     * Muestra un diálogo con buscador para seleccionar un material
     * @param listener Callback cuando se selecciona un material
     */
    private void showMaterialSelectionDialog(final MaterialSelectionListener listener) {
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_material, null);
        final SearchView searchView = dialogView.findViewById(R.id.searchView);
        final ListView listView = dialogView.findViewById(R.id.listViewMaterials);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona un Material")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null);

        final AlertDialog dialog = builder.create();

        Utilidades.getMaterialList(new MaterialListCallback() {
            @Override
            public void onSuccess(List<Material> materialList) {
                final List<String> materialNames = new ArrayList<>();
                for (Material m : materialList) {
                    materialNames.add(m.getNombre());
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        materialNames
                );
                listView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    /**
     * Interfaz para notificar la selección de un material
     */
    interface MaterialSelectionListener {
        void onMaterialSelected(String nombre);
    }


    private void guardarMaterialesEnArchivo() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(listaMateriales); // Convertimos lista a JSON
            FileOutputStream fos = getContext().openFileOutput(nombreArchivoMateriales, Context.MODE_PRIVATE);
            fos.write(json.getBytes()); // Escribimos el JSON como texto
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarMaterialesDesdeArchivo() {
        try {
            FileInputStream fis = getContext().openFileInput(nombreArchivoMateriales);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<MaterialItem>>() {}.getType();
            listaMateriales = gson.fromJson(sb.toString(), listType);

            // Volvemos a mostrar en la UI
            for (MaterialItem item : listaMateriales) {
                addNewSection(item.getNombre(), item.getCantidad(), false); // false = no volver a guardar
            }

        } catch (IOException e) {
            e.printStackTrace(); // Si no existe el archivo, no pasa nada
        }
    }








}
