package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Asistencia;
import com.gimnasio.gympjv.clases.ClaseGrupal;
import com.gimnasio.gympjv.clases.Socio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AsistenciaController {

    @FXML private ComboBox<ClaseGrupal> cmbClase;
    @FXML private TableView<Asistencia> tablaAsistencia;
    @FXML private TableColumn<Asistencia, String> colId;
    @FXML private TableColumn<Asistencia, String> colSocio;
    @FXML private TableColumn<Asistencia, String> colClase;
    @FXML private TableColumn<Asistencia, String> colFecha;
    @FXML private TableColumn<Asistencia, String> colEstado;

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
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIdAsistencia()));
        colSocio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSocio().getNombre()));
        colClase.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getClase().getNombre()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstadoTexto()));

        cmbClase.setItems(FXCollections.observableArrayList(Repositorio.getInstancia().getClases()));
        recargar(null);
    }

    @FXML
    private void onFiltrar() {
        recargar(cmbClase.getValue());
    }

    private void recargar(ClaseGrupal filtro) {
        List<Asistencia> todas = Repositorio.getInstancia().getAsistencias();
        ObservableList<Asistencia> datos = FXCollections.observableArrayList();
        for (Asistencia a : todas) {
            if (filtro == null || a.getClase().equals(filtro)) datos.add(a);
        }
        tablaAsistencia.setItems(datos);
    }

    @FXML
    private void onNuevaAsistencia() {
        // Seleccionar clase
        List<ClaseGrupal> clases = Repositorio.getInstancia().getClases();
        if (clases.isEmpty()) {
            FuncionesGraficas.warning("Asistencia", "No hay clases registradas.");
            return;
        }
        String[] nombresClases = clases.stream().map(ClaseGrupal::getNombre).toArray(String[]::new);
        int iClase = FuncionesGraficas.menuDesplegable("Nueva asistencia", "Selecciona la clase:", nombresClases);
        if (iClase < 0) return;
        ClaseGrupal clase = clases.get(iClase);

        // Seleccionar socio
        List<Socio> socios = Repositorio.getInstancia().getSocios();
        if (socios.isEmpty()) {
            FuncionesGraficas.warning("Asistencia", "No hay socios registrados.");
            return;
        }
        String[] nombresSocios = socios.stream().map(Socio::getNombre).toArray(String[]::new);
        int iSocio = FuncionesGraficas.menuDesplegable("Nueva asistencia", "Selecciona el socio:", nombresSocios);
        if (iSocio < 0) return;
        Socio socio = socios.get(iSocio);

        String id = "ASI-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Asistencia nueva = new Asistencia(id, socio, clase, LocalDateTime.now());
        Repositorio.getInstancia().getAsistencias().add(nueva);
        recargar(cmbClase.getValue());
        FuncionesGraficas.mostrarMensaje("Asistencia",
                "Asistencia registrada:\n" + socio.getNombre() + " - " + clase.getNombre(), 1);
    }

    @FXML
    private void onMarcarPresente() {
        Asistencia a = tablaAsistencia.getSelectionModel().getSelectedItem();
        if (a == null) {
            FuncionesGraficas.warning("Asistencia", "Selecciona un registro.");
            return;
        }
        a.marcarPresente();
        tablaAsistencia.refresh();
    }

    @FXML
    private void onMarcarAusente() {
        Asistencia a = tablaAsistencia.getSelectionModel().getSelectedItem();
        if (a == null) {
            FuncionesGraficas.warning("Asistencia", "Selecciona un registro.");
            return;
        }
        a.marcarAusente();
        tablaAsistencia.refresh();
    }

    @FXML
    private void onEliminar() {
        Asistencia a = tablaAsistencia.getSelectionModel().getSelectedItem();
        if (a == null) {
            FuncionesGraficas.warning("Asistencia", "Selecciona un registro.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool("Eliminar",
                "Eliminar el registro de " + a.getSocio().getNombre() + "?");
        if (ok) {
            Repositorio.getInstancia().getAsistencias().remove(a);
            recargar(cmbClase.getValue());
        }
    }
}
