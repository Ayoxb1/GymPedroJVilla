package com.gimnasio.gympjv;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Genera y lee QRs. El QR solo lleva el ID del socio, no datos personales.
 */
public final class QRUtil {

    private static final int TAMANO_QR = 250;

    private QRUtil() {}

    /** Genera el QR del socio (solo lleva el ID) y lo guarda en rutaDestino. Null si falla. */
    public static Path generarQRSocio(String idSocio, String rutaDestino) {
        if (idSocio == null || idSocio.isEmpty() || rutaDestino == null) {
            return null;
        }
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    idSocio, BarcodeFormat.QR_CODE, TAMANO_QR, TAMANO_QR);
            Path ruta = Paths.get(rutaDestino);
            File parent = ruta.toFile().getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            MatrixToImageWriter.writeToPath(matrix, "PNG", ruta);
            return ruta.toAbsolutePath();
        } catch (Exception ex) {
            return null;
        }
    }

    /** Lee el QR del PNG y devuelve su contenido (el ID del socio). Null si falla. */
    public static String leerQR(String rutaFichero) {
        if (rutaFichero == null) {
            return null;
        }
        try {
            BufferedImage imagen = ImageIO.read(new File(rutaFichero));
            if (imagen == null) {
                return null;
            }
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(imagen);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (IOException | com.google.zxing.NotFoundException ex) {
            return null;
        }
    }
}
