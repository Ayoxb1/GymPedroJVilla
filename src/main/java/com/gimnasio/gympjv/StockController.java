package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Producto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.util.UUID;

public class StockController {

    @FXML private TableView<Producto> tablaStock;
    @FXML private TableColumn<Producto, String> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Integer> colMinimo;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        recargar();
    }

    private void recargar() {
        tablaStock.setItems(FXCollections.observableArrayList(
                Repositorio.getInstancia().getGestorStock().getProductos()));
    }

    @FXML
    private void onNuevo() {
        String nombre = FuncionesGraficas.pedirDatos("Nuevo producto", "Nombre:");
        if (nombre == null || nombre.isBlank()) return;

        String precioStr = FuncionesGraficas.pedirDatos("Nuevo producto", "Precio venta (EUR):");
        if (precioStr == null) return;
        double precio;
        try { precio = Double.parseDouble(precioStr.replace(",", ".")); }
        catch (NumberFormatException ex) { FuncionesGraficas.warning("Error", "Precio invalido."); return; }

        String stockStr = FuncionesGraficas.pedirDatos("Nuevo producto", "Stock inicial:");
        if (stockStr == null) return;
        int stock;
        try { stock = Integer.parseInt(stockStr.trim()); }
        catch (NumberFormatException ex) { FuncionesGraficas.warning("Error", "Stock invalido."); return; }

        String minimoStr = FuncionesGraficas.pedirDatos("Nuevo producto", "Stock minimo de alerta:");
        if (minimoStr == null) return;
        int minimo;
        try { minimo = Integer.parseInt(minimoStr.trim()); }
        catch (NumberFormatException ex) { FuncionesGraficas.warning("Error", "Minimo invalido."); return; }

        String id = "PROD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Producto p = new Producto(id, nombre, precio, stock, minimo);
        Repositorio.getInstancia().getGestorStock().anadirProducto(p);
        recargar();
        FuncionesGraficas.mostrarMensaje("Producto", "Producto anadido: " + nombre, 1);
    }

    @FXML
    private void onReponer() {
        Producto p = tablaStock.getSelectionModel().getSelectedItem();
        if (p == null) {
            FuncionesGraficas.warning("Reponer", "Selecciona un producto.");
            return;
        }
        String uStr = FuncionesGraficas.pedirDatos("Reponer",
                "Unidades a reponer (stock actual: " + p.getStock() + "):");
        if (uStr == null) return;
        try {
            int unidades = Integer.parseInt(uStr.trim());
            if (unidades > 0) {
                p.reponer(unidades);
                tablaStock.refresh();
                FuncionesGraficas.mostrarMensaje("Reponer",
                        p.getNombre() + ": nuevo stock = " + p.getStock(), 1);
            }
        } catch (NumberFormatException ex) {
            FuncionesGraficas.warning("Error", "Numero invalido.");
        }
    }

    @FXML
    private void onEliminar() {
        Producto p = tablaStock.getSelectionModel().getSelectedItem();
        if (p == null) {
            FuncionesGraficas.warning("Eliminar", "Selecciona un producto.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool("Eliminar producto",
                "Eliminar definitivamente '" + p.getNombre() + "'?");
        if (ok) {
            Repositorio.getInstancia().getGestorStock().getProductos().remove(p);
            recargar();
        }
    }

    @FXML
    private void onAlertas() {
        var alertas = Repositorio.getInstancia().getGestorStock().checkAlertas();
        if (alertas.isEmpty()) {
            FuncionesGraficas.mostrarMensaje("Stock OK", "Ningun producto bajo el minimo.", 1);
            return;
        }
        StringBuilder sb = new StringBuilder("Productos bajo minimo:\n\n");
        for (Producto p : alertas) {
            sb.append("- ").append(p.getNombre())
              .append(" (stock: ").append(p.getStock())
              .append(", min: ").append(p.getStockMinimo()).append(")\n");
        }
        FuncionesGraficas.mostrarDatos("Alertas de stock", sb.toString());
    }
}
