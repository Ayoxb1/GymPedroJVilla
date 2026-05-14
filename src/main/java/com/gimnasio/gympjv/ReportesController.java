package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Producto;
import com.gimnasio.gympjv.clases.Reportes;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Controlador de reportes y estadisticas.
 * BarChart: ingresos por tipo de suscripcion (JDBC).
 * PieChart: distribucion de aforo por clase (JDBC).
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class ReportesController {

    @FXML private Label lblIngresosHoy;
    @FXML private Label lblIngresos;
    @FXML private Label lblTotal;
    @FXML private Label lblSocios;
    @FXML private Label lblTopProductos;
    @FXML private Label lblBajoStock;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart                 pieChart;

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
        actualizar();
    }

    @FXML
    private void onActualizar() {
        actualizar();
        FuncionesGraficas.mostrarMensaje(
            "Reportes", "Datos actualizados.", 1);
    }

    private void actualizar() {
        Reportes r = Repositorio.getInstancia().getReportes();

        lblIngresosHoy.setText(
            String.format("%.2f EUR", r.ingresosDiarios()));
        lblIngresos.setText(
            String.format("%.2f EUR", r.ingresosMensuales()));
        lblTotal.setText(
            String.format("%.2f EUR", r.ventasTotales()));
        lblSocios.setText(
            String.valueOf(r.sociosActivos()));

        actualizarGraficos(r);

        StringBuilder top = new StringBuilder();
        for (Producto p : r.topProductos()) {
            top.append("- ").append(p.getNombre())
               .append("\n");
        }
        String topTxt = top.toString();
        lblTopProductos.setText(
            topTxt.isEmpty() ? "Sin ventas" : topTxt);

        StringBuilder baj = new StringBuilder();
        for (Producto p : r.productosBajoStock()) {
            baj.append("- ").append(p.getNombre())
               .append(" (").append(p.getStock())
               .append(" uds)\n");
        }
        String bajTxt = baj.toString();
        lblBajoStock.setText(
            bajTxt.isEmpty() ? "Stock OK" : bajTxt);
    }

    /**
     * Puebla el BarChart (ingresos por tipo suscripcion)
     * y el PieChart (aforo por clase) con consultas JDBC.
     * Si no hay conexion, usa datos del Repositorio.
     */
    private void actualizarGraficos(Reportes r) {
        barChart.getData().clear();
        pieChart.getData().clear();

        XYChart.Series<String, Number> series =
            new XYChart.Series<>();
        series.setName("Ingresos EUR");

        boolean bbddOk = false;
        Connection con = ConexionBBDD.getConexion();

        if (con != null) {
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(
                    "SELECT tipo, SUM(precio)"
                    + " FROM suscripciones"
                    + " GROUP BY tipo"
                    + " ORDER BY tipo");
                while (rs.next()) {
                    String tipo = rs.getString(1);
                    double tot  = rs.getDouble(2);
                    series.getData().add(
                        new XYChart.Data<>(tipo, tot));
                }
                rs.close();

                ResultSet rc = con.createStatement()
                    .executeQuery(
                        "SELECT nombre, aforo_maximo"
                        + " FROM clases_grupales"
                        + " ORDER BY nombre");
                while (rc.next()) {
                    String nom  = rc.getString(1);
                    int    aforo = rc.getInt(2);
                    int    val   = (aforo > 0) ? aforo : 1;
                    pieChart.getData().add(
                        new PieChart.Data(nom, val));
                }
                rc.close();
                st.close();
                bbddOk = true;
            } catch (SQLException ex) {
                bbddOk = false;
            }
        }

        if (!bbddOk) {
            for (Map.Entry<String, Integer> e
                    : r.ocupacionClases().entrySet()) {
                int val = (e.getValue() > 0)
                    ? e.getValue() : 1;
                series.getData().add(
                    new XYChart.Data<>(e.getKey(), val));
                pieChart.getData().add(
                    new PieChart.Data(e.getKey(), val));
            }
        }

        barChart.getData().add(series);
    }
}
