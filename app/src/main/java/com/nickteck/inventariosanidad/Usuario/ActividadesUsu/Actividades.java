package com.nickteck.inventariosanidad.Usuario.ActividadesUsu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Actividades extends Fragment {
    private SearchView cuadrobus;
    private ImageView btnananir;
    private RecyclerView recyclerView;
    private ActividadesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);

        cuadrobus = view.findViewById(R.id.busquedatabla);
        btnananir = view.findViewById(R.id.btnana);
        recyclerView = view.findViewById(R.id.recyclerViewActividades);

        adapter = new ActividadesAdapter(new ArrayList<>(), getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

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

        btnananir.setOnClickListener(v -> mostrarDialogoNuevaTabla());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarActividadesDesdeBD();
    }

    private void mostrarDialogoNuevaTabla() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.anadir_item_cabecera, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        Button btnSelectMat = dialogView.findViewById(R.id.btnSelectMaterial);
        final String[] matSel = new String[1];

        btnSelectMat.setOnClickListener(x -> {
            showMaterialSelectionDialog(material -> {
                matSel[0] = material;
                btnSelectMat.setText(material);
            });
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
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
                        -1,
                        nombre,
                        Arrays.asList(material),
                        Arrays.asList(cantidadInt),
                        false
                );
                adapter.añadirActividad(nuevoItem);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "La cantidad debe ser un número", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarActividadesDesdeBD() {
        Context context = getContext();
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(context, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Utilidades.verActiviadadesUsuario(userId, new RespuestaFinalCallback() {
            @Override
            public void onResultado(Respuesta respuesta) {
                if (respuesta.getDescripciones() == null ||
                        respuesta.getMateriales() == null ||
                        respuesta.getUnidades() == null ||
                        respuesta.getEnviados() == null) {
                    Toast.makeText(getContext(), "No tienes actividades aún", Toast.LENGTH_SHORT).show();
                    adapter.actualizarListaCompleta(new ArrayList<>());
                    return;
                }
                List<String> idsString = Arrays.asList(respuesta.getActivity_ids().split(","));
                List<String> descripciones = Arrays.asList(respuesta.getDescripciones().split(","));
                List<String> materialesTodos = Arrays.asList(respuesta.getMateriales().split(","));
                List<String> unidadesTodos = Arrays.asList(respuesta.getUnidades().split(","));
                List<String> enviados = Arrays.asList(respuesta.getEnviados().split(","));

                List<ActividadItem> nuevasActividades = new ArrayList<>();

                for (int i = 0; i < descripciones.size(); i++) {
                    String[] matsArray = materialesTodos.get(i).split("\\|");
                    String[] unitsArray = unidadesTodos.get(i).split("\\|");

                    List<String> mats = Arrays.asList(matsArray);
                    List<Integer> cants = new ArrayList<>();
                    for (String u : unitsArray) {
                        try {
                            cants.add(Integer.parseInt(u));
                        } catch (NumberFormatException e) {
                            cants.add(0);
                        }
                    }

                    boolean enviado = enviados.get(i).equals("1");
                    int activityId = Integer.parseInt(idsString.get(i));
                    ActividadItem item = new ActividadItem(activityId, descripciones.get(i), mats, cants, enviado);

                    nuevasActividades.add(item);
                }

                adapter.actualizarListaCompleta(nuevasActividades);
            }

            @Override
            public void onFailure(boolean error) {
                Toast.makeText(context, "No se pudieron cargar actividades", Toast.LENGTH_SHORT).show();
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
