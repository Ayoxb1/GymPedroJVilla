package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.ItemVenta;
import com.gimnasio.gympjv.clases.Producto;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Venta;
import com.gimnasio.gympjv.clases.enums.MetodoPago;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TiendaController {

    @FXML private TilePane panelProductos;

    @FXML private TableView<ItemVenta>  tablaCarrito;
    @FXML private TableColumn<ItemVenta, String>  colItemProd;
    @FXML private TableColumn<ItemVenta, Integer> colItemCant;
    @FXML private TableColumn<ItemVenta, Double>  colItemImp;

    @FXML private Label            lblTotal;
    @FXML private ComboBox<String> cmbMetodoPago;

    private Venta ventaActual;
    private ObservableList<ItemVenta> carritoItems;

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
        // Columnas carrito
        colItemProd.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getProducto().getNombre()));
        colItemCant.setCellValueFactory(
                new PropertyValueFactory<>("cantidad"));
        colItemImp.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        d.getValue().calcularImporte()));

        // ComboBox metodo de pago
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
                "EFECTIVO", "TARJETA", "TRANSFERENCIA"));
        cmbMetodoPago.getSelectionModel().selectFirst();

        nuevaVenta();
        poblarBotones();
    }

    private void poblarBotones() {
        panelProductos.getChildren().clear();
        List<Producto> productos =
                Repositorio.getInstancia().getGestorStock().getProductos();
        for (Producto p : productos) {
            String etiqueta = p.getNombre()
                    + "\n"
                    + String.format("%.2f EUR", p.getPrecioVenta())
                    + "\nStock: " + p.getStock();
            Button btn = new Button(etiqueta);
            btn.getStyleClass().add("btn-kiosco");
            btn.setOnAction(ev -> onAnadirKiosco(p));
            panelProductos.getChildren().add(btn);
        }
    }

    private void onAnadirKiosco(Producto p) {
        if (p.getStock() > 0) {
            ItemVenta item = new ItemVenta(p, 1);
            ventaActual.agregarItem(item);
            carritoItems.add(item);
            actualizarTotal();
        } else {
            FuncionesGraficas.warning(
                    "Sin stock",
                    p.getNombre() + " sin stock disponible.");
        }
    }

    private void nuevaVenta() {
        String idV = "VEN-"
                + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        ventaActual = new Venta(idV, null, MetodoPago.EFECTIVO);
        carritoItems = FXCollections.observableArrayList();
        tablaCarrito.setItems(carritoItems);
        actualizarTotal();
    }

    private void actualizarTotal() {
        double desc = ventaActual.getDescuento();
        if (desc > 0.001) {
            lblTotal.setText(String.format(
                    "Total: %.2f EUR  (dto: -%.2f EUR)",
                    ventaActual.getTotal(), desc));
        } else {
            lblTotal.setText(String.format(
                    "Total: %.2f EUR", ventaActual.getTotal()));
        }
    }

    @FXML
    private void onQuitarItem() {
        ItemVenta sel = tablaCarrito.getSelectionModel().getSelectedItem();
        if (sel == null) {
            FuncionesGraficas.warning(
                    "Quitar", "Selecciona un item del carrito primero.");
            return;
        }
        ventaActual.getItems().remove(sel);
        carritoItems.remove(sel);
        ventaActual.calcularTotal();
        actualizarTotal();
    }

    @FXML
    private void onAplicarDescuento() {
        List<Socio> socios = Repositorio.getInstancia().getSocios();
        if (socios.isEmpty()) {
            FuncionesGraficas.warning("Descuento", "No hay socios registrados.");
            return;
        }
        String[] nSocios = socios.stream()
                .map(Socio::getNombre).toArray(String[]::new);
        int idx = FuncionesGraficas.menuDesplegable(
                "Descuento socio",
                "Selecciona el socio (Cancelar = sin descuento):",
                nSocios);
        if (idx < 0) {
            ventaActual.aplicarDescuento(null);
        } else {
            ventaActual.aplicarDescuento(socios.get(idx));
        }
        actualizarTotal();
    }

    @FXML
    private void onConfirmar() {
        if (ventaActual.getItems().isEmpty()) {
            FuncionesGraficas.warning("Confirmar", "El carrito esta vacio.");
            return;
        }
        String seleccion = cmbMetodoPago.getValue();
        ventaActual.setMetodoPago(
                MetodoPago.valueOf(seleccion != null ? seleccion : "EFECTIVO"));

        if (!ventaActual.confirmar()) {
            FuncionesGraficas.warning(
                    "Stock insuficiente",
                    "Algun producto no tiene stock suficiente.");
            return;
        }
        Repositorio.getInstancia().getVentas().add(ventaActual);
        poblarBotones();
        FuncionesGraficas.mostrarDatos("Ticket", ventaActual.generarTicket());
        nuevaVenta();
    }
}
