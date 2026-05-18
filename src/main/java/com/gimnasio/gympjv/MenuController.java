package com.gimnasio.gympjv;

import com.gimnasio.gympjv.App;
import javafx.geometry.Insets;
import com.gimnasio.gympjv.clases.UsuarioSistema;
import com.gimnasio.gympjv.clases.enums.RolUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import modulos.FuncionesGraficas;

import java.io.IOException;

/**
 * Menú principal del admin/monitor. Cada botón carga su vista en el centro.
 */
public class MenuController {

    @FXML private BorderPane root;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblFecha;
    @FXML private Button btnSocios;
    @FXML private Button btnEmpleados;
    @FXML private Button btnSuscripciones;
    @FXML private Button btnClases;
    @FXML private Button btnSesionesPersonales;
    @FXML private Button btnReservas;
    @FXML private Button btnPeluqueria;
    @FXML private Button btnTienda;
    @FXML private Button btnAsistencia;
    @FXML private Button btnStock;
    @FXML private Button btnReportes;
    @FXML private Button btnAccesoQR;
    @FXML private Button btnCalculadora;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnBackup;
    @FXML private Button btnCerrarSesion;

    @FXML
    private void initialize() {
        UsuarioSistema u = SesionActual.getUsuario();
        if (u == null) {
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
        if (u != null) {
            lblUsuario.setText(u.getNombre());
            lblRol.setText(u.getRol().name());
        }
        if (lblFecha != null) {
            lblFecha.setText(java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        // el monitor no necesita ver empleados, reportes ni stock
        if (u != null && u.getRol() == RolUsuario.MONITOR) {
            btnEmpleados.setVisible(false);
            btnEmpleados.setManaged(false);
            btnReportes.setVisible(false);
            btnReportes.setManaged(false);
            btnStock.setVisible(false);
            btnStock.setManaged(false);
        }
        cargarDashboard();
    }

    private void cargarDashboard() {
        Repositorio r = Repositorio.getInstancia();

        javafx.scene.layout.VBox panel = new javafx.scene.layout.VBox(20);
        panel.getStyleClass().add("dashboard-info-box");
        panel.setPadding(new javafx.geometry.Insets(36, 40, 36, 40));

        javafx.scene.control.Label titulo = new javafx.scene.control.Label("Panel de control");
        titulo.getStyleClass().add("titulo-vista");

        // stats del dashboard
        String[][] stats = {
            { "Socios registrados",    String.valueOf(r.getSocios().size()) },
            { "Socios activos",        String.valueOf(r.getReportes().sociosActivos()) },
            { "Clases programadas",    String.valueOf(r.getClases().size()) },
            { "Productos en tienda",   String.valueOf(r.getGestorStock().getProductos().size()) },
            { "Alertas de stock",      String.valueOf(r.getGestorStock().checkAlertas().size()) },
            { "Ingresos este mes",     String.format("%.2f EUR", r.getReportes().ingresosMensuales()) },
            { "Ingresos hoy",          String.format("%.2f EUR", r.getReportes().ingresosDiarios()) },
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

        panel.getChildren().addAll(titulo, grid);
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

    @FXML private void onSocios(ActionEvent e)             { cargarVista("Socios"); }
    @FXML private void onEmpleados(ActionEvent e)          { cargarVista("Empleados"); }
    @FXML private void onSuscripciones(ActionEvent e)      { cargarVista("Suscripciones"); }
    @FXML private void onCuotas(ActionEvent e)             { cargarVista("Cuotas"); }
    @FXML private void onClases(ActionEvent e)             { cargarVista("Clases"); }
    @FXML private void onAsistencia(ActionEvent e)         { cargarVista("Asistencia"); }
    @FXML private void onSesionesPersonales(ActionEvent e) { cargarVista("SesionesPersonales"); }
    @FXML private void onReservas(ActionEvent e)           { cargarVista("Reservas"); }
    @FXML private void onPeluqueria(ActionEvent e)         { cargarVista("Peluqueria"); }
    @FXML private void onTienda(ActionEvent e)             { cargarVista("Tienda"); }
    @FXML private void onStock(ActionEvent e)              { cargarVista("Stock"); }
    @FXML private void onReportes(ActionEvent e)           { cargarVista("Reportes"); }
    @FXML private void onAccesoQR(ActionEvent e)           { cargarVista("AccesoQR"); }
    @FXML private void onCalculadora(ActionEvent e)        { cargarVista("Calculadora"); }
    @FXML private void onConfiguracion(ActionEvent e)      { cargarVista("Configuracion"); }
    @FXML private void onBackup(ActionEvent e)             { cargarVista("Backup"); }

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
