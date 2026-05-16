package com.gimnasio.gympjv;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import modulos.FuncionesGraficas;

import java.io.IOException;

public class ConfiguracionController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCarpetaBackup;

    @FXML
    private void initialize() {
        if (SesionActual.getUsuario() == null) {
            FuncionesGraficas.warning(
                "Sesion",
                "No hay sesion activa.");
            try {
                App.cambiarVista("InicioSesion");
            } catch (IOException ex) {
                FuncionesGraficas.warning(
                    "Error", ex.getMessage());
            }
            return;
        }
        ConfiguracionApp cfg = ConfiguracionApp.getInstancia();
        txtNombre.setText(cfg.getNombreCentro());
        txtDireccion.setText(cfg.getDireccion());
        txtTelefono.setText(cfg.getTelefono());
        txtCarpetaBackup.setText(cfg.getCarpetaBackup());
    }

    @FXML
    private void onGuardar() {
        ConfiguracionApp cfg = ConfiguracionApp.getInstancia();
        String nombre = txtNombre.getText().trim();
        String dir    = txtDireccion.getText().trim();
        String tel    = txtTelefono.getText().trim();
        String backup = txtCarpetaBackup.getText().trim();

        if (nombre.isBlank()) {
            FuncionesGraficas.warning("Configuracion", "El nombre del centro no puede estar vacio.");
            return;
        }
        cfg.setNombreCentro(nombre);
        cfg.setDireccion(dir);
        cfg.setTelefono(tel);
        if (!backup.isBlank()) cfg.setCarpetaBackup(backup);

        FuncionesGraficas.mostrarMensaje("Configuracion", "Configuracion guardada correctamente.", 1);
    }

    @FXML
    private void onRestablecer() {
        boolean ok = FuncionesGraficas.pedirBool("Restablecer",
                "Restablecer la configuracion por defecto?");
        if (!ok) return;
        ConfiguracionApp cfg = ConfiguracionApp.getInstancia();
        cfg.setNombreCentro("Pedro J. Villa");
        cfg.setDireccion("Carretera de Madrid, 47, 30500 Molina de Segura, Murcia");
        cfg.setTelefono("868 05 20 10");
        cfg.setCarpetaBackup(ConfiguracionApp.rutaBackupPorDefecto());
        initialize();
        FuncionesGraficas.mostrarMensaje("Configuracion", "Configuracion restablecida.", 1);
    }
}
