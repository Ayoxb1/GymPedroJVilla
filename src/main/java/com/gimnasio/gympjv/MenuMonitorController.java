package com.gimnasio.gympjv;

import com.gimnasio.gympjv.App;
import com.gimnasio.gympjv.clases.Monitor;
import com.gimnasio.gympjv.clases.UsuarioSistema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import modulos.FuncionesGraficas;

import java.io.IOException;

public class MenuMonitorController {

    // ── Monitor de sesion (patron datosEntrada) ────────────────────────────
    private static Monitor monitorActual = null;

    /** Llamar desde InicioSesionController antes de cargar la vista. */
    public static void datosEntrada(Monitor m) {
        monitorActual = m;
    }

    @FXML private BorderPane root;
    @FXML private Label lblUsuario;
    @FXML private Label lblEspecialidad;
    @FXML private Label lblFecha;

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
        Monitor m = monitorActual;
        if (m == null) {
            UsuarioSistema u = SesionActual.getUsuario();
            if (u instanceof Monitor) {
                m = (Monitor) u;
            }
        }
        if (m != null) {
            lblUsuario.setText(m.getNombre());
            lblEspecialidad.setText(
                "Especialidad: "
                + m.getEspecialidad());
        }
        if (lblFecha != null) {
            lblFecha.setText(java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        cargarDashboard();
    }

    private void cargarDashboard() {
        Monitor m = monitorActual;
        if (m == null) {
            UsuarioSistema u = SesionActual.getUsuario();
            if (u instanceof Monitor) {
                m = (Monitor) u;
            }
        }
        final Monitor mon = m;

        javafx.scene.layout.VBox panel =
            new javafx.scene.layout.VBox(20);
        panel.getStyleClass().add("dashboard-info-box");
        panel.setPadding(
            new javafx.geometry.Insets(36, 40, 36, 40));

        javafx.scene.control.Label titulo =
            new javafx.scene.control.Label(
                "Bienvenido, "
                + (mon != null ? mon.getNombre()
                               : "Monitor"));
        titulo.getStyleClass().add("titulo-vista");

        String especialidad = (mon != null)
                ? "Especialidad:  " + mon.getEspecialidad()
                : "";

        Repositorio r = Repositorio.getInstancia();
        int misClases = (mon != null)
            ? mon.getClasesAsignadas().size() : 0;
        int misPlanes = (mon != null)
            ? mon.getPlanesDieta().size() : 0;
        int totalSocios = r.getSocios().size();

        String[][] stats = {
            { "Clases asignadas",   String.valueOf(misClases) },
            { "Planes de dieta",    String.valueOf(misPlanes) },
            { "Total socios",       String.valueOf(totalSocios) },
        };

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(40);
        grid.setVgap(12);
        for (int i = 0; i < stats.length; i++) {
            javafx.scene.control.Label lbl = new javafx.scene.control.Label(stats[i][0]);
            lbl.getStyleClass().add("campo-titulo");
            javafx.scene.control.Label val = new javafx.scene.control.Label(stats[i][1]);
            val.getStyleClass().add("campo-valor-dorado");
            grid.add(lbl, 0, i);
            grid.add(val, 1, i);
        }

        javafx.scene.control.Label espLabel = new javafx.scene.control.Label(especialidad);
        espLabel.getStyleClass().add("campo-valor");

        panel.getChildren().addAll(titulo, espLabel, grid);
        root.setCenter(panel);
    }

    private void cargarVista(String nombre) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gimnasio/gympjv/fxml/" + nombre + ".fxml"));
            Parent vista = loader.load();
            root.setCenter(vista);
        } catch (IOException ex) {
            FuncionesGraficas.warning("Error", "No se pudo cargar " + nombre + ": " + ex.getMessage());
        }
    }

    @FXML private void onMisClases(ActionEvent e)           { cargarVista("Clases"); }
    @FXML private void onAsistencia(ActionEvent e)          { cargarVista("Asistencia"); }
    @FXML private void onMisSocios(ActionEvent e)           { cargarVista("Socios"); }
    @FXML private void onMisDietas(ActionEvent e)           { cargarVista("SesionesPersonales"); }
    @FXML private void onReservas(ActionEvent e)            { cargarVista("Reservas"); }
    @FXML private void onSesionesPersonales(ActionEvent e)  { cargarVista("SesionesPersonales"); }

    @FXML
    private void onCerrarSesion(ActionEvent e) {
        boolean confirmar = FuncionesGraficas.pedirBool("Cerrar sesion", "Salir de la sesion actual?");
        if (confirmar) {
            SesionActual.cerrar();
            try {
                App.cambiarVista("InicioSesion");
            } catch (IOException ex) {
                FuncionesGraficas.warning("Error", "No se pudo volver al login: " + ex.getMessage());
            }
        }
    }
}
