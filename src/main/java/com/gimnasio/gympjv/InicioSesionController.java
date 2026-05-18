package com.gimnasio.gympjv;

import com.gimnasio.gympjv.App;
import com.gimnasio.gympjv.clases.Monitor;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.UsuarioSistema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Login: primero busca UsuarioSistema (Admin/Monitor), si no busca Socio.
 * "Recordarme" guarda solo el usuario en Preferences del SO.
 */
public class InicioSesionController {

    private static final String PREF_USUARIO = "usuario_recordado";
    private static final Preferences prefs = Preferences.userNodeForPackage(InicioSesionController.class);

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkRecordarme;
    @FXML private Button btnEntrar;
    @FXML private Button btnSalir;

    @FXML
    private void initialize() {
        // Enter en el campo contraseña también lanza el login
        txtPassword.setOnAction(this::onEntrar);

        // Cargo el usuario recordado si existe
        String recordado = prefs.get(PREF_USUARIO, null);
        if (recordado != null && !recordado.isBlank()) {
            txtUsuario.setText(recordado);
            chkRecordarme.setSelected(true);
            txtPassword.requestFocus();
        }
    }

    @FXML
    private void onEntrar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario == null || usuario.isBlank()) {
            FuncionesGraficas.warning("Inicio de sesion", "Introduce un usuario.");
            return;
        }
        if (password == null || password.isBlank()) {
            FuncionesGraficas.warning("Inicio de sesion", "Introduce la contrasena.");
            return;
        }

        Repositorio repo = Repositorio.getInstancia();

        // 1) Pruebo como UsuarioSistema (Admin / Monitor)
        UsuarioSistema u = repo.buscarUsuarioPorLogin(usuario.trim());
        if (u != null) {
            if (u.comprobarPassword(password)) {
                u.iniciarSesion();
                SesionActual.setUsuario(u);
                guardarRecordatorio(usuario.trim());
                if (u instanceof Monitor) {
                    MenuMonitorController
                        .datosEntrada((Monitor) u);
                    navegar("MenuMonitor");
                } else {
                    navegar("Menu");
                }
                return;
            }
            FuncionesGraficas.warning("Inicio de sesion", "Contrasena incorrecta.");
            txtPassword.clear();
            return;
        }

        // 2) Pruebo como Socio
        Socio s = repo.buscarSocioPorLogin(usuario.trim());
        if (s != null) {
            if (s.comprobarPassword(password)) {
                SesionActual.setSocio(s);
                guardarRecordatorio(usuario.trim());
                navegar("MenuSocio");
                return;
            }
            FuncionesGraficas.warning("Inicio de sesion", "Contrasena incorrecta.");
            txtPassword.clear();
            return;
        }

        FuncionesGraficas.warning("Inicio de sesion", "Usuario no encontrado.");
        txtPassword.clear();
    }

    private void guardarRecordatorio(String login) {
        if (chkRecordarme != null && chkRecordarme.isSelected()) {
            prefs.put(PREF_USUARIO, login);
        } else {
            prefs.remove(PREF_USUARIO);
        }
    }

    private void navegar(String vista) {
        try {
            App.cambiarVista(vista);
        } catch (IOException ex) {
            FuncionesGraficas.warning("Error", "No se pudo cargar la vista: " + ex.getMessage());
        }
    }

    @FXML
    private void onSalir(ActionEvent event) {
        boolean confirmar = FuncionesGraficas.pedirBool("Salir", "Cerrar la aplicacion?");
        if (confirmar) {
            javafx.application.Platform.exit();
            System.exit(0);
        }
    }
}
