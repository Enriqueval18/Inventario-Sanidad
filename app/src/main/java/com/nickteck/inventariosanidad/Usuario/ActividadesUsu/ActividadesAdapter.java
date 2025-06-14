package com.nickteck.inventariosanidad.Usuario.ActividadesUsu;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialListCallback;
import com.nickteck.inventariosanidad.sampledata.MaterialSelectionListener;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder> {

    private List<ActividadItem> originalList;
    private List<ActividadItem> listaFiltrada;
    private final Context context;

    public ActividadesAdapter(List<ActividadItem> lista, Context context) {
        this.originalList = lista;
        this.listaFiltrada = new ArrayList<>(lista);
        this.context = context;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        ActividadItem item = listaFiltrada.get(position);
        holder.titulo.setText(item.getTitulo());

        // Limpiar antes de añadir
        holder.contenedor.removeAllViews();
        for (int i = 0; i < item.getMateriales().size(); i++) {
            String material = item.getMateriales().get(i);
            int cantidad = item.getCantidades().get(i);

            TextView fila = new TextView(context);
            fila.setText("• " + material + " — " + cantidad);
            fila.setTextColor(Color.DKGRAY);
            holder.contenedor.addView(fila);
        }

        if (item.isEnviado()) {
            holder.estado.setText("Enviado");
            holder.estado.setTextColor(Color.parseColor("#388E3C")); // Verde
        } else {
            holder.estado.setText("No enviado");
            holder.estado.setTextColor(Color.parseColor("#D32F2F")); // Rojo
        }

        holder.btnEnviar.setOnClickListener(v -> {
            if (!item.isEnviado()) {
                item.setEnviado(true);
                notifyItemChanged(position);
                Toast.makeText(context, "Actividad enviada: " + item.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            int index = originalList.indexOf(item);
            originalList.remove(item);
            filtrar("");
            notifyItemRemoved(index);
        });

        holder.btnAgregarMaterial.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.anadir_item_material, null);
            Button btnSelectMaterial = dialogView.findViewById(R.id.btnSelectMaterial2);
            EditText etCantidad = dialogView.findViewById(R.id.item_Cant);

            final String[] materialSeleccionado = {null};

            btnSelectMaterial.setOnClickListener(view -> {
                showMaterialSelectionDialog(selected -> {
                    materialSeleccionado[0] = selected;
                    btnSelectMaterial.setText(selected);
                });
            });


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView)
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        String cantidadStr = etCantidad.getText().toString().trim();
                        if (materialSeleccionado[0] == null || cantidadStr.isEmpty()) {
                            Toast.makeText(context, "Selecciona un material y cantidad válida", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int cantidad = Integer.parseInt(cantidadStr);

                        // Añadir visual
                        TextView nuevo = new TextView(context);
                        nuevo.setText("• " + materialSeleccionado[0] + " — " + cantidad);
                        nuevo.setTextColor(Color.DKGRAY);
                        nuevo.setTextSize(15);
                        holder.contenedor.addView(nuevo);

                        // Añadir a datos
                        item.getMateriales().add(materialSeleccionado[0]);
                        item.getCantidades().add(cantidad);

                        notifyItemChanged(position);
                    })

                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public void filtrar(String query) {
        listaFiltrada.clear();
        if (query == null || query.trim().isEmpty()) {
            listaFiltrada.addAll(originalList);
        } else {
            String lower = query.toLowerCase();
            for (ActividadItem item : originalList) {
                if (item.getTitulo().toLowerCase().contains(lower)) {
                    listaFiltrada.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, estado;
        LinearLayout contenedor;
        ImageButton btnEnviar, btnEliminar;
        ImageView btnAgregarMaterial;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            estado = itemView.findViewById(R.id.tvEstado);
            contenedor = itemView.findViewById(R.id.layoutMateriales);
            btnEnviar = itemView.findViewById(R.id.btnEnviar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnAgregarMaterial = itemView.findViewById(R.id.btnAgregarMaterial);
        }
    }

    public void añadirActividad(ActividadItem item) {
        originalList.add(item);
        filtrar(""); // Actualiza la lista filtrada y notifica
    }

    // Método para mostrar el diálogo de selección de material
    private void showMaterialSelectionDialog(MaterialSelectionListener listener) {
        // Inflar el layout del diálogo usando el context correcto
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_material, null);
        android.widget.SearchView searchView = dialogView.findViewById(R.id.searchView);
        ListView listView = dialogView.findViewById(R.id.listViewMaterials);

        // Crear el AlertDialog con el contexto correcto
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Selecciona un Material")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();

        // Llamar a la utilidad para obtener materiales
        Utilidades.getMaterialList(new MaterialListCallback() {
            @Override
            public void onSuccess(List<Material> materialList) {
                List<String> nombres = new ArrayList<>();
                for (Material m : materialList) {
                    nombres.add(m.getNombre());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_list_item_1,
                        nombres
                );
                listView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
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


                listView.setOnItemClickListener((parent, view, position, id) -> {
                    String seleccionado = adapter.getItem(position);
                    listener.onMaterialSelected(seleccionado);
                    dialog.dismiss();
                });

                dialog.show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Error al cargar materiales", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
