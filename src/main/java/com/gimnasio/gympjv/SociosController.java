package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Suscripcion;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Gestion CRUD de socios.
 * Patron Agenda: vars estaticas, navegacion paginada,
 * formulario lateral conectado a TextFields (sin popups pedirDatos).
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class SociosController {

    // ── Variables estaticas (patron Agenda) ────────────────────────────────
    private static Connection c       = null;
    private static Statement  insSQL  = null;
    private static int        contReg = 0;
    private static ObservableList<Socio> datos;

    // ── Campos FXML ────────────────────────────────────────────────────────
    @FXML private TableView<Socio>           tablaSocios;
    @FXML private TableColumn<Socio, String>  colId;
    @FXML private TableColumn<Socio, String>  colNombre;
    @FXML private TableColumn<Socio, String>  colTelefono;
    @FXML private TableColumn<Socio, String>  colEmail;
    @FXML private TableColumn<Socio, String>  colQrCode;
    @FXML private TableColumn<Socio, Integer> colPuntos;
    @FXML private TableColumn<Socio, String>  colCuota;
    @FXML private TableColumn<Socio, String>  colEstado;

    @FXML private TextField campoId;
    @FXML private TextField campoNombre;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoEmail;
    @FXML private TextField campoLogin;
    @FXML private TextField campoClave;
    @FXML private TextField campoPuntos;
    @FXML private Label     campoRegistro;
    @FXML private Label     campoMensaje;

    // ── initialize ─────────────────────────────────────────────────────────
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
        configurarColumnas();
        cargarDatos();
        if (!datos.isEmpty()) {
            contReg = 0;
            mostrarRegistro(contReg);
        }
        tablaSocios.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, ant, nuevo) -> {
                if (nuevo != null) {
                    mostrarFila(nuevo);
                }
            });
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getId()));
        colNombre.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getNombre()));
        colTelefono.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getTelefono()));
        colEmail.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getEmail()));
        colQrCode.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getQrCode()));
        colPuntos.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getPuntos()));
        colCuota.setCellValueFactory(cd -> {
            Suscripcion sus = cd.getValue().getSuscripcion();
            String val = "-";
            if (sus != null) {
                val = sus.getTipoSuscripcion();
            }
            return new SimpleObjectProperty<>(val);
        });
        colEstado.setCellValueFactory(cd -> {
            Suscripcion sus = cd.getValue().getSuscripcion();
            String val = "-";
            if (sus != null) {
                val = sus.getEstado().name();
            }
            return new SimpleObjectProperty<>(val);
        });
    }

    private void cargarDatos() {
        datos = FXCollections.observableArrayList(
            Repositorio.getInstancia().getSocios());
        tablaSocios.setItems(datos);
        actualizarContador();
    }

    private void mostrarRegistro(int idx) {
        if (!datos.isEmpty()
                && idx >= 0 && idx < datos.size()) {
            mostrarFila(datos.get(idx));
            tablaSocios.getSelectionModel().select(idx);
        }
    }

    private void mostrarFila(Socio s) {
        campoId.setText(s.getId());
        campoNombre.setText(s.getNombre());
        campoTelefono.setText(s.getTelefono());
        campoEmail.setText(s.getEmail());
        String log = "";
        if (s.getLogin() != null) {
            log = s.getLogin();
        }
        campoLogin.setText(log);
        String pwd = "";
        if (s.getPassword() != null) {
            pwd = s.getPassword();
        }
        campoClave.setText(pwd);
        campoPuntos.setText(String.valueOf(s.getPuntos()));
        contReg = datos.indexOf(s);
        actualizarContador();
    }

    private void limpiarCampos() {
        campoId.setText("");
        campoNombre.setText("");
        campoTelefono.setText("");
        campoEmail.setText("");
        campoLogin.setText("");
        campoClave.setText("");
        campoPuntos.setText("0");
        campoMensaje.setText("");
    }

    private void actualizarContador() {
        int total = datos.size();
        int pos   = 0;
        if (total > 0) {
            pos = contReg + 1;
        }
        campoRegistro.setText(
            "Registro " + pos + " / " + total);
    }

    // ── Navegacion ─────────────────────────────────────────────────────────
    @FXML
    private void onPrimero(ActionEvent e) {
        if (!datos.isEmpty()) {
            contReg = 0;
            mostrarRegistro(contReg);
        }
    }

    @FXML
    private void onAnterior(ActionEvent e) {
        if (contReg > 0) {
            contReg--;
            mostrarRegistro(contReg);
        }
    }

    @FXML
    private void onSiguiente(ActionEvent e) {
        if (contReg < datos.size() - 1) {
            contReg++;
            mostrarRegistro(contReg);
        }
    }

    @FXML
    private void onUltimo(ActionEvent e) {
        if (!datos.isEmpty()) {
            contReg = datos.size() - 1;
            mostrarRegistro(contReg);
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────
    @FXML
    private void onNuevo(ActionEvent e) {
        limpiarCampos();
        String uid = UUID.randomUUID()
            .toString().substring(0, 5).toUpperCase();
        campoId.setText("SOC-" + uid);
        campoMensaje.setText(
            "Rellena los campos y pulsa Guardar.");
    }

    @FXML
    private void onGuardar(ActionEvent e) {
        String id  = campoId.getText().trim();
        String nom = campoNombre.getText().trim();
        String tel = campoTelefono.getText().trim();
        String em  = campoEmail.getText().trim();
        String log = campoLogin.getText().trim();
        String clv = campoClave.getText().trim();
        String pts = campoPuntos.getText().trim();
        String msg = "";
        int puntos = 0;
        boolean ok = !id.isBlank() && !nom.isBlank();
        if (!ok) {
            msg = "ID y Nombre son obligatorios.";
        }
        if (ok) {
            try {
                puntos = Integer.parseInt(
                    pts.isBlank() ? "0" : pts);
            } catch (NumberFormatException ex) {
                ok  = false;
                msg = "Puntos invalidos.";
            }
        }
        if (ok) {
            Socio existente = Repositorio.getInstancia()
                .buscarSocioPorId(id);
            if (existente != null) {
                actualizarSocio(existente, nom, tel, em,
                    log, clv, puntos);
                msg = "Socio " + id + " actualizado.";
            } else {
                if (loginEnUso(log)) {
                    ok  = false;
                    msg = "El login '" + log
                        + "' ya esta en uso.";
                } else {
                    crearSocio(id, nom, tel, em,
                        log, clv, puntos);
                    msg = "Socio " + id + " creado.";
                }
            }
        }
        if (ok) {
            cargarDatos();
            seleccionarPorId(id);
        }
        campoMensaje.setText(msg);
    }

    @FXML
    private void onEliminar(ActionEvent e) {
        String id  = campoId.getText().trim();
        String msg = "";
        if (id.isBlank()) {
            msg = "Selecciona un socio primero.";
        } else {
            boolean ok = FuncionesGraficas.pedirBool(
                "Baja socio",
                "Eliminar definitivamente al socio "
                + id + "?");
            if (ok) {
                eliminarSocio(id);
                cargarDatos();
                limpiarCampos();
                msg = "Socio " + id + " eliminado.";
            }
        }
        campoMensaje.setText(msg);
    }

    // ── Logica de modelo + persistencia ────────────────────────────────────
    private void crearSocio(String id, String nom,
            String tel, String em, String log,
            String clv, int puntos) {
        String qr = "QR-" + id;
        Socio nuevo = new Socio(
            id, nom, tel, em, LocalDate.now(),
            qr, log, clv);
        nuevo.setPuntos(puntos);
        Repositorio.getInstancia().getSocios().add(nuevo);
        // Generar QR e intentar persistir
        String[] partes = nom.split(" ", 2);
        String n  = partes[0];
        String ap = "";
        if (partes.length > 1) {
            ap = partes[1];
        }
        try {
            GeneradorQR gen = new GeneradorQR();
            String rutaQR = gen.generarQR(n, ap, false);
            nuevo.setQrCode(rutaQR);
        } catch (Exception ex) {
            // si falla el QR se mantiene el valor por defecto
        }
        insertarBBDD(nuevo);
    }

    private void actualizarSocio(Socio s, String nom,
            String tel, String em, String log,
            String clv, int puntos) {
        s.setNombre(nom);
        s.setTelefono(tel);
        s.setEmail(em);
        s.setLogin(log);
        if (!clv.isBlank()) {
            s.setPassword(clv);
        }
        s.setPuntos(puntos);
        // Regenerar QR para que la ruta refleje el dato actualizado.
        String[] partes = nom.split(" ", 2);
        String n  = partes[0];
        String ap = "";
        if (partes.length > 1) {
            ap = partes[1];
        }
        boolean cuotaActiva = s.getSuscripcion() != null;
        try {
            GeneradorQR gen = new GeneradorQR();
            String rutaQR = gen.generarQR(n, ap, cuotaActiva);
            s.setQrCode(rutaQR);
        } catch (Exception ex) {
            // si falla el QR se conserva la ruta anterior
        }
        actualizarBBDD(s);
    }

    private void eliminarSocio(String id) {
        Socio s = Repositorio.getInstancia()
            .buscarSocioPorId(id);
        if (s != null) {
            Repositorio.getInstancia()
                .getSocios().remove(s);
        }
        eliminarBBDD(id);
    }

    private void seleccionarPorId(String id) {
        int idx = -1;
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(id)) {
                idx = i;
            }
        }
        if (idx >= 0) {
            contReg = idx;
            mostrarRegistro(contReg);
        }
    }

    private boolean loginEnUso(String login) {
        boolean existe = false;
        if (login != null && !login.isBlank()) {
            for (Socio s
                    : Repositorio.getInstancia().getSocios()) {
                if (login.equals(s.getLogin())) {
                    existe = true;
                }
            }
            if (!existe && insSQL != null) {
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
        }
        return existe;
    }

    // ── SQL: insertar ──────────────────────────────────────────────────────
    private void insertarBBDD(Socio s) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "INSERT INTO socios VALUES ("
                    + "'" + s.getId() + "',"
                    + "'" + s.getNombre() + "',"
                    + "'" + s.getTelefono() + "',"
                    + "'" + s.getEmail() + "',"
                    + "'" + s.getFechaAlta() + "',"
                    + "'" + s.getQrCode() + "',"
                    + "'" + s.getLogin() + "',"
                    + "'" + s.getPassword() + "',"
                    + s.getPuntos() + ")");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo insertar: "
                    + ex.getMessage());
            }
        }
    }

    // ── SQL: actualizar ────────────────────────────────────────────────────
    private void actualizarBBDD(Socio s) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "UPDATE socios SET"
                    + " nombre='" + s.getNombre() + "',"
                    + " telefono='" + s.getTelefono() + "',"
                    + " email='" + s.getEmail() + "',"
                    + " qr_code='" + s.getQrCode() + "',"
                    + " login='" + s.getLogin() + "',"
                    + " password='" + s.getPassword() + "',"
                    + " puntos=" + s.getPuntos()
                    + " WHERE id='" + s.getId() + "'");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo actualizar: "
                    + ex.getMessage());
            }
        }
    }

    // ── SQL: eliminar ──────────────────────────────────────────────────────
    private void eliminarBBDD(String id) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "DELETE FROM suscripciones"
                    + " WHERE id_socio='" + id + "'");
                insSQL.execute(
                    "DELETE FROM socios"
                    + " WHERE id='" + id + "'");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo eliminar: "
                    + ex.getMessage());
            }
        }
    }
}
