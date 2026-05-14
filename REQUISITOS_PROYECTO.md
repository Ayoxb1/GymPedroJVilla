# REQUISITOS Y REGLAS DEL PROYECTO: Pedro J. Villa App

## 1. ESTADO ACTUAL Y OBJETIVO
El proyecto es una aplicación de gestión para un centro deportivo/peluquería. Actualmente funciona en memoria (62% completado). El objetivo es implementar el 38% restante (Fases A-E) siguiendo estrictamente las normativas de código del profesor evaluador.

## 2. REGLAS ARQUITECTÓNICAS Y DE ESTILO (INQUEBRANTABLES)
Cualquier código nuevo o refactorizado DEBE cumplir estas reglas para aprobar la evaluación:

* **Paso de Datos:** PROHIBIDO usar Singleton para compartir datos entre ventanas. Antes de abrir un FXML, se debe llamar al método estático `public static void datosEntrada(...)` del controlador de destino.
* **Carga de Vistas:** Los FXML se cargan con concatenación literal: `new FXMLLoader(App.class.getResource("NombreVentana" + ".fxml"));`. Toda ventana modal usa `initModality(APPLICATION_MODAL)`, `setResizable(false)` y bloquea el cierre con la 'X' lanzando un `warning`.
* **Base de Datos (Fase A):** PROHIBIDO usar `PreparedStatement` u ORMs. Las consultas SQL se hacen por concatenación directa de Strings. Los datos del `ResultSet` se extraen por índice numérico (`getInt(1)`, `getString(2)`).
* **Estilo de Código:**
    * Máximo 80 caracteres por línea.
    * La llave de apertura `{` va al final de la misma línea inicial. Usa llaves siempre.
    * **PROHIBIDO usar `else if`.** Si hay múltiples condiciones, escribe `else { if (...) { } }`.
    * Un único `return` situado al final de cada método.
* **UI y Alertas:** Estética negro/dorado sencilla. Uso obligatorio de `modulos.FuncionesGraficas` para cualquier alerta o diálogo interactivo.

* **Empaquetado y Estructura de Carpetas:** PROHIBIDO crear paquetes con arquitectura tradicional (no uses `modelo`, `vista`, ni `controlador`). 
    * Los Controladores van en el paquete raíz principal.
    * Las entidades de negocio (POJOs) van ESTRICTAMENTE en un subpaquete llamado `clases/`.
    * Los archivos `.fxml` deben ir en `src/main/resources/` espejando exactamente la ruta del paquete Java.
* **Empaquetado y Estructura de Carpetas (Lectura Obligatoria):** PROHIBIDO crear paquetes con arquitectura tradicional (no uses `modelo`, `vista`, ni `controlador`). Debes imitar estrictamente la siguiente estructura de árbol (`tree`) del proyecto original del profesor:

    
    ├── pom.xml
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   ├── com
    │   │   │   │   └── mycompany
    │   │   │   │       └── aplicacionjavafx2526
    │   │   │   │           ├── AgendaController.java  <-- Controladores en la raíz del paquete
    │   │   │   │           ├── App.java               <-- Main en la raíz del paquete
    │   │   │   │           └── clases
    │   │   │   │               └── Persona.java       <-- POJOs ESTRICTAMENTE en el subpaquete clases/
    │   │   │   ├── ficheros
    │   │   │   │   └── datos.dat
    │   │   │   └── module-info.java
    │   │   └── resources
    │   │       ├── com
    │   │       │   └── mycompany
    │   │       │       └── aplicacionjavafx2526
    │   │       │           ├── Agenda.fxml        <-- FXML espejando la ruta del Controller
    │   │       │           └── VentanaInicial.fxml
    │   │       └── imagenes
    │   │           └── qrs/                       <-- (NUEVO) Aquí se guardarán los .png de ZXing
    


## 3. ROADMAP DE IMPLEMENTACIÓN (FASES PENDIENTES)

### FASE A: Persistencia MySQL (PRIORIDAD CRÍTICA)
1. Conectar `mysql-connector-j` en `pom.xml`.
2. Crear clase estática para conexión JDBC pura.
3. Implementar métodos de acceso a datos concatenando Strings, extrayendo datos y poblando las listas en memoria al arrancar la app.
4. Asegurar el cierre de conexión al salir.
5. Al generar el QR, el fichero PNG físico se DEBE guardar obligatoriamente en una carpeta dedicada: `src/main/resources/imagenes/qrs/`. 
    * El código de la clase `GeneradorQR` debe comprobar si esta carpeta existe y, si no existe, crearla automáticamente con `Files.createDirectories()` antes de guardar la imagen.
    * Solo la ruta relativa o nombre del fichero se guardará como un `String` en la base de datos MySQL.

### FASE B: Menús Faltantes y Roles
1. Crear `MenuMonitor.fxml` y su controlador (Mis Clases, Asistencia, Dietas).
2. Rehacer el CRUD de Empleados, Admins y Monitores (alta/baja/modificación completa).
3. Implementar la vista de cobro de cuotas para el Admin.

### FASE C y D: UI Visual y Mockups
1. Añadir JavaFX Charts (BarChart, PieChart) en el panel de Reportes.
2. Implementar calendario semanal visual para clases.
3. Añadir columnas faltantes (Cuota, Estado) en la vista de Socios.
4. Ajustar el TPV (botones grandes) y añadir logo real.

### FASE E: Seguridad
1. Validar sesión activa en el `initialize()` de cada controlador.
2. Evitar duplicados de login al crear socios.

### FASE F: Generación y Gestión de Códigos QR (ZXing)
Para la funcionalidad de acceso QR, se debe utilizar la librería **Java ZXing** respetando el siguiente flujo:
1. **Generación:** Crear una clase `GeneradorQR` que genere un fichero PNG.
2. **Contenido:** El QR debe contener la información del objeto (Nombre, Apellido, Cuota pagada y Nota).
3. **Persistencia:** Al hacer el `insert` de un Socio mediante el formulario, el fichero PNG físico se debe guardar en la estructura de directorios del proyecto (ej. `src/...`) y la **ruta de ese fichero** se debe guardar como un `String` en la base de datos.
4. **Firma y Javadoc Obligatorio:** El método generador debe respetar exactamente esta estructura y Javadoc:

    java
    public class GeneradorQR {
        /**
         * Genera un código QR en formato PNG y devuelve la ruta del fichero generado.
         *
         * @param nombre   Nombre de la persona
         * @param apellido Apellido de la persona
         * @param cuota    Estado de cuota (true/false)
         * @return Ruta absoluta del fichero QR generado
         * @throws Exception
         */
        public String generarQR(String nombre, String apellido, boolean cuota) {
            // Implementación con llaves en la misma línea y ZXing
        }
    }
    
