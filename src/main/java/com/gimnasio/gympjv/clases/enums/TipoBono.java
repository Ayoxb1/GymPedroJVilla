package com.gimnasio.gympjv.clases.enums;

/**
 * Tipos de bono de sesiones disponibles.
 * Cada valor lleva asociado el numero de sesiones.
 */
public enum TipoBono {
    BONO_10(10),
    BONO_20(20);

    private final int sesiones;

    TipoBono(int sesiones) {
        this.sesiones = sesiones;
    }

    public int getSesiones() {
        return sesiones;
    }
}
