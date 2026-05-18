package com.gimnasio.gympjv;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Genera QRs en PNG para los socios.
 *
 * @author Ayoub Atidi Belbaz 1º DAM
 */
public class GeneradorQR {

    private static final int ANCHO = 300;
    private static final int ALTO  = 300;

    /** Ruta de la carpeta de QRs, calculada desde user.dir. */
    private static Path carpetaQRs() {
        return Paths.get(
            System.getProperty("user.dir"),
            "src", "main", "resources",
            "imagenes", "qrs");
    }

    /** Genera el QR en PNG y devuelve la ruta absoluta del fichero. */
    public String generarQR(String nombre,
            String apellido, boolean cuota)
            throws Exception {
        String contenido = "Nombre: "   + nombre
            + ", Apellido: " + apellido
            + ", Cuota: "    + cuota;

        Map<EncodeHintType, Object> hints =
            new HashMap<>();
        hints.put(
            EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter()
            .encode(contenido,
                BarcodeFormat.QR_CODE,
                ANCHO, ALTO, hints);

        Path carpeta = carpetaQRs();
        Files.createDirectories(carpeta);

        String ts = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern(
                "yyyyMMddHHmmss"));
        String nSanitizado = nombre.replaceAll(
            "[^a-zA-Z0-9]", "");
        String apSanitizado = apellido.replaceAll(
            "[^a-zA-Z0-9]", "");
        String fichero = "qr_"
            + nSanitizado
            + "_" + apSanitizado
            + "_" + ts + ".png";

        Path ruta = carpeta.resolve(fichero);
        MatrixToImageWriter.writeToPath(
            matrix, "PNG", ruta);

        // devuelvo la ruta absoluta para guardarla en qr_code de la BD
        return ruta.toAbsolutePath().normalize().toString();
    }
}
