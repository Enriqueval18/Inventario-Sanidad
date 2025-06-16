package com.nickteck.inventariosanidad.Profesor.HistorialPro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private final List<HistorialItem> originalItems;
    private final List<HistorialItem> filteredItems;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public HistorialAdapter(List<HistorialItem> items) {
        this.originalItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorialItem item = filteredItems.get(position);
        holder.tvHora.setText(String.format("Fecha: %s", item.getTime()));
        holder.tvEstudiante.setText(String.format("Usuario: %s", item.getStudent()));
        holder.tvMaterial.setText(String.format("Material: %s", item.getMaterial()));
        holder.tvCantidad.setText(String.format("Cantidad recogida: %s", item.getCantidad()));
    }

    @Override public int getItemCount() {
        return filteredItems.size();
    }

    public void addItem(HistorialItem item) {
        originalItems.add(item);
        filteredItems.add(item);
        notifyItemInserted(filteredItems.size() - 1);
    }

    /**
     * Filtra por rango de fechas (inclusive).
     * @param desde fecha inicial
     * @param hasta   fecha final
     */
    public void filtrarfecha(Date desde, Date hasta) {
        filteredItems.clear();
        for (HistorialItem it : originalItems) {
            try {
                Date d = sdf.parse(it.getTime());
                if ((desde == null || !Objects.requireNonNull(d).before(desde)) &&
                        (hasta   == null || !Objects.requireNonNull(d).after(hasta))) {
                    filteredItems.add(it);
                }
            } catch (ParseException ignored) {}
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvEstudiante, tvMaterial, tvCantidad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora       = itemView.findViewById(R.id.hora);
            tvEstudiante = itemView.findViewById(R.id.estudiante);
            tvMaterial   = itemView.findViewById(R.id.material);
            tvCantidad   = itemView.findViewById(R.id.cantidad);
        }
    }
}
