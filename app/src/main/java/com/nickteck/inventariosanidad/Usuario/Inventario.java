package com.nickteck.inventariosanidad.Usuario;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nickteck.inventariosanidad.R;
import com.nickteck.inventariosanidad.sampledata.MaterialCallback;
import com.nickteck.inventariosanidad.sampledata.Utilidades;
import java.util.ArrayList;
import java.util.List;

public class Inventario extends Fragment {
    private RecyclerView recyclerInventario;
    private MaterialAdapter adapter;
    private LinearLayout layoutError;
    private Button btnReintentar;
    private SearchView searchView;
    private List<MaterialItem> materialesList = new ArrayList<>();
    class MaterialItem {
        String nombre;
        int unidades;
        String almacen;
        String armario;
        String estante;
        int unidadesMin;
        String descripcion;

        MaterialItem(String nombre, int unidades, String almacen, String armario, String estante, int unidadesMin, String descripcion) {
            this.nombre = nombre;
            this.unidades = unidades;
            this.almacen = almacen;
            this.armario = armario;
            this.estante = estante;
            this.unidadesMin = unidadesMin;
            this.descripcion = descripcion;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        searchView      = view.findViewById(R.id.searchView);

        recyclerInventario = view.findViewById(R.id.recyclerInventario);
        recyclerInventario.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutError = view.findViewById(R.id.layoutError);
        btnReintentar = view.findViewById(R.id.btnReintentar);

        btnReintentar.setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            obtenerDatosInventario();
        });

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filtrarTabla(query);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    filtrarTabla(newText);
                    return false;
                }
            });
        }

        obtenerDatosInventario();
        return view;
    }
    private void obtenerDatosInventario() {
        layoutError.setVisibility(View.GONE);
        materialesList.clear();
        Utilidades.obtenerMateriales(new MaterialCallback() {
            @Override
            public void onMaterialObtenido(String nombre, int unidades, String almacen, String armario, String estante, int unidades_min, String descripcion) {
                materialesList.add(new MaterialItem(nombre, unidades, almacen, armario, estante, unidades_min, descripcion));
            }
            @Override
            public void onFailure(boolean error) {
                requireActivity().runOnUiThread(() -> {
                    layoutError.setVisibility(View.VISIBLE);
                });

            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(() -> {refreshTabla(materialesList);}, 800);
    }
    private void refreshTabla(List<MaterialItem> lista) {
        adapter = new MaterialAdapter(lista, getContext(), item -> {
            MostrarInventario dialog = new MostrarInventario();
            Bundle args = new Bundle();
            args.putString("nombre",      item.nombre);
            args.putInt   ("unidades",    item.unidades);
            args.putString("almacen",     item.almacen);
            args.putString("armario",     item.armario);
            args.putString("estante",     item.estante);
            args.putInt   ("unidadesm",   item.unidadesMin);
            args.putString("descripcion", item.descripcion);
            dialog.setArguments(args);
            dialog.show(getParentFragmentManager(), "MostrarInventario");
        });
        recyclerInventario.setAdapter(adapter);
    }
    private void filtrarTabla(String query) {
        query = query.toLowerCase();
        List<MaterialItem> listaFiltrada = new ArrayList<>();
        for (MaterialItem item : materialesList) {
            if (item.nombre.toLowerCase().contains(query)) {
                listaFiltrada.add(item);
            }
        }
        refreshTabla(listaFiltrada);
    }
}
