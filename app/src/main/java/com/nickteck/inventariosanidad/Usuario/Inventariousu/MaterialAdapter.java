package com.nickteck.inventariosanidad.Usuario.Inventariousu;

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
import com.nickteck.inventariosanidad.sampledata.Material;
import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {
    private final List<Material> lista;
    private final Context context;
    public interface OnMaterialClickListener { void onClick(Material item);}
    private final OnMaterialClickListener listener;
    public MaterialAdapter(List<Material> lista, Context context, OnMaterialClickListener listener) {
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

    /**
     * Trae los materiales de la lista y los define
     * Le cambia de color dependiendo de si no hay materiales
     * Aqui se realiza las animaciones
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material item = lista.get(position);

        holder.tvNombre.setText(item.getNombre());
        holder.tvUnidades.setText(String.valueOf(item.getUnidades()));
        holder.tvAlmacen.setText(item.getAlmacen());
        holder.tvArmario.setText(item.getArmario());

        holder.tvArmario.setSingleLine(false);
        holder.tvArmario.setMaxLines(3);
        holder.tvArmario.setEllipsize(null);
        holder.tvArmario.setHorizontallyScrolling(false);

        if (item.getUnidades() < item.getUnidades_min()) {
            holder.tvUnidades.setTextColor(Color.RED);
        } else {
            holder.tvUnidades.setTextColor(Color.BLACK);
        }

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.animacion_entrada_item);
        holder.itemView.startAnimation(anim);
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    /**
     * Metodo que dice cuantos elementos de la lista hay
     * @return el numero de lista
     */
    @Override
    public int getItemCount() {
        return lista.size();
    }

    /**
     * Muestra los materiales asociados a sus correspondientes TextView
     */
    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUnidades, tvAlmacen, tvArmario;
        MaterialViewHolder(View itemView) {
            super(itemView);
            tvNombre   = itemView.findViewById(R.id.tvNombre);
            tvUnidades = itemView.findViewById(R.id.tvUnidades);
            tvAlmacen  = itemView.findViewById(R.id.tvAlmacen);
            tvArmario  = itemView.findViewById(R.id.tvarma);
        }
    }
}
