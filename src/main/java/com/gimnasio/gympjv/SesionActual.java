package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.UsuarioSistema;

/**
 * Guarda quién está logueado: un UsuarioSistema o un Socio, nunca los dos a la vez.
 */
public final class SesionActual {

    private static UsuarioSistema usuarioActual;
    private static Socio socioActual;

    private SesionActual() {}

    // ── Admin / Monitor ──────────────────────────────────────────────────────────
    public static UsuarioSistema getUsuario() {
        return usuarioActual;
    }

    public static void setUsuario(UsuarioSistema usuario) {
        usuarioActual = usuario;
        socioActual = null;
    }

    // ── Socio ────────────────────────────────────────────────────────────────────
    public static Socio getSocio() {
        return socioActual;
    }

    public static void setSocio(Socio socio) {
        socioActual = socio;
        usuarioActual = null;
    }

    // ── Estado ───────────────────────────────────────────────────────────────────
    public static void cerrar() {
        if (usuarioActual != null) usuarioActual.cerrarSesion();
        usuarioActual = null;
        socioActual = null;
    }

    public static boolean haySesion() {
        return (usuarioActual != null && usuarioActual.isSesionActiva())
            || socioActual != null;
    }

    public static boolean esAdmin() {
        return usuarioActual != null
            && usuarioActual.getRol() == com.gimnasio.gympjv.clases.enums.RolUsuario.ADMIN;
    }

    public static boolean esMonitor() {
        return usuarioActual != null
            && usuarioActual.getRol() == com.gimnasio.gympjv.clases.enums.RolUsuario.MONITOR;
    }

    public static boolean esSocio() {
        return socioActual != null;
    }
}
