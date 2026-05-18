# Diagrama de Clases — Infraestructura (GymPJV)

```mermaid
classDiagram
    direction TB

    %% ── ENTRADA ───────────────────────────────────────────────────────────────

    class App {
        -Stage primaryStage$
        -Scene scene$
        +start(Stage)
        +stop()
        +cargarVista(String)$ Parent
        +cambiarVista(String)$
        +getPrimaryStage()$ Stage
        +main(String[])$
    }

    %% ── SESIÓN ────────────────────────────────────────────────────────────────

    class SesionActual {
        -UsuarioSistema usuarioActual$
        -Socio socioActual$
        +getUsuario()$ UsuarioSistema
        +setUsuario(UsuarioSistema)$
        +getSocio()$ Socio
        +setSocio(Socio)$
        +cerrar()$
        +haySesion()$ boolean
        +esAdmin()$ boolean
        +esMonitor()$ boolean
        +esSocio()$ boolean
    }

    %% ── DATOS EN MEMORIA ──────────────────────────────────────────────────────

    class Repositorio {
        -Repositorio instancia$
        -List~Socio~ socios
        -List~Empleado~ empleados
        -List~Admin~ admins
        -List~Monitor~ monitores
        -List~Suscripcion~ suscripciones
        -List~ClaseGrupal~ clases
        -List~SesionPersonal~ sesionesPersonales
        -List~PlanDieta~ planesDieta
        -List~Reserva~ reservas
        -List~Peluqueria~ serviciosPeluqueria
        -List~Venta~ ventas
        -List~Asistencia~ asistencias
        -GestorStock gestorStock
        -Reportes reportes
        +getInstancia()$ Repositorio
        +cargarDesdeBBDD()
        +cargarDemo()
        +buscarSocioPorId(String) Socio
        +buscarSocioPorQR(String) Socio
        +buscarSocioPorLogin(String) Socio
        +buscarUsuarioPorLogin(String) UsuarioSistema
    }

    %% ── BASE DE DATOS ─────────────────────────────────────────────────────────

    class ConexionBBDD {
        -String URL$
        -String USUARIO$
        -String CLAVE$
        -Connection conexion$
        +getConexion()$ Connection
        +cerrarConexion()$
    }

    class GestorBBDD {
        -DateTimeFormatter FMT_DT$
        +crearTablas(Connection)$
        +estaVacia(Connection, String)$ boolean
        +insertarDatosDemo(Connection)$
        +cargarSocios(Connection)$ List~Socio~
        +cargarEmpleados(Connection)$ List~Empleado~
        +cargarAdmins(Connection)$ List~Admin~
        +cargarMonitores(Connection)$ List~Monitor~
        +cargarSuscripciones(Connection, List)$ List~Suscripcion~
        +cargarClases(Connection, List)$ List~ClaseGrupal~
        +cargarMensualidades(Connection, List)$
        +cargarProductos(Connection)$ List~Producto~
        +insertarSocio(Connection, Socio)$
        +actualizarSocio(Connection, Socio)$
        +eliminarSocio(Connection, String)$
        +insertarEmpleado(Connection, Empleado)$
        +actualizarEmpleado(Connection, Empleado)$
        +eliminarEmpleado(Connection, String)$
        +insertarProducto(Connection, Producto)$
        +actualizarProducto(Connection, Producto)$
        +eliminarProducto(Connection, String)$
        +insertarMensualidad(Connection, Mensualidad)$
    }

    %% ── CONFIGURACIÓN ─────────────────────────────────────────────────────────

    class ConfiguracionApp {
        -ConfiguracionApp instancia$
        -String nombreCentro
        -String direccion
        -String telefono
        -String carpetaBackup
        +getInstancia()$ ConfiguracionApp
        +rutaBackupPorDefecto()$ String
    }

    %% ── QR ────────────────────────────────────────────────────────────────────

    class GeneradorQR {
        -int ANCHO$ = 300
        -int ALTO$ = 300
        +carpetaQRs()$ Path
        +generarQR(String, String, boolean) String
    }

    class QRUtil {
        -int TAMANO_QR$ = 250
        +generarQRSocio(String, String)$ Path
        +leerQR(String)$ String
    }

    %% ── RELACIONES ────────────────────────────────────────────────────────────

    App            ..>  Repositorio      : carga al inicio
    App            ..>  ConexionBBDD     : cierra en stop()
    App            ..>  SesionActual     : navega según sesión

    Repositorio    -->  ConexionBBDD     : usa conexión
    Repositorio    ..>  GestorBBDD       : delega CRUD
    Repositorio    -->  ConfiguracionApp : usa config

    GestorBBDD     -->  ConexionBBDD     : usa Connection

    GeneradorQR    ..>  ConfiguracionApp : ruta QRs
    QRUtil         ..>  GeneradorQR      : alternativa de generación
```

## Notas de diseño

| Clase | Patrón | Detalle |
|-------|--------|---------|
| `Repositorio` | Singleton | Una sola instancia; carga BD o fallback demo |
| `ConexionBBDD` | Singleton | Conexión estática reutilizable |
| `ConfiguracionApp` | Singleton | Centro, dirección, ruta de backup |
| `SesionActual` | Clase estática | Vars estáticas — solo un usuario/socio logueado |
| `GestorBBDD` | Utilidad estática | Solo métodos `static`; usa `Statement` sin `PreparedStatement` |
| `QRUtil` | Utilidad estática | Genera y lee QR vía ZXing |
| `GeneradorQR` | Servicio | Calcula ruta dinámica y escribe PNG |
| `App` | JavaFX Application | Punto de entrada; gestiona Stage/Scene |
