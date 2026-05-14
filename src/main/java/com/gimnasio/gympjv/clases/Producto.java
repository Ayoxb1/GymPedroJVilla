package com.gimnasio.gympjv.clases;

/**
 * Producto del catalogo de la tienda.
 */
public class Producto {

    private String idProducto;
    private String nombre;
    private double precioVenta;
    private int stock;
    private int stockMinimo;

    public Producto(String idProducto, String nombre, double precioVenta, int stock, int stockMinimo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    /**
     * Vende una cantidad concreta de este producto.
     * Devuelve true si habia stock suficiente.
     */
    public boolean vender(int cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        if (stock < cantidad) {
            return false;
        }
        stock -= cantidad;
        return true;
    }

    public boolean necesitaReponer() {
        return stock <= stockMinimo;
    }

    public void reponer(int cantidad) {
        if (cantidad > 0) {
            stock += cantidad;
        }
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    @Override
    public String toString() {
        return nombre + " (" + idProducto + ") - " + precioVenta + " EUR - Stock: " + stock;
    }
}
