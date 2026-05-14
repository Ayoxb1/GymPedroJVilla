package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.RolUsuario;
import java.time.LocalDate;

/**
 * Empleado con credenciales de acceso al sistema.
 * Solo Admin y Monitor extienden esta clase.
 */
public abstract class UsuarioSistema extends Empleado {

    protected String login;
    protected String password;
    protected RolUsuario rol;
    protected boolean sesionActiva;

    public UsuarioSistema(String id, String nombre, String telefono, String email,
                          LocalDate fechaAlta, double salarioBase, String horario,
                          String login, String password, RolUsuario rol) {
        super(id, nombre, telefono, email, fechaAlta, salarioBase, horario);
        this.login = login;
        this.password = password;
        this.rol = rol;
        this.sesionActiva = false;
    }

    public boolean tienePermiso(String permiso) {
        // Por defecto, un usuario tiene los permisos derivados de su rol.
        // Las subclases pueden afinarlo.
        return sesionActiva;
    }

    public boolean iniciarSesion() {
        this.sesionActiva = true;
        return true;
    }

    public void cerrarSesion() {
        this.sesionActiva = false;
    }

    public boolean comprobarPassword(String passwordIntroducida) {
        if (passwordIntroducida == null) {
            return false;
        }
        return this.password.equals(passwordIntroducida);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isSesionActiva() {
        return sesionActiva;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
