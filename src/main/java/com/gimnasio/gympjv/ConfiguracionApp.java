package com.gimnasio.gympjv;

/**
 * Configuracion global del centro deportivo. Singleton.
 */
public class ConfiguracionApp {

    private static ConfiguracionApp instancia;

    private String nombreCentro  = "Pedro J. Villa";
    private String direccion     = "Calle Deportiva, 10 - Madrid";
    private String telefono      = "910000000";
    private String carpetaBackup = System.getProperty("user.home");

    private ConfiguracionApp() {}

    public static synchronized ConfiguracionApp getInstancia() {
        if (instancia == null) instancia = new ConfiguracionApp();
        return instancia;
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
