package com.gimnasio.gympjv.clases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase grupal con aforo máximo y lista de reservas.
 */
public class ClaseGrupal {

    private String idClase;
    private String nombre;
    private LocalDateTime fecha;
    private Monitor monitor;
    private List<Reserva> listaReservas;
    private int ocupacionActual;
    private int aforoMaximo;

    public ClaseGrupal(String idClase, String nombre, LocalDateTime fecha,
                       Monitor monitor, int aforoMaximo) {
        this.idClase = idClase;
        this.nombre = nombre;
        this.fecha = fecha;
        this.monitor = monitor;
        this.aforoMaximo = aforoMaximo;
        this.ocupacionActual = 0;
        this.listaReservas = new ArrayList<>();
    }

    public void anadirReserva(Reserva reserva) {
        if (reserva == null) {
            return;
        }
        if (estaLlena()) {
            return;
        }
        if (listaReservas.contains(reserva)) {
            return;
        }
        listaReservas.add(reserva);
        ocupacionActual++;
    }

    public void eliminarReserva(Reserva reserva) {
        if (listaReservas.remove(reserva)) {
            ocupacionActual--;
        }
    }

    public int getOcupacion() {
        return ocupacionActual;
    }

    public boolean estaLlena() {
        return ocupacionActual >= aforoMaximo;
    }

    public String getIdClase() {
        return idClase;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public List<Reserva> getListaReservas() {
        return listaReservas;
    }

    public int getOcupacionActual() {
        return ocupacionActual;
    }

    public int getAforoMaximo() {
        return aforoMaximo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setAforoMaximo(int aforoMaximo) {
        this.aforoMaximo = aforoMaximo;
    }

    @Override
    public String toString() {
        return nombre + " - " + fecha + " - " + ocupacionActual + "/" + aforoMaximo
                + " (Monitor: " + monitor.getNombre() + ")";
    }
}
