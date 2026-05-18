package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.EstadoSuscripcion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Suscripción de un socio, con su historial de mensualidades y bonos.
 */
public class Suscripcion {

    private String idSuscripcion;
    private Socio socio;
    private String tipoSuscripcion;
    private double importeMensual;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int clasesIncluidas;
    private int sesionesConsumidas;
    private EstadoSuscripcion estado;
    private List<Mensualidad> mensualidades;
    private List<BonoSesiones> bonosAdicionales;

    public Suscripcion(String idSuscripcion, Socio socio, String tipoSuscripcion,
                       double importeMensual, LocalDate fechaInicio, LocalDate fechaFin,
                       int clasesIncluidas) {
        this.idSuscripcion = idSuscripcion;
        this.socio = socio;
        this.tipoSuscripcion = tipoSuscripcion;
        this.importeMensual = importeMensual;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.clasesIncluidas = clasesIncluidas;
        this.sesionesConsumidas = 0;
        this.estado = EstadoSuscripcion.ACTIVA;
        this.mensualidades = new ArrayList<>();
        this.bonosAdicionales = new ArrayList<>();
    }

    public void renovarSuscripcion() {
        this.fechaFin = this.fechaFin.plusMonths(1);
        this.estado = EstadoSuscripcion.ACTIVA;
    }

    /** Descuenta una sesión: primero de las incluidas en el plan, si no de un bono. */
    public boolean consumirClase() {
        if (estado != EstadoSuscripcion.ACTIVA) {
            return false;
        }
        if (sesionesConsumidas < clasesIncluidas) {
            sesionesConsumidas++;
            return true;
        }
        // sin sesiones en el plan, tiro de bonos
        for (BonoSesiones bono : bonosAdicionales) {
            if (bono.consumirSesion()) {
                return true;
            }
        }
        return false;
    }

    public String generarFactura() {
        return "Factura suscripcion " + idSuscripcion + " - " + importeMensual + " EUR";
    }

    public boolean verificarVencimiento() {
        if (LocalDate.now().isAfter(fechaFin)) {
            this.estado = EstadoSuscripcion.VENCIDA;
            return true;
        }
        return false;
    }

    public void anadirBono(BonoSesiones bono) {
        if (bono != null && !bonosAdicionales.contains(bono)) {
            bonosAdicionales.add(bono);
        }
    }

    public void anadirMensualidad(Mensualidad mensualidad) {
        if (mensualidad != null && !mensualidades.contains(mensualidad)) {
            mensualidades.add(mensualidad);
        }
    }

    public int calcularSaldoClases() {
        int saldoMensual = clasesIncluidas - sesionesConsumidas;
        if (saldoMensual < 0) {
            saldoMensual = 0;
        }
        int saldoBonos = 0;
        for (BonoSesiones bono : bonosAdicionales) {
            if (!bono.estaVencido()) {
                saldoBonos += bono.getSesionesRestantes();
            }
        }
        return saldoMensual + saldoBonos;
    }

    public void cancelarSuscripcion() {
        this.estado = EstadoSuscripcion.CANCELADA;
    }

    public String getIdSuscripcion() {
        return idSuscripcion;
    }

    public Socio getSocio() {
        return socio;
    }

    public String getTipoSuscripcion() {
        return tipoSuscripcion;
    }

    public double getImporteMensual() {
        return importeMensual;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getClasesIncluidas() {
        return clasesIncluidas;
    }

    public int getSesionesConsumidas() {
        return sesionesConsumidas;
    }

    public EstadoSuscripcion getEstado() {
        return estado;
    }

    public List<Mensualidad> getMensualidades() {
        return mensualidades;
    }

    public List<BonoSesiones> getBonosAdicionales() {
        return bonosAdicionales;
    }

    public void setEstado(EstadoSuscripcion estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Suscripcion " + idSuscripcion + " - " + tipoSuscripcion + " - " + estado;
    }
}
