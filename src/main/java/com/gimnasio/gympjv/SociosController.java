package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Suscripcion;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

public class SociosController {

    // ── Conexion BBDD ──────────────────────────────────────────────────────
    private static Connection c     = null;
    private static Statement  insSQL = null;

    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, String>  colId;
    @FXML private TableColumn<Socio, String>  colNombre;
    @FXML private TableColumn<Socio, String>  colTelefono;
    @FXML private TableColumn<Socio, String>  colEmail;
    @FXML private TableColumn<Socio, String>  colQrCode;
    @FXML private TableColumn<Socio, Integer> colPuntos;
    @FXML private TableColumn<Socio, String>  colCuota;
    @FXML private TableColumn<Socio, String>  colEstado;

    private ObservableList<Socio> datos;

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
        c = ConexionBBDD.getConexion();
        if (c != null) {
            try {
                insSQL = c.createStatement();
            } catch (SQLException ex) {
                insSQL = null;
            }
        }
        colId.setCellValueFactory(
            new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(
            new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(
            new PropertyValueFactory<>("email"));
        colQrCode.setCellValueFactory(
            new PropertyValueFactory<>("qrCode"));
        colPuntos.setCellValueFactory(
            new PropertyValueFactory<>("puntos"));
        colCuota.setCellValueFactory(cd -> {
            Suscripcion sus =
                cd.getValue().getSuscripcion();
            String val = (sus != null)
                ? sus.getTipoSuscripcion() : "-";
            return new SimpleObjectProperty<>(val);
        });
        colEstado.setCellValueFactory(cd -> {
            Suscripcion sus =
                cd.getValue().getSuscripcion();
            String val = (sus != null)
                ? sus.getEstado().name() : "-";
            return new SimpleObjectProperty<>(val);
        });
        recargar();
    }

    private void recargar() {
        datos = FXCollections.observableArrayList(
            Repositorio.getInstancia().getSocios());
        tablaSocios.setItems(datos);
    }

    @FXML
    private void onAlta(ActionEvent e) {
        String nombre = FuncionesGraficas.pedirDatos(
            "Nuevo socio", "Nombre completo:");
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        String telefono = FuncionesGraficas.pedirDatos(
            "Nuevo socio", "Telefono:");
        if (telefono == null) {
            return;
        }
        String email = FuncionesGraficas.pedirDatos(
            "Nuevo socio", "Email:");
        if (email == null) {
            return;
        }
        String login = FuncionesGraficas.pedirDatos(
            "Nuevo socio", "Login de acceso:");
        if (login == null || login.isBlank()) {
            return;
        }
        String clave = FuncionesGraficas.pedirDatos(
            "Nuevo socio", "Contrasena:");
        if (clave == null || clave.isBlank()) {
            return;
        }

        // Validar que el login no exista ya en BD
        if (loginExisteBBDD(login)) {
            FuncionesGraficas.warning(
                "Nuevo socio",
                "El login '" + login
                + "' ya esta en uso.");
            return;
        }

        String id = "SOC-" + UUID.randomUUID()
            .toString().substring(0, 5).toUpperCase();
        String qr = "QR-" + id;
        Socio nuevo = new Socio(
            id, nombre, telefono,
            email, LocalDate.now(),
            qr, login, clave);
        Repositorio.getInstancia()
            .getSocios().add(nuevo);

        // FASE F: generar QR y actualizar campo en BD
        String[] partes = nombre.split(" ", 2);
        String n  = partes[0];
        String ap = (partes.length > 1)
                ? partes[1] : "";
        try {
            GeneradorQR gen = new GeneradorQR();
            String rutaQR = gen.generarQR(
                n, ap, false);
            nuevo.setQrCode(rutaQR);
            actualizarQrBBDD(id, rutaQR);
        } catch (Exception ex) {
            // si falla el QR se mantiene el valor por defecto
        }

        recargar();
        FuncionesGraficas.mostrarMensaje(
            "Alta socio",
            "Socio registrado: " + id, 1);
    }

    private boolean loginExisteBBDD(String login) {
        boolean existe = false;
        if (insSQL != null) {
            try {
                ResultSet rs = insSQL.executeQuery(
                    "SELECT COUNT(*) FROM socios"
                    + " WHERE login = '"
                    + login + "'");
                if (rs.next()) {
                    existe = rs.getInt(1) > 0;
                }
                rs.close();
            } catch (SQLException ex) {
                existe = false;
            }
        }
        return existe;
    }

    private void actualizarQrBBDD(
            String idSocio, String rutaQR) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "UPDATE socios SET qr_code = '"
                    + rutaQR + "'"
                    + " WHERE id = '"
                    + idSocio + "'");
            } catch (SQLException ex) {
                // fallo silencioso, el objeto en memoria ya tiene la ruta
            }
        }
    }

    @FXML
    private void onBaja(ActionEvent e) {
        Socio s = tablaSocios.getSelectionModel()
            .getSelectedItem();
        if (s == null) {
            FuncionesGraficas.warning(
                "Baja socio",
                "Selecciona un socio primero.");
            return;
        }
        boolean ok = FuncionesGraficas.pedirBool(
            "Baja socio",
            "Eliminar definitivamente a "
            + s.getNombre() + "?");
        if (ok) {
            Repositorio.getInstancia()
                .getSocios().remove(s);
            recargar();
        }
    }

    @FXML
    private void onModificar(ActionEvent e) {
        Socio s = tablaSocios.getSelectionModel()
            .getSelectedItem();
        if (s == null) {
            FuncionesGraficas.warning(
                "Modificar socio",
                "Selecciona un socio primero.");
            return;
        }

        String nombre = FuncionesGraficas.pedirDatos(
            "Modificar socio",
            "Nombre completo (actual: "
            + s.getNombre()
            + ")\nDeja en blanco para no cambiar:");
        if (nombre == null) {
            return;
        }
        if (!nombre.isBlank()) {
            s.setNombre(nombre);
        }

        String tel = FuncionesGraficas.pedirDatos(
            "Modificar socio",
            "Telefono (actual: "
            + s.getTelefono()
            + ")\nDeja en blanco para no cambiar:");
        if (tel == null) {
            return;
        }
        if (!tel.isBlank()) {
            s.setTelefono(tel);
        }

        String em = FuncionesGraficas.pedirDatos(
            "Modificar socio",
            "Email (actual: "
            + s.getEmail()
            + ")\nDeja en blanco para no cambiar:");
        if (em == null) {
            return;
        }
        if (!em.isBlank()) {
            s.setEmail(em);
        }

        String loginActual = s.getLogin() != null
            ? s.getLogin() : "(sin login)";
        String login = FuncionesGraficas.pedirDatos(
            "Modificar socio",
            "Login (actual: "
            + loginActual
            + ")\nDeja en blanco para no cambiar:");
        if (login == null) {
            return;
        }
        if (!login.isBlank()) {
            s.setLogin(login);
        }

        recargar();
        FuncionesGraficas.mostrarMensaje(
            "Modificar socio",
            "Datos de " + s.getNombre()
            + " actualizados.", 1);
    }
}
