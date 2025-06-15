package com.nickteck.inventariosanidad.Profesor.HistorialPro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private final List<HistorialItem> items;

    public HistorialAdapter(List<HistorialItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorialItem item = items.get(position);
        holder.tvHora.setText(String.format("Fecha: %s", item.getTime()));
        holder.tvEstudiante.setText(String.format("Alumno: %s", item.getStudent()));
        holder.tvMaterial.setText(String.format("Material: %s", item.getMaterial()));
        holder.tvCantidad.setText(String.format("Cantidad recogida: %s", item.getCantidad()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvEstudiante, tvMaterial, tvCantidad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.hora);
            tvEstudiante = itemView.findViewById(R.id.estudiante);
            tvMaterial = itemView.findViewById(R.id.material);
            tvCantidad = itemView.findViewById(R.id.cantidad);
        }
    }
}
