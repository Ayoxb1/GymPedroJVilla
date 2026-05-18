package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.RolUsuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Monitor del gimnasio. Imparte clases grupales y elabora planes de dieta.
 */
public class Monitor extends UsuarioSistema {

    private String especialidad;
    private List<ClaseGrupal> clasesAsignadas;
    private List<PlanDieta> planesDieta;

    public Monitor(String id, String nombre, String telefono, String email,
                   LocalDate fechaAlta, double salarioBase, String horario,
                   String login, String password, String especialidad) {
        super(id, nombre, telefono, email, fechaAlta, salarioBase, horario,
              login, password, RolUsuario.MONITOR);
        this.especialidad = especialidad;
        this.clasesAsignadas = new ArrayList<>();
        this.planesDieta = new ArrayList<>();
    }

    public void crearRutina(Socio socio) {
        // sin implementar aquí, lo gestiona el controlador
    }

    public void asignarDieta(Socio socio) {
        // sin implementar aquí
    }

    public void registrarAsistencia(Socio socio) {
        // sin implementar aquí
    }

    public List<Reserva> consultarReservas() {
        List<Reserva> todas = new ArrayList<>();
        for (ClaseGrupal clase : clasesAsignadas) {
            todas.addAll(clase.getListaReservas());
        }
        return todas;
    }

    public void anadirClase(ClaseGrupal clase) {
        if (!clasesAsignadas.contains(clase)) {
            clasesAsignadas.add(clase);
        }
    }

    public void anadirPlanDieta(PlanDieta plan) {
        if (!planesDieta.contains(plan)) {
            planesDieta.add(plan);
        }
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public List<ClaseGrupal> getClasesAsignadas() {
        return clasesAsignadas;
    }

    public List<PlanDieta> getPlanesDieta() {
        return planesDieta;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "Monitor " + nombre + " - Especialidad: " + especialidad;
    }
}
