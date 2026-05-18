package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.TipoBono;
import java.time.LocalDate;

/**
 * Bono de sesiones (10 o 20) asociado a una Suscripcion.
 */
public class BonoSesiones {

    private String idBono;
    private Suscripcion suscripcion;
    private TipoBono tipo;
    private int sesionesRestantes;
    private LocalDate fechaExpiracion;

    public BonoSesiones(String idBono, Suscripcion suscripcion, TipoBono tipo,
                        LocalDate fechaExpiracion) {
        this.idBono = idBono;
        this.suscripcion = suscripcion;
        this.tipo = tipo;
        this.sesionesRestantes = tipo.getSesiones();
        this.fechaExpiracion = fechaExpiracion;
    }

    /** Descuenta una sesión. Devuelve false si está vencido o ya vacío. */
    public boolean consumirSesion() {
        if (estaVencido()) {
            return false;
        }
        if (sesionesRestantes <= 0) {
            return false;
        }
        sesionesRestantes--;
        return true;
    }

    public void recargar() {
        this.sesionesRestantes = tipo.getSesiones();
    }

    public boolean estaVencido() {
        return LocalDate.now().isAfter(fechaExpiracion);
    }

    public String getIdBono() {
        return idBono;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public TipoBono getTipo() {
        return tipo;
    }

    public int getSesionesRestantes() {
        return sesionesRestantes;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    @Override
    public String toString() {
        return "Bono " + tipo + " - Restantes: " + sesionesRestantes + " - Expira: " + fechaExpiracion;
    }
}
