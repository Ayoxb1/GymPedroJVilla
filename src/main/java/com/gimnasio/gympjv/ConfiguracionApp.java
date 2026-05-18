package com.gimnasio.gympjv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config del gimnasio. La carpeta de backup se calcula desde user.dir
 * para que funcione en cualquier OS.
 *
 * @author Ayoub Atidi Belbaz 1º DAM
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

    /** Ruta de backup relativa al proyecto, la crea si no existe. */
    public static String rutaBackupPorDefecto() {
        Path p = Paths.get(
            System.getProperty("user.dir"),
            "src", "main", "resources",
            "copias_de_seguridad");
        try {
            Files.createDirectories(p);
        } catch (Exception ex) {
            // si falla la creación devuelvo la ruta igual
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
