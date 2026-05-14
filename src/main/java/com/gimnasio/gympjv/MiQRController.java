package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Muestra el codigo QR del socio en sesion.
 */
public class MiQRController {

    @FXML private Label lblNombre;
    @FXML private Label lblQrCode;
    @FXML private Label lblEstadoAcceso;
    @FXML private ImageView imgQR;

    @FXML
    private void initialize() {
        Socio s = SesionActual.getSocio();
        if (s == null) {
            modulos.FuncionesGraficas.warning(
                "Sesion",
                "No hay sesion activa.");
            try {
                App.cambiarVista("InicioSesion");
            } catch (IOException ex) {
                modulos.FuncionesGraficas.warning(
                    "Error", ex.getMessage());
            }
            return;
        }

        lblNombre.setText(s.getNombre());
        lblQrCode.setText(s.getQrCode());
        lblEstadoAcceso.setText(s.accederQR() ? "ACCESO PERMITIDO" : "ACCESO DENEGADO");
        if (s.accederQR()) {
            lblEstadoAcceso.getStyleClass().add("acceso-ok");
        } else {
            lblEstadoAcceso.getStyleClass().add("acceso-ko");
        }

        // Generar QR temporal y mostrarlo
        try {
            String home = System.getProperty("java.io.tmpdir");
            String ruta = home + File.separator + "gympjv_qr_" + s.getId() + ".png";
            Path generado = QRUtil.generarQRSocio(s.getId(), ruta);
            if (generado != null) {
                imgQR.setImage(new Image(generado.toUri().toString()));
            }
        } catch (Exception ignored) {
            // Si la generacion falla, simplemente no mostramos imagen
        }
    }
}
