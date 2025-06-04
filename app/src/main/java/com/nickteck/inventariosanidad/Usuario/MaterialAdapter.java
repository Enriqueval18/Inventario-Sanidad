package com.nickteck.inventariosanidad.Usuario;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {
    private final List<Inventario.MaterialItem> lista;
    private final Context context;
    public interface OnMaterialClickListener {
        void onClick(Inventario.MaterialItem item);
    }
    private final OnMaterialClickListener listener;
    public MaterialAdapter(List<Inventario.MaterialItem> lista, Context context, OnMaterialClickListener listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Inventario.MaterialItem item = lista.get(position);
        holder.tvNombre.setText(item.nombre);
        holder.tvUnidades.setText(String.valueOf(item.unidades));
        holder.tvAlmacen.setText(item.almacen);
        holder.tvArmario.setText(item.armario);
        holder.tvArmario.setSingleLine(false);
        holder.tvArmario.setMaxLines(3);
        holder.tvArmario.setEllipsize(null);
        holder.tvArmario.setHorizontallyScrolling(false);

        if (item.unidades < item.unidadesMin) {
            holder.tvUnidades.setTextColor(Color.RED);
        } else {
            holder.tvUnidades.setTextColor(Color.BLACK);
        }

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.animacion_entrada_item);
        holder.itemView.startAnimation(anim);
        holder.itemView.setOnClickListener(v -> listener.onClick(item));

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUnidades, tvAlmacen, tvArmario;
        MaterialViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUnidades = itemView.findViewById(R.id.tvUnidades);
            tvAlmacen = itemView.findViewById(R.id.tvAlmacen);
            tvArmario = itemView.findViewById(R.id.tvarma);
        }
    }

}
