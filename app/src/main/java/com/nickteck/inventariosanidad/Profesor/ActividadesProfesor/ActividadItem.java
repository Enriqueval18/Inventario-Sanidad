package com.nickteck.inventariosanidad.Profesor.ActividadesProfesor;

import java.util.ArrayList;
import java.util.List;

public class ActividadItem {
    private int activityId;
    private String titulo;
    private List<String> materiales;
    private List<Integer> cantidades;
    private boolean enviado;

    public ActividadItem(int activityId, String titulo, List<String> materiales, List<Integer> cantidades, boolean enviado) {
        // Asegura que ambas listas sean mutables
        this.activityId = activityId;
        this.titulo = titulo;
        this.materiales = new ArrayList<>(materiales);
        this.cantidades = new ArrayList<>(cantidades);
        this.enviado = enviado;
    }
    public int getActivityId() {
        return activityId;
    }
    public String getTitulo() {
        return titulo;
    }

    public List<String> getMateriales() {
        return materiales;
    }

    public List<Integer> getCantidades() {
        return cantidades;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }
}
