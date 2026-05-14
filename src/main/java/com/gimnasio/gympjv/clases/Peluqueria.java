package com.gimnasio.gympjv.clases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de peluqueria realizado a un socio (o cliente externo).
 * productosUsados se concreta a List<Producto> para integrarse con el stock.
 */
public class Peluqueria {

    private String idServicio;
    private Socio socio;
    private Empleado peluquero;
    private double precio;
    private List<String> servicios;
    private double precioPelo;
    private double precioBarba;
    private double precioCejas;
    private double totalCalculado;
    private List<Producto> productosUsados;
    private LocalDateTime fechaHora;
    private boolean finalizado;

    public Peluqueria(String idServicio, Socio socio, Empleado peluquero,
                      LocalDateTime fechaHora) {
        this.idServicio = idServicio;
        this.socio = socio;
        this.peluquero = peluquero;
        this.fechaHora = fechaHora;
        this.servicios = new ArrayList<>();
        this.productosUsados = new ArrayList<>();
        this.precioPelo = 12.0;
        this.precioBarba = 8.0;
        this.precioCejas = 5.0;
        this.precio = 0.0;
        this.totalCalculado = 0.0;
        this.finalizado = false;
    }

    public void anadirServicio(String servicio) {
        if (servicio == null) {
            return;
        }
        servicios.add(servicio);
        if ("Pelo".equalsIgnoreCase(servicio)) {
            precio += precioPelo;
        } else if ("Barba".equalsIgnoreCase(servicio)) {
            precio += precioBarba;
        } else if ("Cejas".equalsIgnoreCase(servicio)) {
            precio += precioCejas;
        }
    }

    public void anadirProducto(Producto producto) {
        if (producto != null) {
            productosUsados.add(producto);
        }
    }

    public double calcularTotal() {
        double total = precio;
        for (Producto p : productosUsados) {
            total += p.getPrecioVenta();
        }
        this.totalCalculado = total;
        return total;
    }

    /**
     * Finaliza el corte y descuenta del stock los productos usados.
     */
    public void finalizarCorte() {
        if (finalizado) {
            return;
        }
        for (Producto p : productosUsados) {
            p.vender(1);
        }
        calcularTotal();
        finalizado = true;
    }

    public String imprimirTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TICKET PELUQUERIA ===\n");
        sb.append("Servicio: ").append(idServicio).append("\n");
        if (socio != null) {
            sb.append("Cliente: ").append(socio.getNombre()).append("\n");
        }
        sb.append("Peluquero: ").append(peluquero.getNombre()).append("\n");
        for (String s : servicios) {
            sb.append("- ").append(s).append("\n");
        }
        for (Producto p : productosUsados) {
            sb.append("- ").append(p.getNombre()).append(" (")
              .append(p.getPrecioVenta()).append(" EUR)\n");
        }
        sb.append("TOTAL: ").append(totalCalculado).append(" EUR\n");
        return sb.toString();
    }

    public String getIdServicio() {
        return idServicio;
    }

    public Socio getSocio() {
        return socio;
    }

    public Empleado getPeluquero() {
        return peluquero;
    }

    public double getPrecio() {
        return precio;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public double getPrecioPelo() {
        return precioPelo;
    }

    public double getPrecioBarba() {
        return precioBarba;
    }

    public double getPrecioCejas() {
        return precioCejas;
    }

    public double getTotalCalculado() {
        return totalCalculado;
    }

    public List<Producto> getProductosUsados() {
        return productosUsados;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    @Override
    public String toString() {
        String nomCliente = (socio != null) ? socio.getNombre() : "Cliente externo";
        return "Peluqueria " + idServicio + " - " + nomCliente + " - " + totalCalculado + " EUR";
    }
}
