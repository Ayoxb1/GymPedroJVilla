package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Mensualidad;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Suscripcion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Muestra la suscripcion del socio y el estado de su mensualidad.
 */
public class MiSuscripcionController {

    @FXML private Label lblIdSus;
    @FXML private Label lblTipo;
    @FXML private Label lblImporte;
    @FXML private Label lblInicio;
    @FXML private Label lblFin;
    @FXML private Label lblEstado;
    @FXML private Label lblSaldoClases;
    @FXML private Label lblMensualidadEstado;

    @FXML
    private void initialize() {
        Socio s = SesionActual.getSocio();
        if (s == null) return;
        Suscripcion sus = s.getSuscripcion();
        if (sus == null) {
            lblIdSus.setText("(sin suscripcion)");
            return;
        }
        lblIdSus.setText(sus.getIdSuscripcion());
        lblTipo.setText(sus.getTipoSuscripcion());
        lblImporte.setText(String.format("%.2f EUR", sus.getImporteMensual()));
        lblInicio.setText(String.valueOf(sus.getFechaInicio()));
        lblFin.setText(String.valueOf(sus.getFechaFin()));
        lblEstado.setText(sus.getEstado().name());
        lblSaldoClases.setText(String.valueOf(sus.calcularSaldoClases()));

        // Estado de la mensualidad mas reciente
        if (!sus.getMensualidades().isEmpty()) {
            Mensualidad ult = sus.getMensualidades().get(sus.getMensualidades().size() - 1);
            lblMensualidadEstado.setText(ult.getEstado().name());
        } else {
            lblMensualidadEstado.setText("(sin mensualidades)");
        }
    }
}
