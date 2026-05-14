package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Mensualidad;
import com.gimnasio.gympjv.clases.Suscripcion;
import com.gimnasio.gympjv.clases.enums.EstadoPago;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Vista de cuotas/suscripciones para el administrador.
 * Permite marcar suscripciones como pagadas (genera Mensualidad).
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class CuotasController {

    // ── Variables estaticas ────────────────────────────────────────────────
    private static Statement insSQL = null;
    private static Connection c     = null;

    // ── Campos FXML ───────────────────────────────────────────────────────
    @FXML private TableView<Suscripcion>           tabla;
    @FXML private TableColumn<Suscripcion, String> colId;
    @FXML private TableColumn<Suscripcion, String> colSocio;
    @FXML private TableColumn<Suscripcion, String> colTipo;
    @FXML private TableColumn<Suscripcion, String> colImporte;
    @FXML private TableColumn<Suscripcion, String> colFechaFin;
    @FXML private TableColumn<Suscripcion, String> colEstado;
    @FXML private Label campoMensaje;

    // ── initialize ────────────────────────────────────────────────────────
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
        c = ConexionBBDD.getConexion();
        if (c != null) {
            try {
                insSQL = c.createStatement();
            } catch (SQLException ex) {
                insSQL = null;
            }
        }
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getIdSuscripcion()));
        colSocio.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getSocio().getNombre()));
        colTipo.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getTipoSuscripcion()));
        colImporte.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                String.format("%.2f EUR",
                    cd.getValue().getImporteMensual())));
        colFechaFin.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getFechaFin().toString()));
        colEstado.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getEstado().name()));
    }

    private void cargarDatos() {
        ObservableList<Suscripcion> datos =
            FXCollections.observableArrayList(
                Repositorio.getInstancia()
                    .getSuscripciones());
        tabla.setItems(datos);
    }

    // ── Cobrar cuota ───────────────────────────────────────────────────────
    @FXML
    private void onCobrarCuota(ActionEvent e) {
        Suscripcion sus =
            tabla.getSelectionModel().getSelectedItem();
        String msg = "";
        if (sus == null) {
            msg = "Selecciona una suscripcion.";
        } else {
            String idMen = "MEN-"
                + UUID.randomUUID()
                       .toString()
                       .substring(0, 5)
                       .toUpperCase();
            LocalDate hoy = LocalDate.now();
            Mensualidad men = new Mensualidad(
                idMen, sus,
                sus.getImporteMensual(), hoy);
            men.marcarPagada();
            sus.anadirMensualidad(men);
            persistirMensualidad(idMen, sus, hoy);
            cargarDatos();
            msg = "Cuota " + idMen
                + " cobrada para "
                + sus.getSocio().getNombre() + ".";
        }
        campoMensaje.setText(msg);
    }

    private void persistirMensualidad(String idMen,
            Suscripcion sus, LocalDate hoy) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "INSERT INTO mensualidades VALUES ("
                    + "'" + idMen + "',"
                    + "'" + sus.getIdSuscripcion() + "',"
                    + sus.getImporteMensual() + ","
                    + "'" + hoy + "',"
                    + "'" + EstadoPago.PAGADA.name()
                    + "')");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se persistio la mensualidad: "
                    + ex.getMessage());
            }
        }
    }
}
