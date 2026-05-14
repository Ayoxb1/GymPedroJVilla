package com.gimnasio.gympjv.clases;

import java.time.LocalDate;

/**
 * Empleado del gimnasio. Concreta porque los peluqueros se instancian aqui.
 * Admin y Monitor heredaran a traves de UsuarioSistema.
 */
public class Empleado extends Persona {

    protected double salarioBase;
    protected String horario;

    public Empleado(String id, String nombre, String telefono, String email,
                    LocalDate fechaAlta, double salarioBase, String horario) {
        super(id, nombre, telefono, email, fechaAlta);
        this.salarioBase = salarioBase;
        this.horario = horario;
    }

    public double calcularNomina() {
        return salarioBase;
    }

    public String consultarHorario() {
        return horario;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public String getHorario() {
        return horario;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "Empleado " + nombre + " (" + id + ") - Horario: " + horario;
    }
}
