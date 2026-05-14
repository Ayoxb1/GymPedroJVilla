package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Venta;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Muestra el historial de ventas (compras) del socio actual.
 */
public class MiHistorialController {

    @FXML private TableView<Venta> tablaHistorial;
    @FXML private TableColumn<Venta, String> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, String> colMetodoPago;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));

        Socio s = SesionActual.getSocio();
        if (s != null) {
            tablaHistorial.setItems(FXCollections.observableArrayList(s.getHistorialVentas()));
        }
    }
}
