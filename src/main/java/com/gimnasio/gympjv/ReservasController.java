package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.ClaseGrupal;
import com.gimnasio.gympjv.clases.Reserva;
import com.gimnasio.gympjv.clases.Socio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservasController {

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colId;
    @FXML private TableColumn<Reserva, String> colSocio;
    @FXML private TableColumn<Reserva, String> colClase;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, String> colEstado;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colSocio.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getSocio().getNombre()));
        colClase.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getServicio().getNombre()));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        recargar();
    }

    private void recargar() {
        tablaReservas.setItems(FXCollections.observableArrayList(
                Repositorio.getInstancia().getReservas()));
    }

    @FXML
    private void onNueva() {
        List<Socio> socios = Repositorio.getInstancia().getSocios();
        List<ClaseGrupal> clases = Repositorio.getInstancia().getClases();
        if (socios.isEmpty() || clases.isEmpty()) {
            FuncionesGraficas.warning("Nueva reserva", "Faltan socios o clases.");
            return;
        }
        String[] nSocios = socios.stream().map(Socio::getNombre).toArray(String[]::new);
        int iS = FuncionesGraficas.menuDesplegable("Nueva reserva", "Selecciona el socio:", nSocios);
        if (iS < 0) return;
        String[] nClases = clases.stream().map(ClaseGrupal::getNombre).toArray(String[]::new);
        int iC = FuncionesGraficas.menuDesplegable("Nueva reserva", "Selecciona la clase:", nClases);
        if (iC < 0) return;

        Socio s = socios.get(iS);
        ClaseGrupal c = clases.get(iC);
        if (!s.tieneSesiones()) {
            FuncionesGraficas.warning("Reserva no permitida",
                    "El socio no tiene saldo de sesiones disponibles.");
            return;
        }
        if (c.estaLlena()) {
            FuncionesGraficas.warning("Reserva no permitida",
                    "La clase esta completa.");
            return;
        }

        String id = "RES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Reserva r = new Reserva(id, s, c, LocalDateTime.now());
        boolean ok = r.confirmar();
        if (ok) {
            Repositorio.getInstancia().getReservas().add(r);
            s.anadirReserva(r);
            recargar();
            FuncionesGraficas.mostrarMensaje("Reserva", "Reserva " + id + " confirmada.", 1);
        } else {
            FuncionesGraficas.warning("Reserva", "No se ha podido confirmar.");
        }
    }

    @FXML
    private void onCancelar() {
        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();
        if (r == null) {
            FuncionesGraficas.warning("Cancelar", "Selecciona una reserva.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool("Cancelar reserva", "Confirmas la cancelacion?");
        if (ok) {
            r.cancelar();
            tablaReservas.refresh();
        }
    }
}
