package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.PlanDieta;
import com.gimnasio.gympjv.clases.Socio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Vista del plan de dieta del socio en sesion.
 */
public class MiDietaController {

    public static class Comida {
        private final String dia;
        private final String descripcion;
        public Comida(String dia, String descripcion) { this.dia = dia; this.descripcion = descripcion; }
        public String getDia() { return dia; }
        public String getDescripcion() { return descripcion; }
    }

    @FXML private Label lblIdPlan;
    @FXML private Label lblMonitor;
    @FXML private Label lblCalorias;
    @FXML private Label lblSinPlan;

    @FXML private TableView<Comida> tablaComidas;
    @FXML private TableColumn<Comida, String> colDia;
    @FXML private TableColumn<Comida, String> colDescripcion;

    @FXML
    private void initialize() {
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        Socio s = SesionActual.getSocio();
        if (s == null) {
            modulos.FuncionesGraficas.warning(
                "Sesion",
                "No hay sesion activa.");
            try {
                App.cambiarVista("InicioSesion");
            } catch (IOException ex) {
                modulos.FuncionesGraficas.warning(
                    "Error", ex.getMessage());
            }
            return;
        }
        PlanDieta plan = s.getPlanDieta();
        if (plan == null) {
            lblSinPlan.setText("Aun no tienes un plan de dieta asignado.");
            lblSinPlan.setVisible(true);
            tablaComidas.setVisible(false);
            return;
        }

        lblSinPlan.setVisible(false);
        lblIdPlan.setText(plan.getIdPlan());
        lblMonitor.setText(plan.getMonitor() != null ? plan.getMonitor().getNombre() : "-");
        lblCalorias.setText(plan.calcularCalorias() + " kcal/dia");

        List<Comida> comidas = new ArrayList<>();
        for (Map.Entry<String, String> e : plan.getComidas().entrySet()) {
            comidas.add(new Comida(e.getKey(), e.getValue()));
        }
        tablaComidas.setItems(FXCollections.observableArrayList(comidas));
    }
}
