package com.nickteck.inventariosanidad.Profesor.ActividadesProfesor;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.Material;
import com.nickteck.inventariosanidad.sampledata.MaterialListCallback;
import com.nickteck.inventariosanidad.sampledata.MaterialSelectionListener;
import com.nickteck.inventariosanidad.sampledata.RespuestaCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;

import java.util.ArrayList;
import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder> {

    private List<ActividadItem> originalList = new ArrayList<>();
    private List<ActividadItem> listaFiltrada = new ArrayList<>();
    private final Context context;

    public ActividadesAdapter(List<ActividadItem> lista, Context context) {
        this.context = context;
        originalList.addAll(lista);
        listaFiltrada.addAll(lista);
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

        holder.contenedor.removeAllViews();
        for (int i = 0; i < item.getMateriales().size(); i++) {
            String material = item.getMateriales().get(i);
            int cantidad = item.getCantidades().get(i);

            TextView fila = new TextView(context);
            fila.setText("• " + material + " — " + cantidad);
            fila.setTextColor(Color.DKGRAY);
            holder.contenedor.addView(fila);
        }

        holder.estado.setText(item.isEnviado() ? "Enviado" : "No enviado");
        holder.estado.setTextColor(ContextCompat.getColor(context, item.isEnviado() ? R.color.success : R.color.error));

        // Oculta o muestra botones según si fue enviado
        holder.btnEnviar.setEnabled(!item.isEnviado());
        holder.btnAgregarMaterial.setEnabled(!item.isEnviado());
        holder.btnEnviar.setAlpha(item.isEnviado() ? 0.4f : 1.0f); // Visual feedback
        holder.btnAgregarMaterial.setAlpha(item.isEnviado() ? 0.4f : 1.0f);




        holder.btnEliminar.setOnClickListener(v -> {
            Log.d("DEBUG", "Eliminando actividad con ID: " + item.getActivityId());
            int userId = context.getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("user_id", -1);
            if (userId == -1) {
                Toast.makeText(context, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(context)
                    .setTitle("Eliminar actividad")
                    .setMessage("¿Deseas eliminar esta actividad?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        if (item.getActivityId() == -1) {
                            int index = originalList.indexOf(item);
                            originalList.remove(item);
                            filtrar("");
                            notifyItemRemoved(index);
                            Toast.makeText(context, "Actividad eliminada localmente", Toast.LENGTH_SHORT).show();
                        } else {
                            Utilidades.eliminarActividadUsuario(userId, item.getActivityId(), new RespuestaCallback() {
                                @Override
                                public void onResultado(boolean correcto) {
                                    if (correcto) {
                                        int index = originalList.indexOf(item);
                                        originalList.remove(item);
                                        filtrar("");
                                        notifyItemRemoved(index);
                                        Toast.makeText(context, "Actividad eliminada", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "No se pudo eliminar la actividad", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(boolean error) {
                                    Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        holder.btnAgregarMaterial.setOnClickListener(v -> mostrarDialogoAgregarMaterial(holder, item));
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

    private void mostrarDialogoAgregarMaterial(ActividadViewHolder holder, ActividadItem item) {
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

        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String cantidadStr = etCantidad.getText().toString().trim();
                    if (materialSeleccionado[0] == null || cantidadStr.isEmpty()) {
                        Toast.makeText(context, "Selecciona un material y cantidad válida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int cantidad = Integer.parseInt(cantidadStr);

                    TextView nuevo = new TextView(context);
                    nuevo.setText("• " + materialSeleccionado[0] + " — " + cantidad);
                    nuevo.setTextColor(Color.DKGRAY);
                    nuevo.setTextSize(15);
                    holder.contenedor.addView(nuevo);

                    item.getMateriales().add(materialSeleccionado[0]);
                    item.getCantidades().add(cantidad);

                    notifyItemChanged(holder.getAdapterPosition());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    private void showMaterialSelectionDialog(MaterialSelectionListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_material, null);
        android.widget.SearchView searchView = dialogView.findViewById(R.id.searchView);
        ListView listView = dialogView.findViewById(R.id.listViewMaterials);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Selecciona un Material")
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, nombres);
                listView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
                    @Override public boolean onQueryTextSubmit(String query) { return false; }
                    @Override public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    listener.onMaterialSelected(adapter.getItem(position));
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

    public void actualizarListaCompleta(List<ActividadItem> nuevaLista) {
        this.originalList = new ArrayList<>(nuevaLista);
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

}
