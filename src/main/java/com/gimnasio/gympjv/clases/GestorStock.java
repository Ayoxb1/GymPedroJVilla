package com.gimnasio.gympjv.clases;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista de productos del catálogo y detección de stock bajo.
 */
public class GestorStock {

    private List<Producto> productos;

    public GestorStock() {
        this.productos = new ArrayList<>();
    }

    public void anadirProducto(Producto producto) {
        if (producto != null && !productos.contains(producto)) {
            productos.add(producto);
        }
    }

    public void eliminarProducto(Producto producto) {
        productos.remove(producto);
    }

    public List<Producto> checkAlertas() {
        List<Producto> alertas = new ArrayList<>();
        for (Producto p : productos) {
            if (p.necesitaReponer()) {
                alertas.add(p);
            }
        }
        return alertas;
    }

    public void reponerStock() {
        // TODO: pendiente de implementar
    }

    public void notificarStockBajo() {
        // TODO: pendiente de implementar
    }

    public Producto buscarPorId(String idProducto) {
        for (Producto p : productos) {
            if (p.getIdProducto().equals(idProducto)) {
                return p;
            }
        }
        return null;
    }

    public List<Producto> getProductos() {
        return productos;
    }
}
