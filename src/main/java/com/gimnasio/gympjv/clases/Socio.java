package com.gimnasio.gympjv.clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Socio del gimnasio.
 * Datos propios: qrCode, puntos, login y password (para iniciar sesion como socio).
 * El resto se obtiene via las asociaciones (Suscripcion, PlanDieta, Reservas, Ventas).
 */
public class Socio extends Persona {

    private String qrCode;
    private int puntos;
    private String login;
    private String password;
    private Suscripcion suscripcion;
    private PlanDieta planDieta;
    private List<Reserva> reservas;
    private List<Venta> historialVentas;

    public Socio(String id, String nombre, String telefono, String email,
                 LocalDate fechaAlta, String qrCode,
                 String login, String password) {
        super(id, nombre, telefono, email, fechaAlta);
        this.qrCode = qrCode;
        this.login = login;
        this.password = password;
        this.puntos = 0;
        this.reservas = new ArrayList<>();
        this.historialVentas = new ArrayList<>();
    }

    // Constructor secundario sin login
    public Socio(String id, String nombre, String telefono, String email,
                 LocalDate fechaAlta, String qrCode) {
        this(id, nombre, telefono, email, fechaAlta, qrCode, null, null);
    }

    public boolean comprarBono(int sesiones) {
        if (suscripcion == null) return false;
        com.gimnasio.gympjv.clases.enums.TipoBono tipo;
        if (sesiones == 10) tipo = com.gimnasio.gympjv.clases.enums.TipoBono.BONO_10;
        else if (sesiones == 20) tipo = com.gimnasio.gympjv.clases.enums.TipoBono.BONO_20;
        else return false;
        String idBono = "BONO-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        BonoSesiones bono = new BonoSesiones(idBono, suscripcion, tipo,
                LocalDate.now().plusMonths(6));
        suscripcion.anadirBono(bono);
        return true;
    }

    public void anadirDieta(String dia, String comida) {
        if (planDieta != null) planDieta.anadirComida(dia, comida);
    }

    public boolean tieneSesiones() {
        if (suscripcion == null) return false;
        return suscripcion.calcularSaldoClases() > 0;
    }

    public void reservarClase() {
        // Delegado al controlador.
    }

    public boolean accederQR() {
        if (suscripcion == null) return false;
        return suscripcion.getEstado() == com.gimnasio.gympjv.clases.enums.EstadoSuscripcion.ACTIVA;
    }

    public boolean comprobarPassword(String passwordIntroducida) {
        if (password == null || passwordIntroducida == null) return false;
        return password.equals(passwordIntroducida);
    }

    public List<Reserva> consultarHistorial() {
        return new ArrayList<>(reservas);
    }

    public void anadirReserva(Reserva reserva) {
        if (reserva != null && !reservas.contains(reserva)) reservas.add(reserva);
    }

    public void anadirVenta(Venta venta) {
        if (venta != null && !historialVentas.contains(venta)) historialVentas.add(venta);
    }

    public String getQrCode() { return qrCode; }
    public int getPuntos() { return puntos; }
    public String getLogin() { return login; }
    public Suscripcion getSuscripcion() { return suscripcion; }
    public PlanDieta getPlanDieta() { return planDieta; }
    public List<Reserva> getReservas() { return reservas; }
    public List<Venta> getHistorialVentas() { return historialVentas; }

    public String getPassword() { return password; }

    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public void setSuscripcion(Suscripcion suscripcion) { this.suscripcion = suscripcion; }
    public void setPlanDieta(PlanDieta planDieta) { this.planDieta = planDieta; }

    @Override
    public String toString() {
        return "Socio " + nombre + " (" + id + ") - Puntos: " + puntos;
    }
}
