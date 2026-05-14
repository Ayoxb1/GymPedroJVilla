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
        // La lógica concreta vive en la clase Reportes.
    }

    public void gestionarUsuarios() {
        // Implementado por la capa de controladores y los gestores de datos.
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
