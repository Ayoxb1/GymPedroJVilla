package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Monitor;
import com.gimnasio.gympjv.clases.SesionPersonal;
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

public class SesionesPersonalesController {

    @FXML private TableView<SesionPersonal> tablaSesiones;
    @FXML private TableColumn<SesionPersonal, String> colId;
    @FXML private TableColumn<SesionPersonal, String> colSocio;
    @FXML private TableColumn<SesionPersonal, String> colEntrenador;
    @FXML private TableColumn<SesionPersonal, String> colFecha;
    @FXML private TableColumn<SesionPersonal, Integer> colDuracion;
    @FXML private TableColumn<SesionPersonal, String> colObjetivos;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idSesion"));
        colSocio.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getSocio().getNombre()));
        colEntrenador.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getEntrenador().getNombre()));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colObjetivos.setCellValueFactory(new PropertyValueFactory<>("objetivos"));
        recargar();
    }

    private void recargar() {
        tablaSesiones.setItems(FXCollections.observableArrayList(
                Repositorio.getInstancia().getSesionesPersonales()));
    }

    @FXML
    private void onNueva() {
        List<Socio> socios = Repositorio.getInstancia().getSocios();
        List<Monitor> monitores = Repositorio.getInstancia().getMonitores();
        if (socios.isEmpty() || monitores.isEmpty()) {
            FuncionesGraficas.warning("Nueva sesion", "Faltan socios o monitores.");
            return;
        }
        String[] nSocios = socios.stream().map(Socio::getNombre).toArray(String[]::new);
        int iS = FuncionesGraficas.menuDesplegable("Sesion personal", "Selecciona el socio:", nSocios);
        if (iS < 0) return;
        String[] nMon = monitores.stream().map(Monitor::getNombre).toArray(String[]::new);
        int iM = FuncionesGraficas.menuDesplegable("Sesion personal", "Selecciona el entrenador:", nMon);
        if (iM < 0) return;
        int duracion = FuncionesGraficas.pedirEntero("Sesion personal", "Duracion en minutos:");
        if (duracion <= 0) return;
        String objetivos = FuncionesGraficas.pedirDatos("Sesion personal", "Objetivos:");
        if (objetivos == null) return;

        String id = "SES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        SesionPersonal s = new SesionPersonal(id, socios.get(iS), monitores.get(iM),
                LocalDateTime.now().plusDays(1), duracion, objetivos, "MEDIA");
        Repositorio.getInstancia().getSesionesPersonales().add(s);
        recargar();
        FuncionesGraficas.mostrarMensaje("Sesion creada", "Sesion " + id + " programada.", 1);
    }

    @FXML
    private void onCancelar() {
        SesionPersonal s = tablaSesiones.getSelectionModel().getSelectedItem();
        if (s == null) {
            FuncionesGraficas.warning("Cancelar", "Selecciona una sesion.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool("Cancelar sesion", "Confirmas la cancelacion?");
        if (ok) {
            Repositorio.getInstancia().getSesionesPersonales().remove(s);
            recargar();
        }
    }
}
