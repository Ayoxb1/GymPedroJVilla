package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Reserva;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.enums.EstadoReserva;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modulos.FuncionesGraficas;

import java.io.IOException;

/**
 * Vista del socio: muestra sus reservas y permite cancelar la seleccionada.
 */
public class MisReservasController {

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colId;
    @FXML private TableColumn<Reserva, String> colClase;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, String> colEstado;

    @FXML
    private void initialize() {
        if (SesionActual.getSocio() == null) {
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
        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colClase.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getServicio().getNombre()));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        recargar();
    }

    private void recargar() {
        Socio s = SesionActual.getSocio();
        if (s != null) {
            tablaReservas.setItems(FXCollections.observableArrayList(s.getReservas()));
        }
    }

    @FXML
    private void onCancelar(ActionEvent e) {
        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();
        if (r == null) {
            FuncionesGraficas.warning("Cancelar reserva", "Selecciona una reserva.");
            return;
        }
        if (r.getEstado() == EstadoReserva.CANCELADA) {
            FuncionesGraficas.warning("Cancelar reserva", "Esta reserva ya esta cancelada.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool("Cancelar reserva",
                "Quieres cancelar la reserva en " + r.getServicio().getNombre() + "?");
        if (ok) {
            r.cancelar();
            recargar();
            FuncionesGraficas.mostrarMensaje("Reserva", "Reserva cancelada.", 1);
        }
    }
}
