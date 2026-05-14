package com.gimnasio.gympjv.clases;

import java.time.LocalDateTime;

/**
 * Sesion de entrenamiento personal entre un socio y un monitor.
 * (No pasa por Reserva: tiene su propio metodo programar()).
 */
public class SesionPersonal {

    private String idSesion;
    private Socio socio;
    private Monitor entrenador;
    private LocalDateTime fecha;
    private int duracion;
    private String objetivos;
    private String intensidad;

    public SesionPersonal(String idSesion, Socio socio, Monitor entrenador,
                          LocalDateTime fecha, int duracion, String objetivos, String intensidad) {
        this.idSesion = idSesion;
        this.socio = socio;
        this.entrenador = entrenador;
        this.fecha = fecha;
        this.duracion = duracion;
        this.objetivos = objetivos;
        this.intensidad = intensidad;
    }

    public void programar(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double calcularPrecio() {
        // Precio simple: 0.50 EUR por minuto, ajustable segun intensidad
        double base = duracion * 0.5;
        if ("ALTA".equalsIgnoreCase(intensidad)) {
            base *= 1.25;
        }
        return base;
    }

    public void modificarSesion() {
        // Punto de extension - delegado a controlador.
    }

    public String getIdSesion() {
        return idSesion;
    }

    public Socio getSocio() {
        return socio;
    }

    public Monitor getEntrenador() {
        return entrenador;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getDuracion() {
        return duracion;
    }

    public String getObjetivos() {
        return objetivos;
    }

    public String getIntensidad() {
        return intensidad;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public void setObjetivos(String objetivos) {
        this.objetivos = objetivos;
    }

    public void setIntensidad(String intensidad) {
        this.intensidad = intensidad;
    }

    @Override
    public String toString() {
        return "Sesion " + idSesion + " - " + socio.getNombre() + " con "
                + entrenador.getNombre() + " - " + fecha;
    }
}
