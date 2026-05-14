package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Admin;
import com.gimnasio.gympjv.clases.Empleado;
import com.gimnasio.gympjv.clases.Monitor;
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
import java.util.ArrayList;
import java.util.UUID;

/**
 * Gestion CRUD de empleados/admins/monitores.
 * Patron Agenda: vars estaticas, datosEntrada(), navegacion,
 * Statement + SQL concatenado, ResultSet por indice.
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class EmpleadosController {

    // ── Fila de tabla ──────────────────────────────────────────────────────
    private static class FilaEmpleado {
        String id, nombre, telefono, email;
        String horario, tipo, login, clave, especialidad;
        double salario;

        FilaEmpleado(String id, String nombre,
                String telefono, String email,
                double salario, String horario,
                String tipo, String login,
                String clave, String especialidad) {
            this.id           = id;
            this.nombre       = nombre;
            this.telefono     = telefono;
            this.email        = email;
            this.salario      = salario;
            this.horario      = horario;
            this.tipo         = tipo;
            this.login        = login;
            this.clave        = clave;
            this.especialidad = especialidad;
        }
    }

    // ── Variables estaticas (patron Agenda) ────────────────────────────────
    private static ArrayList<FilaEmpleado> lista =
        new ArrayList<>();
    private static int       contReg  = 0;
    private static String    usuario  = "";
    private static Statement insSQL   = null;
    private static Connection c       = null;
    private static ObservableList<FilaEmpleado> datos;

    /** Llamar antes de abrir la ventana con el login del admin. */
    public static void datosEntrada(String us) {
        usuario = us;
    }

    // ── Campos FXML ───────────────────────────────────────────────────────
    @FXML private TableView<FilaEmpleado>           tabla;
    @FXML private TableColumn<FilaEmpleado, String> colId;
    @FXML private TableColumn<FilaEmpleado, String> colNombre;
    @FXML private TableColumn<FilaEmpleado, String> colRol;
    @FXML private TableColumn<FilaEmpleado, String> colTelefono;
    @FXML private TableColumn<FilaEmpleado, String> colEmail;
    @FXML private TableColumn<FilaEmpleado, String> colSalario;
    @FXML private TableColumn<FilaEmpleado, String> colHorario;

    @FXML private TextField campoId;
    @FXML private TextField campoNombre;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoEmail;
    @FXML private TextField campoSalario;
    @FXML private TextField campoHorario;
    @FXML private TextField campoTipo;
    @FXML private TextField campoLogin;
    @FXML private TextField campoClave;
    @FXML private TextField campoEspecialidad;
    @FXML private Label     campoRegistro;
    @FXML private Label     campoMensaje;

    // ── initialize ────────────────────────────────────────────────────────
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
        if (!lista.isEmpty()) {
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
                cd.getValue().id));
        colNombre.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().nombre));
        colRol.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().tipo));
        colTelefono.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().telefono));
        colEmail.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().email));
        colSalario.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                String.format("%.2f",
                    cd.getValue().salario)));
        colHorario.setCellValueFactory(cd ->
            new SimpleObjectProperty<>(
                cd.getValue().horario));
    }

    private void cargarDatos() {
        lista.clear();
        boolean exito = false;
        if (insSQL != null) {
            try {
                cargarDesdeSQL();
                exito = true;
            } catch (SQLException ex) {
                exito = false;
            }
        }
        if (!exito) {
            cargarDesdeRepo();
        }
        datos = FXCollections.observableArrayList(lista);
        tabla.setItems(datos);
        actualizarContador();
    }

    private void cargarDesdeSQL() throws SQLException {
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(
            "SELECT id, nombre, telefono, email,"
            + " salario_base, horario, tipo"
            + " FROM empleados"
            + " ORDER BY tipo, nombre");
        while (rs.next()) {
            String id  = rs.getString(1);
            String nom = rs.getString(2);
            String tel = rs.getString(3);
            String em  = rs.getString(4);
            double sal = rs.getDouble(5);
            String hor = rs.getString(6);
            String tip = rs.getString(7);
            String log = "";
            String clv = "";
            String esp = "";
            if ("ADMIN".equals(tip)) {
                ResultSet ra = c.createStatement()
                    .executeQuery(
                        "SELECT login, password"
                        + " FROM admins_extra"
                        + " WHERE id='" + id + "'");
                if (ra.next()) {
                    log = ra.getString(1);
                    clv = ra.getString(2);
                }
                ra.close();
            } else {
                if ("MONITOR".equals(tip)) {
                    ResultSet rm = c.createStatement()
                        .executeQuery(
                            "SELECT login, password,"
                            + " especialidad"
                            + " FROM monitores_extra"
                            + " WHERE id='" + id + "'");
                    if (rm.next()) {
                        log = rm.getString(1);
                        clv = rm.getString(2);
                        esp = rm.getString(3);
                    }
                    rm.close();
                }
            }
            lista.add(new FilaEmpleado(
                id, nom, tel, em,
                sal, hor, tip, log, clv, esp));
        }
        rs.close();
        st.close();
    }

    private void cargarDesdeRepo() {
        for (Empleado emp
                : Repositorio.getInstancia().getEmpleados()) {
            String tip = "EMPLEADO";
            String log = "";
            String clv = "";
            String esp = "";
            if (emp instanceof Admin) {
                tip = "ADMIN";
                log = ((Admin) emp).getLogin();
                clv = ((Admin) emp).getPassword();
            } else {
                if (emp instanceof Monitor) {
                    tip = "MONITOR";
                    log = ((Monitor) emp).getLogin();
                    clv = ((Monitor) emp).getPassword();
                    esp = ((Monitor) emp).getEspecialidad();
                }
            }
            lista.add(new FilaEmpleado(
                emp.getId(), emp.getNombre(),
                emp.getTelefono(), emp.getEmail(),
                emp.getSalarioBase(), emp.getHorario(),
                tip, log, clv, esp));
        }
    }

    private void mostrarRegistro(int idx) {
        if (!lista.isEmpty()
                && idx >= 0 && idx < lista.size()) {
            mostrarFila(lista.get(idx));
            tabla.getSelectionModel().select(idx);
        }
    }

    private void mostrarFila(FilaEmpleado f) {
        campoId.setText(f.id);
        campoNombre.setText(f.nombre);
        campoTelefono.setText(f.telefono);
        campoEmail.setText(f.email);
        campoSalario.setText(
            String.format("%.2f", f.salario));
        campoHorario.setText(f.horario);
        campoTipo.setText(f.tipo);
        campoLogin.setText(f.login);
        campoClave.setText(f.clave);
        campoEspecialidad.setText(f.especialidad);
        contReg = lista.indexOf(f);
        actualizarContador();
    }

    private void limpiarCampos() {
        campoId.setText("");
        campoNombre.setText("");
        campoTelefono.setText("");
        campoEmail.setText("");
        campoSalario.setText("");
        campoHorario.setText("MANANA");
        campoTipo.setText("EMPLEADO");
        campoLogin.setText("");
        campoClave.setText("");
        campoEspecialidad.setText("");
        campoMensaje.setText("");
    }

    private void actualizarContador() {
        int total = lista.size();
        int pos   = (total == 0) ? 0 : contReg + 1;
        campoRegistro.setText(
            "Registro " + pos + " / " + total);
    }

    // ── Navegacion ─────────────────────────────────────────────────────────
    @FXML
    private void onPrimero(ActionEvent e) {
        if (!lista.isEmpty()) {
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
        if (contReg < lista.size() - 1) {
            contReg++;
            mostrarRegistro(contReg);
        }
    }

    @FXML
    private void onUltimo(ActionEvent e) {
        if (!lista.isEmpty()) {
            contReg = lista.size() - 1;
            mostrarRegistro(contReg);
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────
    @FXML
    private void onNuevo(ActionEvent e) {
        limpiarCampos();
        String uid = UUID.randomUUID()
            .toString().substring(0, 5).toUpperCase();
        campoId.setText("EMP-" + uid);
        campoMensaje.setText(
            "Rellena los campos y pulsa Guardar.");
    }

    @FXML
    private void onGuardar(ActionEvent e) {
        String id  = campoId.getText().trim();
        String nom = campoNombre.getText().trim();
        String tel = campoTelefono.getText().trim();
        String em  = campoEmail.getText().trim();
        String sal = campoSalario.getText().trim();
        String hor = campoHorario.getText().trim()
            .toUpperCase();
        String tip = campoTipo.getText().trim()
            .toUpperCase();
        String log = campoLogin.getText().trim();
        String clv = campoClave.getText().trim();
        String esp = campoEspecialidad.getText().trim();
        String msg = "";
        double salario = 0.0;
        boolean ok = !id.isBlank() && !nom.isBlank();
        if (!ok) {
            msg = "ID y Nombre son obligatorios.";
        }
        if (ok) {
            try {
                salario = Double.parseDouble(
                    sal.replace(",", "."));
            } catch (NumberFormatException ex) {
                ok  = false;
                msg = "Salario invalido.";
            }
        }
        if (ok) {
            boolean tipoOk = tip.equals("ADMIN")
                || tip.equals("MONITOR")
                || tip.equals("EMPLEADO");
            if (!tipoOk) {
                ok  = false;
                msg = "Tipo: ADMIN, MONITOR o EMPLEADO.";
            }
        }
        if (ok) {
            boolean existe = false;
            for (FilaEmpleado f : lista) {
                if (f.id.equals(id)) {
                    existe = true;
                }
            }
            if (existe) {
                actualizarBBDD(id, nom, tel, em,
                    salario, hor, tip, log, clv, esp);
            } else {
                insertarBBDD(id, nom, tel, em,
                    salario, hor, tip, log, clv, esp);
            }
            cargarDatos();
            msg = "Guardado correctamente.";
        }
        campoMensaje.setText(msg);
    }

    @FXML
    private void onEliminar(ActionEvent e) {
        String id  = campoId.getText().trim();
        String msg = "";
        if (!id.isBlank()) {
            boolean ok = FuncionesGraficas.pedirBool(
                "Baja empleado",
                "Eliminar definitivamente: " + id + "?");
            if (ok) {
                eliminarBBDD(id);
                cargarDatos();
                limpiarCampos();
                msg = "Empleado " + id + " eliminado.";
            }
        } else {
            msg = "Selecciona un empleado primero.";
        }
        campoMensaje.setText(msg);
    }

    // ── SQL: insertar ─────────────────────────────────────────────────────
    private void insertarBBDD(String id, String nom,
            String tel, String em, double sal,
            String hor, String tip, String log,
            String clv, String esp) {
        if (insSQL == null) {
            insertarRepo(id, nom, tel, em,
                sal, hor, tip, log, clv, esp);
        } else {
            try {
                insSQL.execute(
                    "INSERT INTO empleados VALUES ("
                    + "'" + id + "',"
                    + "'" + nom + "',"
                    + "'" + tel + "',"
                    + "'" + em + "',"
                    + "'" + LocalDate.now() + "',"
                    + sal + ","
                    + "'" + hor + "',"
                    + "'" + tip + "')");
                if ("ADMIN".equals(tip)) {
                    insSQL.execute(
                        "INSERT INTO admins_extra"
                        + " VALUES ("
                        + "'" + id + "',"
                        + "'" + log + "',"
                        + "'" + clv + "')");
                } else {
                    if ("MONITOR".equals(tip)) {
                        insSQL.execute(
                            "INSERT INTO monitores_extra"
                            + " VALUES ("
                            + "'" + id + "',"
                            + "'" + log + "',"
                            + "'" + clv + "',"
                            + "'" + esp + "')");
                    }
                }
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo insertar: "
                    + ex.getMessage());
            }
        }
    }

    // ── SQL: actualizar ───────────────────────────────────────────────────
    private void actualizarBBDD(String id, String nom,
            String tel, String em, double sal,
            String hor, String tip, String log,
            String clv, String esp) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "UPDATE empleados SET"
                    + " nombre='" + nom + "',"
                    + " telefono='" + tel + "',"
                    + " email='" + em + "',"
                    + " salario_base=" + sal + ","
                    + " horario='" + hor + "',"
                    + " tipo='" + tip + "'"
                    + " WHERE id='" + id + "'");
                if ("ADMIN".equals(tip)) {
                    insSQL.execute(
                        "UPDATE admins_extra SET"
                        + " login='" + log + "',"
                        + " password='" + clv + "'"
                        + " WHERE id='" + id + "'");
                } else {
                    if ("MONITOR".equals(tip)) {
                        insSQL.execute(
                            "UPDATE monitores_extra SET"
                            + " login='" + log + "',"
                            + " password='" + clv + "',"
                            + " especialidad='"
                            + esp + "'"
                            + " WHERE id='" + id + "'");
                    }
                }
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo actualizar: "
                    + ex.getMessage());
            }
        }
    }

    // ── SQL: eliminar ─────────────────────────────────────────────────────
    private void eliminarBBDD(String id) {
        if (insSQL != null) {
            try {
                insSQL.execute(
                    "DELETE FROM admins_extra"
                    + " WHERE id='" + id + "'");
                insSQL.execute(
                    "DELETE FROM monitores_extra"
                    + " WHERE id='" + id + "'");
                insSQL.execute(
                    "DELETE FROM clases_grupales"
                    + " WHERE id_monitor='" + id + "'");
                insSQL.execute(
                    "DELETE FROM empleados"
                    + " WHERE id='" + id + "'");
            } catch (SQLException ex) {
                FuncionesGraficas.warning(
                    "Error BD",
                    "No se pudo eliminar: "
                    + ex.getMessage());
            }
        }
    }

    // ── Repo fallback ─────────────────────────────────────────────────────
    private void insertarRepo(String id, String nom,
            String tel, String em, double sal,
            String hor, String tip, String log,
            String clv, String esp) {
        Repositorio repo = Repositorio.getInstancia();
        LocalDate hoy = LocalDate.now();
        if ("ADMIN".equals(tip)) {
            Admin a = new Admin(id, nom, tel, em,
                hoy, sal, hor, log, clv);
            repo.getEmpleados().add(a);
            repo.getAdmins().add(a);
        } else {
            if ("MONITOR".equals(tip)) {
                Monitor m = new Monitor(id, nom,
                    tel, em, hoy, sal, hor,
                    log, clv, esp);
                repo.getEmpleados().add(m);
                repo.getMonitores().add(m);
            } else {
                Empleado emp = new Empleado(
                    id, nom, tel, em,
                    hoy, sal, hor);
                repo.getEmpleados().add(emp);
            }
        }
    }
}
