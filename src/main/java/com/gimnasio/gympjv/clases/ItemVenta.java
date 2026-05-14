package com.gimnasio.gympjv.clases;

/**
 * Linea de venta. Renombrado desde ITEMVENTA del UML por convencion Java.
 */
public class ItemVenta {

    private Producto producto;
    private int cantidad;
    private double precioUnitario;

    public ItemVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
    }

    public double calcularImporte() {
        return precioUnitario * cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return producto.getNombre() + " x" + cantidad + " = " + calcularImporte() + " EUR";
    }
}
