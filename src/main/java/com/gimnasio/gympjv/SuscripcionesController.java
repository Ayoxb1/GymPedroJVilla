package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Mensualidad;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Suscripcion;
import com.gimnasio.gympjv.clases.enums.EstadoPago;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SuscripcionesController {

    @FXML private TableView<Suscripcion> tablaSuscripciones;
    @FXML private TableColumn<Suscripcion, String> colId;
    @FXML private TableColumn<Suscripcion, String> colTipo;
    @FXML private TableColumn<Suscripcion, Double> colImporte;
    @FXML private TableColumn<Suscripcion, String> colInicio;
    @FXML private TableColumn<Suscripcion, String> colFin;
    @FXML private TableColumn<Suscripcion, String> colEstado;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idSuscripcion"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoSuscripcion"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importeMensual"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaSuscripciones.setItems(FXCollections.observableArrayList(
                Repositorio.getInstancia().getSuscripciones()));
    }

    @FXML
    private void onRenovar() {
        Suscripcion s = tablaSuscripciones.getSelectionModel().getSelectedItem();
        if (s == null) {
            FuncionesGraficas.warning("Renovar", "Selecciona una suscripcion.");
            return;
        }
        s.renovarSuscripcion();
        tablaSuscripciones.refresh();
        FuncionesGraficas.mostrarMensaje("Renovacion",
                "Suscripcion renovada hasta: " + s.getFechaFin(), 1);
    }

    @FXML
    private void onCancelar() {
        Suscripcion s = tablaSuscripciones.getSelectionModel().getSelectedItem();
        if (s == null) {
            FuncionesGraficas.warning("Cancelar", "Selecciona una suscripcion.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool("Cancelar suscripcion",
                "Cancelar la suscripcion de " + s.getSocio().getNombre() + "?");
        if (ok) {
            s.cancelarSuscripcion();
            tablaSuscripciones.refresh();
        }
    }

    @FXML
    private void onPagarMensualidad() {
        Suscripcion s = tablaSuscripciones.getSelectionModel().getSelectedItem();
        if (s == null) {
            FuncionesGraficas.warning("Pagar mensualidad", "Selecciona una suscripcion.");
            return;
        }
        // Buscar mensualidad pendiente
        Mensualidad pendiente = null;
        for (Mensualidad m : s.getMensualidades()) {
            if (m.getEstado() == EstadoPago.PENDIENTE) {
                pendiente = m;
                break;
            }
        }
        if (pendiente != null) {
            pendiente.marcarPagada();
            tablaSuscripciones.refresh();
            FuncionesGraficas.mostrarMensaje("Pago registrado",
                    "Mensualidad pagada: " + String.format("%.2f", pendiente.getImporte()) + " EUR\n"
                    + "Socio: " + s.getSocio().getNombre(), 1);
        } else {
            // Crear nueva mensualidad y marcarla pagada de inmediato
            String id = "MEN-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Mensualidad nueva = new Mensualidad(id, s, s.getImporteMensual(),
                    LocalDate.now().plusMonths(1));
            nueva.marcarPagada();
            s.anadirMensualidad(nueva);
            tablaSuscripciones.refresh();
            FuncionesGraficas.mostrarMensaje("Pago registrado",
                    "Mensualidad creada y pagada: "
                    + String.format("%.2f", s.getImporteMensual()) + " EUR\n"
                    + "Socio: " + s.getSocio().getNombre(), 1);
        }
    }

    @FXML
    private void onNuevaSuscripcion() {
        List<Socio> socios = Repositorio.getInstancia().getSocios();
        if (socios.isEmpty()) {
            FuncionesGraficas.warning("Nueva suscripcion", "No hay socios registrados.");
            return;
        }
        String[] nombresSocios = socios.stream().map(Socio::getNombre).toArray(String[]::new);
        int iSocio = FuncionesGraficas.menuDesplegable("Nueva suscripcion",
                "Selecciona el socio:", nombresSocios);
        if (iSocio < 0) return;
        Socio socio = socios.get(iSocio);

        String[] tipos = {"Mensual", "Trimestral", "Anual"};
        int iTipo = FuncionesGraficas.menuDesplegable("Nueva suscripcion", "Tipo:", tipos);
        if (iTipo < 0) return;
        String tipo = tipos[iTipo];

        String importeStr = FuncionesGraficas.pedirDatos("Nueva suscripcion",
                "Importe mensual (EUR):");
        if (importeStr == null) return;
        double importe;
        try {
            importe = Double.parseDouble(importeStr.replace(",", "."));
        } catch (NumberFormatException ex) {
            FuncionesGraficas.warning("Nueva suscripcion", "Importe invalido.");
            return;
        }

        int meses = iTipo == 0 ? 1 : (iTipo == 1 ? 3 : 12);
        String id = "SUS-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Suscripcion nueva = new Suscripcion(id, socio, tipo, importe,
                LocalDate.now(), LocalDate.now().plusMonths(meses), meses * 4);
        socio.setSuscripcion(nueva);
        Repositorio.getInstancia().getSuscripciones().add(nueva);
        tablaSuscripciones.setItems(FXCollections.observableArrayList(
                Repositorio.getInstancia().getSuscripciones()));
        FuncionesGraficas.mostrarMensaje("Nueva suscripcion",
                "Suscripcion creada para " + socio.getNombre() + "\nTipo: " + tipo, 1);
    }
}
