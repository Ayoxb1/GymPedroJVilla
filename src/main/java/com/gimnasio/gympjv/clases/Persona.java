package com.gimnasio.gympjv.clases;

import java.time.LocalDate;

/**
 * Clase abstracta base de la jerarquia de personas del sistema.
 * Atributos comunes a Socio y Empleado (y por tanto a UsuarioSistema, Admin, Monitor).
 */
public abstract class Persona {

    protected String id;
    protected String nombre;
    protected String telefono;
    protected String email;
    protected LocalDate fechaAlta;

    public Persona(String id, String nombre, String telefono, String email, LocalDate fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.fechaAlta = fechaAlta;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public abstract String toString();
}
