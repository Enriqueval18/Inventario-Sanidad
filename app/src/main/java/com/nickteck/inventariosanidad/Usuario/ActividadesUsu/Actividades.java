package com.nickteck.inventariosanidad.Usuario.ActividadesUsu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialListCallback;
import com.nickteck.inventariosanidad.sampledata.MaterialSelectionListener;
import com.nickteck.inventariosanidad.sampledata.Respuesta;
import com.nickteck.inventariosanidad.sampledata.RespuestaFinalCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Actividades extends Fragment {
    private SearchView cuadrobus;
    private ImageView btnananir;
    private RecyclerView recyclerView;
    private ActividadesAdapter adapter;
    private List<ActividadItem> listaActividades = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);

        cuadrobus = view.findViewById(R.id.busquedatabla);
        cuadrobus.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filtrar(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText);
                return false;
            }
        });

        btnananir = view.findViewById(R.id.btnana);
        btnananir.setOnClickListener(v -> mostrarDialogoNuevaTabla());

        recyclerView = view.findViewById(R.id.recyclerViewActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActividadesAdapter(listaActividades, getContext());
        recyclerView.setAdapter(adapter);

        cargarActividadesDesdeBD();
        return view;
    }

    private void mostrarDialogoNuevaTabla() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.anadir_item_cabecera);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#001a33")));

        EditText etNombre = dialog.findViewById(R.id.etNombre);
        EditText etCantidad = dialog.findViewById(R.id.etCantidad);
        Button btnSelectMat = dialog.findViewById(R.id.btnSelectMaterial);
        final String[] matSel = new String[1];

        btnSelectMat.setOnClickListener(x -> {
            showMaterialSelectionDialog(material -> {
                matSel[0] = material;
                btnSelectMat.setText(material);
            });
        });

        dialog.findViewById(R.id.btnCancel).setOnClickListener(x -> dialog.dismiss());

        dialog.findViewById(R.id.btnAccept).setOnClickListener(x -> {
            String nombre = etNombre.getText().toString().trim();
            String cantidad = etCantidad.getText().toString().trim();
            String material = matSel[0];

            if (nombre.isEmpty() || cantidad.isEmpty() || material == null || material.isEmpty()) {
                Toast.makeText(getContext(), "Rellena todos los campos y selecciona un material", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int cantidadInt = Integer.parseInt(cantidad);
                ActividadItem nuevoItem = new ActividadItem(
                        nombre,
                        Arrays.asList(material),
                        Arrays.asList(cantidadInt),
                        false
                );

                adapter.añadirActividad(nuevoItem);
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "La cantidad debe ser un número", Toast.LENGTH_SHORT).show();
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            window.setLayout((int) (dm.widthPixels * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.show();
    }


    private void cargarActividadesDesdeBD() {
        Utilidades.verActiviadadesUsuario(25, new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                List<String> descripciones = Arrays.asList(respuesta.getDescripciones().split(","));
                List<String> materiales = Arrays.asList(respuesta.getMateriales().split(","));
                List<String> unidades = Arrays.asList(respuesta.getUnidades().split(","));
                List<String> enviados = Arrays.asList(respuesta.getEnviados().split(","));

                for (int i = 0; i < descripciones.size(); i++) {
                    List<String> mats = Arrays.asList(materiales.get(i).split("\\|"));
                    List<Integer> cants = new ArrayList<>();
                    for (String u : unidades.get(i).split("\\|")) {
                        try {
                            cants.add(Integer.parseInt(u));
                        } catch (NumberFormatException e) {
                            cants.add(0);
                        }
                    }
                    boolean enviado = enviados.get(i).equals("1");
                    ActividadItem item = new ActividadItem(descripciones.get(i), mats, cants, enviado);
                    listaActividades.add(item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(boolean error) {
                Toast.makeText(getContext(), "No se pudieron cargar actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showMaterialSelectionDialog(MaterialSelectionListener listener) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_material, null);
        SearchView searchView = dialogView.findViewById(R.id.searchView);
        ListView listView = dialogView.findViewById(R.id.listViewMaterials);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona un Material")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();

        Utilidades.getMaterialList(new MaterialListCallback() {
            @Override
            public void onSuccess(List<Material> materialList) {
                List<String> nombres = new ArrayList<>();
                for (Material m : materialList) {
                    nombres.add(m.getNombre());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        nombres
                );
                listView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override public boolean onQueryTextSubmit(String query) { return false; }

                    @Override public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    String seleccionado = adapter.getItem(position);
                    listener.onMaterialSelected(seleccionado);
                    dialog.dismiss();
                });

                dialog.show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Error al cargar materiales", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
