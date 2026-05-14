package com.gimnasio.gympjv.clases;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de stock: agrupa los productos del catalogo y detecta los que necesitan reposicion.
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
        // En una implementacion real, se generaria un pedido al proveedor.
        // Aqui delegamos en cada Producto.
    }

    public void notificarStockBajo() {
        // Se notificaria al admin. La capa GUI lo orquestara.
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
