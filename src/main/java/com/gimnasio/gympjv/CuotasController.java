package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Mensualidad;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Suscripcion;
import com.gimnasio.gympjv.clases.enums.EstadoPago;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * Gestion de suscripciones para el administrador.
 * La Agenda --> Pantalla con lista paginada + al hacer clic en algun registro los datos aparecen en un formulario fijo a la derecha sin abrir ninguna popup
 *
 * @author Ayoub Atidi Belbaz 1º DAM
 */
public class CuotasController {

    // ── Variables estaticas (patron Agenda) ────────────────────────────────
    private static Connection c       = null;
    private static Statement  insSQL  = null;
    private static int        contReg = 0;
    private static ObservableList<Suscripcion> datos;

    // ── Campos FXML ────────────────────────────────────────────────────────
    @FXML private TableView<Suscripcion>           tabla;
    @FXML private TableColumn<Suscripcion, String> colId;
    @FXML private TableColumn<Suscripcion, String> colSocio;
    @FXML private TableColumn<Suscripcion, String> colTipo;
    @FXML private TableColumn<Suscripcion, String> colImporte;
    @FXML private TableColumn<Suscripcion, String> colFechaFin;
    @FXML private TableColumn<Suscripcion, String> colEstado;

    @FXML private TextField campoId;
    @FXML private TextField campoSocioId;
    @FXML private TextField campoTipo;
    @FXML private TextField campoImporte;
    @FXML private TextField campoFechaInicio;
    @FXML private TextField campoFechaFin;
    @FXML private TextField campoClases;
    @FXML private TextField campoEstado;
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
        tabla.getSelectionModel()
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
                cd.getValue().getIdSuscripcion()));
        colSocio.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getSocio().getNombre()));
        colTipo.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getTipoSuscripcion()));
        colImporte.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                String.format("%.2f EUR",
                    cd.getValue().getImporteMensual())));
        colFechaFin.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getFechaFin().toString()));
        colEstado.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().getEstado().name()));
    }

    private void cargarDatos() {
        datos = FXCollections.observableArrayList(
            Repositorio.getInstancia().getSuscripciones());
        tabla.setItems(datos);
        actualizarContador();
    }

    private void mostrarRegistro(int idx) {
        if (!datos.isEmpty()
                && idx >= 0 && idx < datos.size()) {
            mostrarFila(datos.get(idx));
            tabla.getSelectionModel().select(idx);
        }
    }

    private void mostrarFila(Suscripcion sus) {
        campoId.setText(sus.getIdSuscripcion());
        campoSocioId.setText(sus.getSocio().getId());
        campoTipo.setText(sus.getTipoSuscripcion());
        campoImporte.setText(
            String.format("%.2f", sus.getImporteMensual()));
        campoFechaInicio.setText(
            sus.getFechaInicio().toString());
        campoFechaFin.setText(
            sus.getFechaFin().toString());
        campoClases.setText(
            String.valueOf(sus.getClasesIncluidas()));
        campoEstado.setText(sus.getEstado().name());
        contReg = datos.indexOf(sus);
        actualizarContador();
    }

    private void limpiarCampos() {
        campoId.setText("");
        campoSocioId.setText("");
        campoTipo.setText("Mensual");
        campoImporte.setText("");
        campoFechaInicio.setText(
            LocalDate.now().toString());
        campoFechaFin.setText(
            LocalDate.now().plusMonths(1).toString());
        campoClases.setText("12");
        campoEstado.setText("ACTIVA");
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
        campoId.setText("SUS-" + uid);
        campoMensaje.setText(
            "Rellena los campos y pulsa Guardar.");
    }

    @FXML
    private void onGuardar(ActionEvent e) {
        String id  = campoId.getText().trim();
        String sid = campoSocioId.getText().trim();
        String tip = campoTipo.getText().trim();
        String imp = campoImporte.getText().trim();
        String fi  = campoFechaInicio.getText().trim();
        String ff  = campoFechaFin.getText().trim();
        String cl  = campoClases.getText().trim();
        String msg = "";
        double importe = 0.0;
        int    clases  = 0;
        LocalDate fechaIni = null;
        LocalDate fechaFin = null;
        boolean ok = !id.isBlank()
            && !sid.isBlank()
            && !tip.isBlank();
        if (!ok) {
            msg = "ID, ID Socio y Tipo son obligatorios.";
        }
        if (ok) {
            try {
                importe = Double.parseDouble(
                    imp.replace(",", "."));
                clases = Integer.parseInt(cl);
            } catch (NumberFormatException ex) {
                ok  = false;
                msg = "Importe o clases invalidos.";
            }
        }
        if (ok) {
            try {
                fechaIni = LocalDate.parse(fi);
                fechaFin = LocalDate.parse(ff);
            } catch (DateTimeParseException ex) {
                ok  = false;
                msg = "Fechas invalidas (AAAA-MM-DD).";
            }
        }
        Socio socio = null;
        if (ok) {
            socio = Repositorio.getInstancia()
                .buscarSocioPorId(sid);
            if (socio == null) {
                ok  = false;
                msg = "Socio " + sid + " no encontrado.";
            }
        }
        if (ok) {
            Suscripcion existente = buscarPorId(id);
            if (existente != null) {
                actualizarSuscripcion(existente, tip,
                    importe, fechaIni, fechaFin, clases);
                msg = "Suscripcion " + id + " actualizada.";
            } else {
                crearSuscripcion(id, socio, tip, importe,
                    fechaIni, fechaFin, clases);
                msg = "Suscripcion " + id + " creada.";
            }
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
            msg = "Selecciona una suscripcion primero.";
        } else {
            boolean ok = FuncionesGraficas.pedirBool(
                "Baja suscripcion",
                "Eliminar definitivamente: " + id + "?");
            if (ok) {
                eliminarSuscripcion(id);
                cargarDatos();
                limpiarCampos();
                msg = "Suscripcion " + id + " eliminada.";
            }
        }
        campoMensaje.setText(msg);
    }

    // ── Cobrar cuota ───────────────────────────────────────────────────────
    @FXML
    private void onCobrarCuota(ActionEvent e) {
        Suscripcion sus = tabla.getSelectionModel()
            .getSelectedItem();
        String msg = "";
        if (sus == null) {
            msg = "Selecciona una suscripcion.";
        } else {
            String idMen = "MEN-"
                + UUID.randomUUID()
                       .toString()
                       .substring(0, 5)
                       .toUpperCase();
            LocalDate hoy = LocalDate.now();
            Mensualidad men = new Mensualidad(
                idMen, sus,
                sus.getImporteMensual(), hoy);
            men.marcarPagada();
            sus.anadirMensualidad(men);
            persistirMensualidad(idMen, sus, hoy);
            cargarDatos();
            msg = "Cuota " + idMen
                + " cobrada para "
                + sus.getSocio().getNombre() + ".";
        }
        campoMensaje.setText(msg);
    }

    // ── Logica de modelo + persistencia ────────────────────────────────────
    private void crearSuscripcion(String id, Socio socio,
            String tip, double importe,
            LocalDate fechaIni, LocalDate fechaFin,
            int clases) {
        Suscripcion nueva = new Suscripcion(
            id, socio, tip, importe,
            fechaIni, fechaFin, clases);
        socio.setSuscripcion(nueva);
        Repositorio.getInstancia()
            .getSuscripciones().add(nueva);
        insertarBBDD(nueva);
    }

    private void actualizarSuscripcion(Suscripcion sus,
            String tip, double importe,
            LocalDate fechaIni, LocalDate fechaFin,
            int clases) {
        // El modelo no expone setters salvo estado;
        // reemplazamos por una nueva con los mismos enlaces.
        Socio socio = sus.getSocio();
        Suscripcion reemplazo = new Suscripcion(
            sus.getIdSuscripcion(), socio, tip, importe,
            fechaIni, fechaFin, clases);
        reemplazo.setEstado(sus.getEstado());
        socio.setSuscripcion(reemplazo);
        Repositorio.getInstancia()
            .getSuscripciones().remove(sus);
        Repositorio.getInstancia()
            .getSuscripciones().add(reemplazo);
        actualizarBBDD(reemplazo);
    }

    private void eliminarSuscripcion(String id) {
        Suscripcion sus = buscarPorId(id);
        if (sus != null) {
            Socio socio = sus.getSocio();
            if (socio != null
                    && socio.getSuscripcion() == sus) {
                socio.setSuscripcion(null);
            }
            Repositorio.getInstancia()
                .getSuscripciones().remove(sus);
        }
        eliminarBBDD(id);
    }

    private Suscripcion buscarPorId(String id) {
        Suscripcion encontrada = null;
        for (Suscripcion s
                : Repositorio.getInstancia().getSuscripciones()) {
            if (s.getIdSuscripcion().equals(id)) {
                encontrada = s;
            }
        }
        return encontrada;
    }

    private void seleccionarPorId(String id) {
        int idx = -1;
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getIdSuscripcion().equals(id)) {
                idx = i;
            }
        }
        if (idx >= 0) {
            contReg = idx;
            mostrarRegistro(contReg);
        }
    }

    // ── SQL: insertar ──────────────────────────────────────────────────────
    private void insertarBBDD(Suscripcion sus) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "INSERT INTO suscripciones VALUES ("
                    + "'" + sus.getIdSuscripcion() + "',"
                    + "'" + sus.getSocio().getId() + "',"
                    + "'" + sus.getTipoSuscripcion() + "',"
                    + sus.getImporteMensual() + ","
                    + "'" + sus.getFechaInicio() + "',"
                    + "'" + sus.getFechaFin() + "',"
                    + sus.getClasesIncluidas() + ")");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo insertar: "
                    + ex.getMessage());
            }
        }
    }

    // ── SQL: actualizar ────────────────────────────────────────────────────
    private void actualizarBBDD(Suscripcion sus) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "UPDATE suscripciones SET"
                    + " tipo='" + sus.getTipoSuscripcion() + "',"
                    + " importe=" + sus.getImporteMensual() + ","
                    + " fecha_inicio='" + sus.getFechaInicio() + "',"
                    + " fecha_fin='" + sus.getFechaFin() + "',"
                    + " clases_incluidas=" + sus.getClasesIncluidas()
                    + " WHERE id='" + sus.getIdSuscripcion() + "'");
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
                    "DELETE FROM mensualidades"
                    + " WHERE id_suscripcion='" + id + "'");
                insSQL.execute(
                    "DELETE FROM suscripciones"
                    + " WHERE id='" + id + "'");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo eliminar: "
                    + ex.getMessage());
            }
        }
    }

    private void persistirMensualidad(String idMen,
            Suscripcion sus, LocalDate hoy) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "INSERT INTO mensualidades VALUES ("
                    + "'" + idMen + "',"
                    + "'" + sus.getIdSuscripcion() + "',"
                    + sus.getImporteMensual() + ","
                    + "'" + hoy + "',"
                    + "'" + EstadoPago.PAGADA.name()
                    + "')");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se persistio la mensualidad: "
                    + ex.getMessage());
            }
        }
    }
}
