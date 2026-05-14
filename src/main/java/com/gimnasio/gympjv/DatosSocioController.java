package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import modulos.FuncionesGraficas;

import java.io.IOException;

/**
 * Muestra los datos personales del socio en sesion.
 */
public class DatosSocioController {

    @FXML private Label lblId;
    @FXML private Label lblNombre;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;
    @FXML private Label lblFechaAlta;
    @FXML private Label lblPuntos;
    @FXML private Label lblQrCode;

    @FXML
    private void initialize() {
        Socio s = SesionActual.getSocio();
        if (s == null) {
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
        lblId.setText(s.getId());
        lblNombre.setText(s.getNombre());
        lblTelefono.setText(s.getTelefono());
        lblEmail.setText(s.getEmail());
        lblFechaAlta.setText(String.valueOf(s.getFechaAlta()));
        lblPuntos.setText(String.valueOf(s.getPuntos()));
        lblQrCode.setText(s.getQrCode());
    }
}
