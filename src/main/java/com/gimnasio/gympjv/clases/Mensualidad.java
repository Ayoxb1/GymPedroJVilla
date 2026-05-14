package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.EstadoPago;
import java.time.LocalDate;

/**
 * Mensualidad asociada a una Suscripcion.
 * El socio se accede via suscripcion.getSocio() (sin duplicar referencia).
 */
public class Mensualidad {

    private String idMensualidad;
    private Suscripcion suscripcion;
    private double importe;
    private LocalDate fechaVencimiento;
    private EstadoPago estado;

    public Mensualidad(String idMensualidad, Suscripcion suscripcion, double importe,
                       LocalDate fechaVencimiento) {
        this.idMensualidad = idMensualidad;
        this.suscripcion = suscripcion;
        this.importe = importe;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = EstadoPago.PENDIENTE;
    }

    public void renovar() {
        // Crea una nueva mensualidad para el siguiente periodo (delegado al gestor).
    }

    public void enviarRecordatorio() {
        // Envio de notificacion al socio - delegado al modulo de mensajeria.
    }

    public void marcarPagada() {
        this.estado = EstadoPago.PAGADA;
    }

    public boolean estaVencida() {
        if (estado == EstadoPago.PAGADA) {
            return false;
        }
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    public String getIdMensualidad() {
        return idMensualidad;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public double getImporte() {
        return importe;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Mensualidad " + idMensualidad + " - " + importe + " EUR - " + estado;
    }
}
