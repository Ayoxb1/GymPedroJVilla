package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import modulos.FuncionesGraficas;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AccesoQRController {

    @FXML private Label lblResultado;
    @FXML private ImageView imgQR;

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
        }
    }

    @FXML
    private void onGenerar() {
        List<Socio> socios = Repositorio.getInstancia().getSocios();
        if (socios.isEmpty()) {
            FuncionesGraficas.warning("Generar QR", "No hay socios.");
            return;
        }
        String[] nombres = socios.stream().map(Socio::getNombre).toArray(String[]::new);
        int idx = FuncionesGraficas.menuDesplegable("Generar QR",
                "Selecciona el socio:", nombres);
        if (idx < 0) return;
        Socio s = socios.get(idx);

        // uso GeneradorQR con ruta dinámica; antes estaba hardcodeada a user.home
        String[] partes = s.getNombre().split(" ", 2);
        String nom = partes[0];
        String ape = "";
        if (partes.length > 1) {
            ape = partes[1];
        }
        boolean cuotaActiva = s.getSuscripcion() != null;
        String rutaGenerada = null;
        try {
            GeneradorQR gen = new GeneradorQR();
            rutaGenerada = gen.generarQR(nom, ape, cuotaActiva);
            s.setQrCode(rutaGenerada);
        } catch (Exception ex) {
            rutaGenerada = null;
        }
        if (rutaGenerada != null) {
            try {
                imgQR.setImage(new Image(
                    new File(rutaGenerada).toURI().toString()));
            } catch (Exception ignored) {}
            lblResultado.setText("QR generado en: " + rutaGenerada);
            FuncionesGraficas.mostrarMensaje("QR generado",
                    "QR del socio " + s.getNombre() + " en:\n"
                    + rutaGenerada, 1);
        } else {
            FuncionesGraficas.warning("Generar QR",
                "No se pudo generar el QR.");
        }
    }

    @FXML
    private void onLeer() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Abrir QR");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File f = fc.showOpenDialog(null);
        if (f == null) return;
        String contenido = QRUtil.leerQR(f.getAbsolutePath());
        if (contenido == null) {
            FuncionesGraficas.warning("Leer QR", "No se pudo leer el QR.");
            return;
        }
        Socio s = Repositorio.getInstancia().buscarSocioPorId(contenido);
        if (s == null) {
            lblResultado.setText("QR leido pero socio no encontrado: " + contenido);
            FuncionesGraficas.warning("Acceso", "Socio no encontrado: " + contenido);
            return;
        }
        if (s.accederQR()) {
            lblResultado.setText("ACCESO PERMITIDO: " + s.getNombre());
            FuncionesGraficas.mostrarMensaje("Acceso QR",
                    "ACCESO PERMITIDO\n" + s.getNombre(), 1);
        } else {
            lblResultado.setText("ACCESO DENEGADO: " + s.getNombre());
            FuncionesGraficas.warning("Acceso QR",
                    "ACCESO DENEGADO\n" + s.getNombre() + "\nSuscripcion no activa.");
        }
        try {
            imgQR.setImage(new Image(f.toURI().toString()));
        } catch (Exception ignored) {}
    }
}
