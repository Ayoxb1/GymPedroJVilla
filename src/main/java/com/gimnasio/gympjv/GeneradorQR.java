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
 * Generador de codigos QR en formato PNG para socios del gimnasio.
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class GeneradorQR {

    private static final int    ANCHO   = 300;
    private static final int    ALTO    = 300;
    private static final String CARPETA =
        "src/main/resources/imagenes/qrs/";

    /**
     * Genera un código QR en formato PNG y devuelve la ruta del fichero generado.
     *
     * @param nombre   Nombre de la persona
     * @param apellido Apellido de la persona
     * @param cuota    Estado de cuota (true/false)
     * @return Ruta absoluta del fichero QR generado
     * @throws Exception
     */
    public String generarQR(String nombre,
            String apellido, boolean cuota)
            throws Exception {
        String contenido = "Nombre: "   + nombre
            + "\nApellido: " + apellido
            + "\nCuota: "   + cuota
            + "\nNota: GymPJV";

        Map<EncodeHintType, Object> hints =
            new HashMap<>();
        hints.put(
            EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter()
            .encode(contenido,
                BarcodeFormat.QR_CODE,
                ANCHO, ALTO, hints);

        Path carpeta = Paths.get(CARPETA);
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

        return ruta.toAbsolutePath().toString();
    }
}
