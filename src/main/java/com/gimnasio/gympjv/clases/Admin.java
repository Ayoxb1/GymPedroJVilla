package com.gimnasio.gympjv.clases;

import com.gimnasio.gympjv.clases.enums.RolUsuario;
import java.time.LocalDate;

/**
 * Administrador del sistema. Acceso total.
 */
public class Admin extends UsuarioSistema {

    public Admin(String id, String nombre, String telefono, String email,
                 LocalDate fechaAlta, double salarioBase, String horario,
                 String login, String password) {
        super(id, nombre, telefono, email, fechaAlta, salarioBase, horario,
              login, password, RolUsuario.ADMIN);
    }

    public void generarReportes() {
        // lo hace la clase Reportes
    }

    public void gestionarUsuarios() {
        // lo hacen los controladores
    }

    public void gestionarSocios() {
    }

    public void gestionarProductos() {
    }

    public void gestionarStock() {
    }

    @Override
    public String toString() {
        return "Admin " + nombre + " (" + login + ")";
    }
}
