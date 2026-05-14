package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.EstadoSuscripcion;
import com.gimnasio.gympjv.clases.enums.MetodoPago;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Venta en la tienda del gimnasio.
 * Permite cliente externo (socio == null) y aplica descuento por suscripcion activa.
 */
public class Venta {

    public static final double PORCENTAJE_DESCUENTO_SOCIO = 0.05;

    private String idVenta;
    private Socio socio;
    private List<ItemVenta> items;
    private double subtotal;
    private double descuento;
    private double total;
    private MetodoPago metodoPago;
    private LocalDateTime fechaHora;
    private boolean confirmada;

    public Venta(String idVenta, Socio socio, MetodoPago metodoPago) {
        this.idVenta = idVenta;
        this.socio = socio;
        this.metodoPago = metodoPago;
        this.items = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
        this.subtotal = 0.0;
        this.descuento = 0.0;
        this.total = 0.0;
        this.confirmada = false;
    }

    public void agregarItem(ItemVenta item) {
        if (item != null) {
            items.add(item);
            calcularTotal();
        }
    }

    public void calcularTotal() {
        double sub = 0.0;
        for (ItemVenta it : items) {
            sub += it.calcularImporte();
        }
        this.subtotal = sub;
        this.total = sub - descuento;
    }

    /**
     * Aplica un 5% de descuento si el socio tiene suscripcion ACTIVA.
     */
    public void aplicarDescuento(Socio socio) {
        if (socio == null) {
            this.descuento = 0.0;
            calcularTotal();
            return;
        }
        Suscripcion sus = socio.getSuscripcion();
        if (sus != null && sus.getEstado() == EstadoSuscripcion.ACTIVA) {
            this.descuento = subtotal * PORCENTAJE_DESCUENTO_SOCIO;
        } else {
            this.descuento = 0.0;
        }
        calcularTotal();
    }

    /**
     * Confirma la venta: descuenta stock de todos los items.
     * Devuelve false si algun item no tiene stock suficiente.
     */
    public boolean confirmar() {
        for (ItemVenta it : items) {
            if (it.getProducto().getStock() < it.getCantidad()) {
                return false;
            }
        }
        for (ItemVenta it : items) {
            it.getProducto().vender(it.getCantidad());
        }
        this.confirmada = true;
        return true;
    }

    public String generarTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TICKET VENTA ===\n");
        sb.append("Venta: ").append(idVenta).append("\n");
        sb.append("Fecha: ").append(fechaHora).append("\n");
        if (socio != null) {
            sb.append("Socio: ").append(socio.getNombre()).append("\n");
        } else {
            sb.append("Cliente externo\n");
        }
        sb.append("--------------------\n");
        for (ItemVenta it : items) {
            sb.append(it.toString()).append("\n");
        }
        sb.append("--------------------\n");
        sb.append("Subtotal: ").append(subtotal).append(" EUR\n");
        sb.append("Descuento: ").append(descuento).append(" EUR\n");
        sb.append("TOTAL: ").append(total).append(" EUR\n");
        sb.append("Pago: ").append(metodoPago).append("\n");
        return sb.toString();
    }

    public String getIdVenta() {
        return idVenta;
    }

    public Socio getSocio() {
        return socio;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getTotal() {
        return total;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    @Override
    public String toString() {
        String cli = (socio != null) ? socio.getNombre() : "Externo";
        return "Venta " + idVenta + " - " + cli + " - " + total + " EUR - " + metodoPago;
    }
}
