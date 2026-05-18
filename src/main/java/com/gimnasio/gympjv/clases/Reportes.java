package com.gimnasio.gympjv.clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Cálculos de reportes a partir de las listas del Repositorio.
 */
public class Reportes {

    private List<Socio> socios;
    private List<Venta> ventas;
    private List<ClaseGrupal> clases;
    private GestorStock gestorStock;

    public Reportes(List<Socio> socios, List<Venta> ventas,
                    List<ClaseGrupal> clases, GestorStock gestorStock) {
        this.socios = socios;
        this.ventas = ventas;
        this.clases = clases;
        this.gestorStock = gestorStock;
    }

    public double ingresosDiarios() {
        double total = 0.0;
        LocalDate hoy = LocalDate.now();
        for (Venta v : ventas) {
            if (v.isConfirmada() && v.getFechaHora().toLocalDate().equals(hoy)) {
                total += v.getTotal();
            }
        }
        return total;
    }

    public double ingresosMensuales() {
        double total = 0.0;
        LocalDate hoy = LocalDate.now();
        int mes = hoy.getMonthValue();
        int anio = hoy.getYear();
        for (Venta v : ventas) {
            if (v.isConfirmada()
                    && v.getFechaHora().getMonthValue() == mes
                    && v.getFechaHora().getYear() == anio) {
                total += v.getTotal();
            }
        }
        return total;
    }

    public Map<String, Integer> ocupacionClases() {
        Map<String, Integer> mapa = new HashMap<>();
        for (ClaseGrupal c : clases) {
            mapa.put(c.getNombre(), c.getOcupacionActual());
        }
        return mapa;
    }

    public int sociosActivos() {
        int n = 0;
        for (Socio s : socios) {
            if (s.getSuscripcion() != null
                    && s.getSuscripcion().getEstado()
                       == com.gimnasio.gympjv.clases.enums.EstadoSuscripcion.ACTIVA) {
                n++;
            }
        }
        return n;
    }

    public List<Producto> topProductos() {
        // ordeno por unidades vendidas sumando los items de las ventas confirmadas
        Map<Producto, Integer> contador = new HashMap<>();
        for (Venta v : ventas) {
            if (!v.isConfirmada()) continue;
            for (ItemVenta it : v.getItems()) {
                contador.merge(it.getProducto(), it.getCantidad(), Integer::sum);
            }
        }
        List<Producto> orden = new ArrayList<>(contador.keySet());
        orden.sort(Comparator.comparingInt(contador::get).reversed());
        if (orden.size() > 10) {
            return orden.subList(0, 10);
        }
        return orden;
    }

    public double ventasTotales() {
        double total = 0.0;
        for (Venta v : ventas) {
            if (v.isConfirmada()) {
                total += v.getTotal();
            }
        }
        return total;
    }

    public List<Producto> productosBajoStock() {
        return gestorStock.checkAlertas();
    }
}
