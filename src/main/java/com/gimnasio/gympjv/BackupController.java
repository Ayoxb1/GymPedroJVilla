package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Socio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import modulos.FuncionesGraficas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class BackupController {

    @FXML private Label lblCarpeta;
    @FXML private TextArea txtLog;

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
        lblCarpeta.setText("Carpeta: "
            + ConfiguracionApp.getInstancia()
                .getCarpetaBackup());
    }

    @FXML
    private void onExportar() {
        String carpeta = ConfiguracionApp.getInstancia().getCarpetaBackup();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File archivo = new File(carpeta, "backup_socios_" + ts + ".csv");

        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write("id;nombre;telefono;email;fechaAlta;qrCode;login\n");
            for (Socio s : Repositorio.getInstancia().getSocios()) {
                fw.write(s.getId() + ";"
                        + s.getNombre() + ";"
                        + s.getTelefono() + ";"
                        + s.getEmail() + ";"
                        + s.getFechaAlta() + ";"
                        + s.getQrCode() + ";"
                        + (s.getLogin() != null ? s.getLogin() : "") + "\n");
            }
            log("Exportados " + Repositorio.getInstancia().getSocios().size()
                    + " socios → " + archivo.getAbsolutePath());
            FuncionesGraficas.mostrarMensaje("Backup", "Exportacion completada:\n" + archivo.getAbsolutePath(), 1);
        } catch (IOException ex) {
            log("ERROR exportando: " + ex.getMessage());
            FuncionesGraficas.warning("Backup", "Error al exportar: " + ex.getMessage());
        }
    }

    @FXML
    private void onImportar() {
        String rutaStr = FuncionesGraficas.pedirDatos("Importar socios",
                "Ruta del archivo CSV (ej: " + ConfiguracionApp.getInstancia().getCarpetaBackup()
                + File.separator + "backup_socios.csv):");
        if (rutaStr == null || rutaStr.isBlank()) return;

        File archivo = new File(rutaStr.trim());
        if (!archivo.exists()) {
            FuncionesGraficas.warning("Importar", "Archivo no encontrado: " + rutaStr);
            return;
        }

        List<Socio> socios = Repositorio.getInstancia().getSocios();
        int importados = 0;
        int omitidos   = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine(); // cabecera
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";", -1);
                if (partes.length < 6) { omitidos++; continue; }
                String id    = partes[0].trim();
                // Evitar duplicados por ID
                boolean existe = false;
                for (Socio s : socios) {
                    if (s.getId().equals(id)) { existe = true; break; }
                }
                if (existe) { omitidos++; continue; }

                String nombre   = partes[1].trim();
                String tel      = partes[2].trim();
                String email    = partes[3].trim();
                LocalDate fecha;
                try { fecha = LocalDate.parse(partes[4].trim()); }
                catch (Exception ex) { fecha = LocalDate.now(); }
                String qr       = partes[5].trim();
                String login    = partes.length > 6 ? partes[6].trim() : nombre;

                Socio nuevo = new Socio(id, nombre, tel, email, fecha, qr, login, "1234");
                socios.add(nuevo);
                importados++;
            }
            log("Importados: " + importados + " socios. Omitidos (duplicados/error): " + omitidos);
            FuncionesGraficas.mostrarMensaje("Importar",
                    "Importacion completada.\nImportados: " + importados
                    + "  Omitidos: " + omitidos, 1);
        } catch (IOException ex) {
            log("ERROR importando: " + ex.getMessage());
            FuncionesGraficas.warning("Importar", "Error al leer el archivo: " + ex.getMessage());
        }
    }

    @FXML
    private void onLimpiarLog() {
        txtLog.clear();
    }

    private void log(String msg) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        txtLog.appendText("[" + ts + "] " + msg + "\n");
    }
}
