package com.gimnasio.gympjv.clases;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plan de dieta personalizado para un socio, creado por un monitor.
 */
public class PlanDieta {

    private String idPlan;
    private Socio socio;
    private Monitor monitor;
    private int calorias;
    private Map<String, String> comidas;

    public PlanDieta(String idPlan, Socio socio, Monitor monitor, int calorias) {
        this.idPlan = idPlan;
        this.socio = socio;
        this.monitor = monitor;
        this.calorias = calorias;
        this.comidas = new LinkedHashMap<>();
    }

    public void anadirComida(String dia, String descripcion) {
        if (dia != null && descripcion != null) {
            comidas.put(dia, descripcion);
        }
    }

    public int calcularCalorias() {
        return calorias;
    }

    public void actualizarPlan() {
        // Punto de extension - delegado a controlador.
    }

    public String getIdPlan() {
        return idPlan;
    }

    public Socio getSocio() {
        return socio;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public int getCalorias() {
        return calorias;
    }

    public Map<String, String> getComidas() {
        return comidas;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    @Override
    public String toString() {
        return "Plan dieta " + idPlan + " - " + calorias + " kcal - " + comidas.size() + " comidas";
    }
}
