package com.gimnasio.gympjv.clases;

import java.time.LocalDateTime;

/**
 * Registro de asistencia de un socio a una clase grupal.
 */
public class Asistencia {

    private String idAsistencia;
    private Socio socio;
    private ClaseGrupal clase;
    private LocalDateTime fechaHora;
    private boolean presente;

    public Asistencia(String idAsistencia, Socio socio, ClaseGrupal clase, LocalDateTime fechaHora) {
        this.idAsistencia = idAsistencia;
        this.socio = socio;
        this.clase = clase;
        this.fechaHora = fechaHora;
        this.presente = false;
    }

    public void marcarPresente()  { this.presente = true; }
    public void marcarAusente()   { this.presente = false; }

    public String getIdAsistencia()   { return idAsistencia; }
    public Socio getSocio()           { return socio; }
    public ClaseGrupal getClase()     { return clase; }
    public LocalDateTime getFechaHora(){ return fechaHora; }
    public boolean isPresente()       { return presente; }

    public String getEstadoTexto()    { return presente ? "PRESENTE" : "AUSENTE"; }

    @Override
    public String toString() {
        return socio.getNombre() + " - " + clase.getNombre() + " - " + getEstadoTexto();
    }
}
