package com.gimnasio.gympjv;

import com.gimnasio.gympjv.App;
import com.gimnasio.gympjv.clases.ClaseGrupal;
import com.gimnasio.gympjv.clases.Reserva;
import com.gimnasio.gympjv.clases.Socio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import modulos.FuncionesGraficas;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Menú del socio. Accede a sus datos, reservas, dieta, QR e historial.
 */
public class MenuSocioController {

    @FXML private BorderPane root;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblFecha;
    @FXML private Button btnDatos;
    @FXML private Button btnSuscripcion;
    @FXML private Button btnDieta;
    @FXML private Button btnReservar;
    @FXML private Button btnReservas;
    @FXML private Button btnComprarBono;
    @FXML private Button btnQR;
    @FXML private Button btnHistorial;
    @FXML private Button btnCerrarSesion;

    @FXML
    private void initialize() {
        if (SesionActual.getSocio() == null) {
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
        Socio s = SesionActual.getSocio();
        if (s != null) {
            lblUsuario.setText(s.getNombre());
            lblRol.setText("SOCIO");
        }
        if (lblFecha != null) {
            lblFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        cargarBienvenida();
    }

    private void cargarBienvenida() {
        Socio s = SesionActual.getSocio();

        VBox cont = new VBox(20);
        cont.getStyleClass().add("dashboard-info-box");
        cont.setPadding(new Insets(36, 40, 36, 40));

        Label titulo = new Label("Bienvenido, " + (s != null ? s.getNombre() : "socio"));
        titulo.getStyleClass().add("titulo-vista");

        String estadoSus    = (s != null && s.getSuscripcion() != null)
                ? s.getSuscripcion().getEstado().name() : "Sin suscripcion";
        String tipoSus      = (s != null && s.getSuscripcion() != null)
                ? s.getSuscripcion().getTipoSuscripcion() : "-";
        String sesiones     = (s != null && s.getSuscripcion() != null)
                ? String.valueOf(s.getSuscripcion().calcularSaldoClases()) : "-";
        String puntos       = s != null ? String.valueOf(s.getPuntos()) : "0";
        String reservas     = s != null ? String.valueOf(s.getReservas().size()) : "0";

        String[][] stats = {
            { "Suscripcion",    tipoSus + "  (" + estadoSus + ")" },
            { "Sesiones disp.", sesiones },
            { "Reservas",       reservas },
            { "Puntos",         puntos },
        };

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(12);
        for (int i = 0; i < stats.length; i++) {
            Label lbl = new Label(stats[i][0]);
            lbl.getStyleClass().add("campo-titulo");
            Label val = new Label(stats[i][1]);
            val.getStyleClass().add("campo-valor-dorado");
            grid.add(lbl, 0, i);
            grid.add(val, 1, i);
        }

        cont.getChildren().addAll(titulo, grid);
        root.setCenter(cont);
    }

    private void cargarVista(String nombre) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gimnasio/gympjv/fxml/" + nombre + ".fxml"));
            Parent vista = loader.load();
            root.setCenter(vista);
        } catch (IOException ex) {
            FuncionesGraficas.warning("Error", "No se pudo cargar " + nombre + ": " + ex.getMessage());
        }
    }

    @FXML private void onDatos(ActionEvent e)        { cargarVista("DatosSocio"); }
    @FXML private void onSuscripcion(ActionEvent e)  { cargarVista("MiSuscripcion"); }
    @FXML private void onDieta(ActionEvent e)        { cargarVista("MiDieta"); }
    @FXML private void onReservas(ActionEvent e)     { cargarVista("MisReservas"); }
    @FXML private void onQR(ActionEvent e)           { cargarVista("MiQR"); }
    @FXML private void onHistorial(ActionEvent e)    { cargarVista("MiHistorial"); }

    /** Reserva una clase para el socio en sesión. */
    @FXML
    private void onReservar(ActionEvent e) {
        Socio s = SesionActual.getSocio();
        if (s == null) return;

        if (s.getSuscripcion() == null
                || s.getSuscripcion().getEstado()
                   != com.gimnasio.gympjv.clases.enums.EstadoSuscripcion.ACTIVA) {
            FuncionesGraficas.warning("Reservar clase",
                    "Necesitas una suscripcion activa para reservar.");
            return;
        }
        if (!s.tieneSesiones()) {
            FuncionesGraficas.warning("Reservar clase",
                    "No te quedan sesiones disponibles. Compra un bono.");
            return;
        }

        // solo clases futuras con plazas libres
        Repositorio repo = Repositorio.getInstancia();
        List<ClaseGrupal> disponibles = repo.getClases().stream()
                .filter(c -> !c.estaLlena())
                .filter(c -> c.getFecha().isAfter(LocalDateTime.now()))
                .toList();

        if (disponibles.isEmpty()) {
            FuncionesGraficas.warning("Reservar clase", "No hay clases disponibles.");
            return;
        }

        String[] nombres = new String[disponibles.size()];
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        for (int i = 0; i < disponibles.size(); i++) {
            ClaseGrupal c = disponibles.get(i);
            nombres[i] = c.getNombre() + "  -  " + c.getFecha().format(fmt)
                    + "  ("  + c.getOcupacionActual() + "/" + c.getAforoMaximo() + ")";
        }
        int idx = FuncionesGraficas.menuDesplegable("Reservar clase",
                "Selecciona la clase a reservar:", nombres);
        if (idx < 0 || idx >= disponibles.size()) return;

        ClaseGrupal clase = disponibles.get(idx);
        String idRes = "RES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Reserva r = new Reserva(idRes, s, clase, LocalDateTime.now());
        if (r.confirmar()) {
            repo.getReservas().add(r);
            s.anadirReserva(r);
            FuncionesGraficas.mostrarMensaje("Reserva",
                    "Reserva confirmada en " + clase.getNombre() + ".", 1);
            cargarVista("MisReservas");
        } else {
            FuncionesGraficas.warning("Reserva",
                    "No se ha podido confirmar la reserva.");
        }
    }

    /** Compra un bono de sesiones. */
    @FXML
    private void onComprarBono(ActionEvent e) {
        Socio s = SesionActual.getSocio();
        if (s == null) return;

        if (s.getSuscripcion() == null) {
            FuncionesGraficas.warning("Comprar bono",
                    "Necesitas una suscripcion activa para comprar bonos.");
            return;
        }

        String[] opciones = {"Bono 10 sesiones (50 EUR)", "Bono 20 sesiones (90 EUR)"};
        int idx = FuncionesGraficas.menuDesplegable("Comprar bono",
                "Selecciona el bono que quieres comprar:", opciones);
        if (idx < 0) return;
        int sesiones = (idx == 0) ? 10 : 20;
        if (s.comprarBono(sesiones)) {
            FuncionesGraficas.mostrarMensaje("Bono comprado",
                    "Bono de " + sesiones + " sesiones anadido a tu suscripcion.\n"
                    + "Saldo total: " + s.getSuscripcion().calcularSaldoClases() + " sesiones.", 1);
        } else {
            FuncionesGraficas.warning("Comprar bono", "No se pudo comprar el bono.");
        }
    }

    @FXML
    private void onCerrarSesion(ActionEvent e) {
        boolean confirmar = FuncionesGraficas.pedirBool("Cerrar sesion", "Salir de la sesion actual?");
        if (confirmar) {
            SesionActual.cerrar();
            try {
                App.cambiarVista("InicioSesion");
            } catch (IOException ex) {
                FuncionesGraficas.warning("Error", "No se pudo volver al login: " + ex.getMessage());
            }
        }
    }
}
