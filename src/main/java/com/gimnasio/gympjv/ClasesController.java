package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.ClaseGrupal;
import com.gimnasio.gympjv.clases.Monitor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador de clases grupales.
 * Muestra tabla de clases + vista de calendario semanal.
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class ClasesController {

    private static final DateTimeFormatter FMT_HORA =
        DateTimeFormatter.ofPattern("HH:mm");

    @FXML private TableView<ClaseGrupal>           tablaClases;
    @FXML private TableColumn<ClaseGrupal, String> colId;
    @FXML private TableColumn<ClaseGrupal, String> colNombre;
    @FXML private TableColumn<ClaseGrupal, String> colFecha;
    @FXML private TableColumn<ClaseGrupal, String> colMonitor;
    @FXML private TableColumn<ClaseGrupal, String> colOcupacion;
    @FXML private TableColumn<ClaseGrupal, String> colAforo;
    @FXML private GridPane                          calendarioSemana;

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
        colId.setCellValueFactory(
            new PropertyValueFactory<>("idClase"));
        colNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre"));
        colFecha.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getFecha().format(
                    DateTimeFormatter.ofPattern(
                        "EEE dd/MM HH:mm"))));
        colMonitor.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getMonitor().getNombre()));
        colOcupacion.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                String.valueOf(
                    cd.getValue().getOcupacionActual())));
        colAforo.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                String.valueOf(
                    cd.getValue().getAforoMaximo())));
        recargar();
    }

    private void recargar() {
        tablaClases.setItems(
            FXCollections.observableArrayList(
                Repositorio.getInstancia().getClases()));
        poblarCalendario();
    }

    // ── Calendario semanal ─────────────────────────────────────────────────
    private void poblarCalendario() {
        calendarioSemana.getChildren().clear();

        String[] dias = {
            "LUN", "MAR", "MIE",
            "JUE", "VIE", "SAB", "DOM"
        };

        for (int i = 0; i < dias.length; i++) {
            Label cabecera = new Label(dias[i]);
            cabecera.getStyleClass().add("cal-header");
            cabecera.setMaxWidth(Double.MAX_VALUE);
            GridPane.setColumnIndex(cabecera, i);
            GridPane.setRowIndex(cabecera, 0);
            calendarioSemana.getChildren().add(cabecera);
        }

        Map<Integer, List<ClaseGrupal>> porDia =
            new HashMap<>();
        for (ClaseGrupal c
                : Repositorio.getInstancia().getClases()) {
            int col = c.getFecha().getDayOfWeek()
                .getValue() - 1;
            if (!porDia.containsKey(col)) {
                porDia.put(col, new ArrayList<>());
            }
            porDia.get(col).add(c);
        }

        for (Map.Entry<Integer, List<ClaseGrupal>> entry
                : porDia.entrySet()) {
            int col = entry.getKey();
            int row = 1;
            for (ClaseGrupal c : entry.getValue()) {
                String txt = c.getNombre()
                    + "\n"
                    + c.getFecha().format(FMT_HORA)
                    + "\n"
                    + c.getOcupacionActual()
                    + "/" + c.getAforoMaximo();
                Label lbl = new Label(txt);
                lbl.getStyleClass().add("cal-clase");
                lbl.setWrapText(true);
                lbl.setMaxWidth(Double.MAX_VALUE);
                VBox celda = new VBox(lbl);
                celda.setPadding(
                    new Insets(4, 4, 4, 4));
                celda.getStyleClass().add("cal-celda");
                GridPane.setColumnIndex(celda, col);
                GridPane.setRowIndex(celda, row);
                calendarioSemana.getChildren().add(celda);
                row++;
            }
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────
    @FXML
    private void onNueva() {
        String nombre = FuncionesGraficas.pedirDatos(
            "Nueva clase", "Nombre de la clase:");
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        int aforo = FuncionesGraficas.pedirEntero(
            "Nueva clase", "Aforo maximo:");
        if (aforo <= 0) {
            return;
        }

        List<Monitor> monitores =
            Repositorio.getInstancia().getMonitores();
        if (monitores.isEmpty()) {
            FuncionesGraficas.warning(
                "Nueva clase",
                "No hay monitores disponibles.");
            return;
        }
        String[] nombresMon = monitores.stream()
            .map(Monitor::getNombre)
            .toArray(String[]::new);
        int idx = FuncionesGraficas.menuDesplegable(
            "Asignar monitor",
            "Selecciona el monitor de la clase:",
            nombresMon);
        if (idx < 0 || idx >= monitores.size()) {
            return;
        }

        Monitor m = monitores.get(idx);
        String idClase = "CLA-"
            + UUID.randomUUID()
                   .toString()
                   .substring(0, 5)
                   .toUpperCase();
        ClaseGrupal nueva = new ClaseGrupal(
            idClase, nombre,
            LocalDateTime.now().plusDays(1), m, aforo);
        Repositorio.getInstancia().getClases().add(nueva);
        m.anadirClase(nueva);
        recargar();
        FuncionesGraficas.mostrarMensaje(
            "Clase creada",
            "Clase " + nombre + " creada.", 1);
    }

    @FXML
    private void onCancelarClase() {
        ClaseGrupal c =
            tablaClases.getSelectionModel()
                        .getSelectedItem();
        if (c == null) {
            FuncionesGraficas.warning(
                "Cancelar clase",
                "Selecciona una clase.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool(
            "Cancelar clase",
            "Eliminar la clase " + c.getNombre() + "?");
        if (ok) {
            Repositorio.getInstancia()
                .getClases().remove(c);
            recargar();
        }
    }
}
