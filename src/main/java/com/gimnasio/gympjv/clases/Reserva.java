package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.EstadoReserva;
import java.time.LocalDateTime;

/**
 * Reserva de una clase grupal por un socio.
 * Reserva.servicio se concreta a ClaseGrupal (regla 12: no Object).
 */
public class Reserva {

    private String idReserva;
    private Socio socio;
    private ClaseGrupal servicio;
    private LocalDateTime fecha;
    private EstadoReserva estado;

    public Reserva(String idReserva, Socio socio, ClaseGrupal servicio, LocalDateTime fecha) {
        this.idReserva = idReserva;
        this.socio = socio;
        this.servicio = servicio;
        this.fecha = fecha;
        this.estado = EstadoReserva.PENDIENTE;
    }

    /**
     * Confirma la reserva si el socio tiene credito (mensualidad activa o bono con sesiones).
     * Devuelve true si la reserva queda confirmada.
     */
    public boolean confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            return false;
        }
        if (!consumirCreditos()) {
            return false;
        }
        if (servicio.estaLlena()) {
            return false;
        }
        servicio.anadirReserva(this);
        this.estado = EstadoReserva.CONFIRMADA;
        return true;
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
        if (servicio != null) {
            servicio.eliminarReserva(this);
        }
    }

    /**
     * Intenta consumir un credito de la suscripcion del socio.
     * Implementa la asociacion shared con Mensualidad/BonoSesiones sin guardar referencia.
     */
    public boolean consumirCreditos() {
        if (socio == null) {
            return false;
        }
        Suscripcion sus = socio.getSuscripcion();
        if (sus == null) {
            return false;
        }
        return sus.consumirClase();
    }

    public String getIdReserva() {
        return idReserva;
    }

    public Socio getSocio() {
        return socio;
    }

    public ClaseGrupal getServicio() {
        return servicio;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva " + idReserva + " - " + socio.getNombre() + " - "
                + servicio.getNombre() + " - " + estado;
    }
}
