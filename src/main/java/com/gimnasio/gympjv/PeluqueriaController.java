package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Empleado;
import com.gimnasio.gympjv.clases.Peluqueria;
import com.gimnasio.gympjv.clases.Socio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PeluqueriaController {

    @FXML private TableView<Peluqueria> tablaServicios;
    @FXML private TableColumn<Peluqueria, String> colId;
    @FXML private TableColumn<Peluqueria, String> colSocio;
    @FXML private TableColumn<Peluqueria, String> colEmpleado;
    @FXML private TableColumn<Peluqueria, String> colFecha;
    @FXML private TableColumn<Peluqueria, Double> colTotal;
    @FXML private TableColumn<Peluqueria, Boolean> colFinalizado;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        colSocio.setCellValueFactory(d -> {
            Socio s = d.getValue().getSocio();
            return new javafx.beans.property.SimpleStringProperty(
                    s != null ? s.getNombre() : "Cliente externo");
        });
        colEmpleado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getPeluquero().getNombre()));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCalculado"));
        colFinalizado.setCellValueFactory(new PropertyValueFactory<>("finalizado"));
        recargar();
    }

    private void recargar() {
        tablaServicios.setItems(FXCollections.observableArrayList(
                Repositorio.getInstancia().getServiciosPeluqueria()));
    }

    @FXML
    private void onNuevo() {
        List<Empleado> empleados = Repositorio.getInstancia().getEmpleados();
        if (empleados.isEmpty()) {
            FuncionesGraficas.warning("Nuevo servicio", "No hay empleados.");
            return;
        }
        String[] nEmpl = empleados.stream().map(Empleado::getNombre).toArray(String[]::new);
        int iE = FuncionesGraficas.menuDesplegable("Peluqueria",
                "Selecciona el peluquero:", nEmpl);
        if (iE < 0) return;

        List<Socio> socios = Repositorio.getInstancia().getSocios();
        String[] sociosArr = new String[socios.size() + 1];
        sociosArr[0] = "Cliente externo";
        for (int i = 0; i < socios.size(); i++) sociosArr[i + 1] = socios.get(i).getNombre();
        int iS = FuncionesGraficas.menuDesplegable("Peluqueria",
                "Selecciona el cliente:", sociosArr);
        if (iS < 0) return;
        Socio cliente = (iS == 0) ? null : socios.get(iS - 1);

        String[] tiposServ = {"Pelo", "Barba", "Cejas"};
        int iT = FuncionesGraficas.menuDesplegable("Peluqueria",
                "Tipo de servicio:", tiposServ);
        if (iT < 0) return;

        String id = "PEL-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Peluqueria pel = new Peluqueria(id, cliente, empleados.get(iE), LocalDateTime.now());
        pel.anadirServicio(tiposServ[iT]);
        pel.calcularTotal();
        Repositorio.getInstancia().getServiciosPeluqueria().add(pel);
        recargar();
        FuncionesGraficas.mostrarMensaje("Servicio creado",
                "Servicio " + id + " - Total: " + pel.getTotalCalculado() + " EUR", 1);
    }

    @FXML
    private void onFinalizar() {
        Peluqueria p = tablaServicios.getSelectionModel().getSelectedItem();
        if (p == null) {
            FuncionesGraficas.warning("Finalizar", "Selecciona un servicio.");
            return;
        }
        if (p.isFinalizado()) {
            FuncionesGraficas.warning("Finalizar", "El servicio ya esta finalizado.");
            return;
        }
        p.finalizarCorte();
        tablaServicios.refresh();
        FuncionesGraficas.mostrarDatos("Ticket peluqueria", p.imprimirTicket());
    }
}
