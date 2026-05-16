package com.gimnasio.gympjv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuracion global del gym.
 * La carpeta de backup por defecto se construye dinamicamente a partir del
 * directorio actual del proyecto (user.dir) para que funcione igual en
 * Linux, Windows y Mac, y se crea al vuelo si no existe.
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class ConfiguracionApp {

    private static ConfiguracionApp instancia;

    private String nombreCentro  = "Pedro J. Villa";
    private String direccion     = "Calle Deportiva, 10 - Madrid";
    private String telefono      = "910000000";
    private String carpetaBackup = rutaBackupPorDefecto();

    private ConfiguracionApp() {}

    public static synchronized ConfiguracionApp getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionApp();
        }
        return instancia;
    }

    /**
     * Devuelve la ruta absoluta de src/main/resources/copias_de_seguridad
     * relativa al directorio del proyecto, creandola si no existe.
     */
    public static String rutaBackupPorDefecto() {
        Path p = Paths.get(
            System.getProperty("user.dir"),
            "src", "main", "resources",
            "copias_de_seguridad");
        try {
            Files.createDirectories(p);
        } catch (Exception ex) {
            // si falla la creacion se devuelve la ruta igualmente
        }
        return p.toAbsolutePath().normalize().toString();
    }

    public String getNombreCentro()  { return nombreCentro; }
    public String getDireccion()     { return direccion; }
    public String getTelefono()      { return telefono; }
    public String getCarpetaBackup() { return carpetaBackup; }

    public void setNombreCentro(String v)  { this.nombreCentro  = v; }
    public void setDireccion(String v)     { this.direccion     = v; }
    public void setTelefono(String v)      { this.telefono      = v; }
    public void setCarpetaBackup(String v) { this.carpetaBackup = v; }
}
