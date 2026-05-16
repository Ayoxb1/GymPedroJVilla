package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.Admin;
import com.gimnasio.gympjv.clases.ClaseGrupal;
import com.gimnasio.gympjv.clases.Empleado;
import com.gimnasio.gympjv.clases.Monitor;
import com.gimnasio.gympjv.clases.Producto;
import com.gimnasio.gympjv.clases.Socio;
import com.gimnasio.gympjv.clases.Suscripcion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones JDBC contra MySQL para todas las entidades.
 * REGLA: solo Statement (sin PreparedStatement),
 * SQL por concatenacion de Strings,
 * ResultSet extraido por indice numerico.
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class GestorBBDD {

    private static final DateTimeFormatter FMT_DT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GestorBBDD() { }

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZACION DE TABLAS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea las tablas de la BD si no existen todavia.
     * Orden: empleados antes que admins_extra/monitores_extra
     * (claves foraneas).
     */
    public static void crearTablas(Connection c) throws SQLException {
        Statement s = c.createStatement();

        s.execute(
            "CREATE TABLE IF NOT EXISTS socios ("
            + "id VARCHAR(20) PRIMARY KEY,"
            + "nombre VARCHAR(100),"
            + "telefono VARCHAR(20),"
            + "email VARCHAR(100),"
            + "fecha_alta DATE,"
            + "qr_code VARCHAR(200),"
            + "login VARCHAR(50),"
            + "password VARCHAR(50),"
            + "puntos INT DEFAULT 0)");

        s.execute(
            "CREATE TABLE IF NOT EXISTS empleados ("
            + "id VARCHAR(20) PRIMARY KEY,"
            + "nombre VARCHAR(100),"
            + "telefono VARCHAR(20),"
            + "email VARCHAR(100),"
            + "fecha_alta DATE,"
            + "salario_base DOUBLE,"
            + "horario VARCHAR(20),"
            + "tipo VARCHAR(20) DEFAULT 'EMPLEADO')");

        s.execute(
            "CREATE TABLE IF NOT EXISTS admins_extra ("
            + "id VARCHAR(20) PRIMARY KEY,"
            + "login VARCHAR(50),"
            + "password VARCHAR(50),"
            + "FOREIGN KEY (id) REFERENCES empleados(id))");

        s.execute(
            "CREATE TABLE IF NOT EXISTS monitores_extra ("
            + "id VARCHAR(20) PRIMARY KEY,"
            + "login VARCHAR(50),"
            + "password VARCHAR(50),"
            + "especialidad VARCHAR(100),"
            + "FOREIGN KEY (id) REFERENCES empleados(id))");

        s.execute(
            "CREATE TABLE IF NOT EXISTS productos ("
            + "id_producto VARCHAR(20) PRIMARY KEY,"
            + "nombre VARCHAR(100),"
            + "precio_venta DOUBLE,"
            + "stock INT,"
            + "stock_minimo INT)");

        s.execute(
            "CREATE TABLE IF NOT EXISTS suscripciones ("
            + "id VARCHAR(20) PRIMARY KEY,"
            + "id_socio VARCHAR(20),"
            + "tipo VARCHAR(50),"
            + "precio DOUBLE,"
            + "fecha_inicio DATE,"
            + "fecha_fin DATE,"
            + "clases_incluidas INT,"
            + "FOREIGN KEY (id_socio) REFERENCES socios(id))");

        s.execute(
            "CREATE TABLE IF NOT EXISTS clases_grupales ("
            + "id_clase VARCHAR(20) PRIMARY KEY,"
            + "nombre VARCHAR(100),"
            + "fecha_hora DATETIME,"
            + "id_monitor VARCHAR(20),"
            + "aforo_maximo INT,"
            + "ocupacion_actual INT DEFAULT 0,"
            + "FOREIGN KEY (id_monitor)"
            + " REFERENCES empleados(id))");

        s.execute(
            "CREATE TABLE IF NOT EXISTS mensualidades ("
            + "id_mensualidad VARCHAR(20) PRIMARY KEY,"
            + "id_suscripcion VARCHAR(20),"
            + "importe DOUBLE,"
            + "fecha_vencimiento DATE,"
            + "estado VARCHAR(20),"
            + "FOREIGN KEY (id_suscripcion)"
            + " REFERENCES suscripciones(id))");

        s.close();
    }

    /**
     * Comprueba si la tabla indicada esta vacia.
     * Se usa para saber si hay que insertar datos de demo.
     */
    public static boolean estaVacia(Connection c, String tabla)
            throws SQLException {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(
            "SELECT COUNT(*) FROM " + tabla);
        rs.next();
        int total = rs.getInt(1);
        rs.close();
        s.close();
        return total == 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INSERCION DE DATOS DE DEMO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Popula todas las tablas con los datos de demo equivalentes
     * al metodo Repositorio.cargarDemo(). Solo se llama si la BD
     * esta vacia (primera ejecucion).
     */
    public static void insertarDatosDemo(Connection c)
            throws SQLException {
        insertarDemoEmpleados(c);
        insertarDemoSocios(c);
        insertarDemoProductos(c);
        insertarDemoSuscripciones(c);
        insertarDemoClases(c);
    }

    private static void insertarDemoEmpleados(Connection c)
            throws SQLException {
        Statement s = c.createStatement();

        // ── Empleados base ────────────────────────────────────────
        s.execute(
            "INSERT INTO empleados VALUES ("
            + "'ADM-001','Ayoub Atidi Belbaz','611000000',"
            + "'ayoub.admin@gym.com','2024-01-10',2800.0,"
            + "'MANANA','ADMIN')");

        s.execute(
            "INSERT INTO empleados VALUES ("
            + "'MON-001','Laura Martinez','611000002',"
            + "'laura@gym.com','2023-03-01',1900.0,"
            + "'MANANA','MONITOR')");

        s.execute(
            "INSERT INTO empleados VALUES ("
            + "'MON-002','Javier Sanchez','611000003',"
            + "'javier@gym.com','2023-06-15',1900.0,"
            + "'TARDE','MONITOR')");

        s.execute(
            "INSERT INTO empleados VALUES ("
            + "'EMP-001','Marta Lopez','611000004',"
            + "'marta@gym.com','2024-01-05',1700.0,"
            + "'TARDE','EMPLEADO')");

        // ── Credenciales admin ────────────────────────────────────
        s.execute(
            "INSERT INTO admins_extra VALUES ("
            + "'ADM-001','Ayoub','1234')");

        // ── Credenciales monitores ────────────────────────────────
        s.execute(
            "INSERT INTO monitores_extra VALUES ("
            + "'MON-001','laura','1234','Yoga / Pilates')");

        s.execute(
            "INSERT INTO monitores_extra VALUES ("
            + "'MON-002','javier','1234',"
            + "'CrossFit / Musculacion')");

        s.close();
    }

    private static void insertarDemoSocios(Connection c)
            throws SQLException {
        Statement s = c.createStatement();

        s.execute(
            "INSERT INTO socios VALUES ("
            + "'SOC-001','Ayoub Atidi Belbaz','612000001',"
            + "'ayoub.socio@email.com','2024-09-01',"
            + "'QR-SOC-001','Ayoub Atidi Belbaz','1234',0)");

        s.execute(
            "INSERT INTO socios VALUES ("
            + "'SOC-002','Pedro Fernandez','612000002',"
            + "'pedro@email.com','2024-03-20',"
            + "'QR-SOC-002','pedro','1234',0)");

        s.execute(
            "INSERT INTO socios VALUES ("
            + "'SOC-003','Maria Gonzalez','612000003',"
            + "'maria@email.com','2025-02-01',"
            + "'QR-SOC-003','maria','1234',0)");

        s.execute(
            "INSERT INTO socios VALUES ("
            + "'SOC-004','Luis Torres','612000004',"
            + "'luis@email.com','2025-04-15',"
            + "'QR-SOC-004','luis','1234',0)");

        s.close();
    }

    private static void insertarDemoProductos(Connection c)
            throws SQLException {
        Statement s = c.createStatement();

        s.execute(
            "INSERT INTO productos VALUES ("
            + "'PROD-001','Proteina Whey 1kg',39.99,50,5)");
        s.execute(
            "INSERT INTO productos VALUES ("
            + "'PROD-002','Creatina 500g',24.99,30,5)");
        s.execute(
            "INSERT INTO productos VALUES ("
            + "'PROD-003','Botella Termica',14.99,40,5)");
        s.execute(
            "INSERT INTO productos VALUES ("
            + "'PROD-004','Camiseta Tecnica',24.99,60,5)");
        s.execute(
            "INSERT INTO productos VALUES ("
            + "'PROD-005','Bebida Isotonica',2.50,100,10)");
        s.execute(
            "INSERT INTO productos VALUES ("
            + "'PROD-006','Champu Profesional',12.50,25,5)");

        s.close();
    }

    private static void insertarDemoSuscripciones(Connection c)
            throws SQLException {
        Statement s = c.createStatement();
        LocalDate hoy = LocalDate.now();

        String ini001 = hoy.minusDays(10).toString();
        String fin001 = hoy.plusDays(20).toString();
        s.execute(
            "INSERT INTO suscripciones VALUES ("
            + "'SUS-001','SOC-001','Mensual',45.0,"
            + "'" + ini001 + "','" + fin001 + "',12)");

        String ini002 = hoy.minusMonths(2).toString();
        String fin002 = hoy.plusMonths(1).toString();
        s.execute(
            "INSERT INTO suscripciones VALUES ("
            + "'SUS-002','SOC-002','Trimestral',120.0,"
            + "'" + ini002 + "','" + fin002 + "',36)");

        String ini003 = hoy.minusMonths(3).toString();
        String fin003 = hoy.plusMonths(9).toString();
        s.execute(
            "INSERT INTO suscripciones VALUES ("
            + "'SUS-003','SOC-003','Anual',400.0,"
            + "'" + ini003 + "','" + fin003 + "',200)");

        s.close();
    }

    private static void insertarDemoClases(Connection c)
            throws SQLException {
        Statement s = c.createStatement();
        LocalDate hoy = LocalDate.now();

        String cl1 = hoy.plusDays(1).atTime(9, 0).format(FMT_DT);
        s.execute(
            "INSERT INTO clases_grupales VALUES ("
            + "'CLA-001','Yoga Matutino',"
            + "'" + cl1 + "','MON-001',15,0)");

        String cl2 = hoy.plusDays(2).atTime(18, 30).format(FMT_DT);
        s.execute(
            "INSERT INTO clases_grupales VALUES ("
            + "'CLA-002','CrossFit Intensivo',"
            + "'" + cl2 + "','MON-002',20,0)");

        String cl3 = hoy.plusDays(3).atTime(10, 30).format(FMT_DT);
        s.execute(
            "INSERT INTO clases_grupales VALUES ("
            + "'CLA-003','Pilates Core',"
            + "'" + cl3 + "','MON-001',12,0)");

        String cl4 = hoy.plusDays(1).atTime(19, 30).format(FMT_DT);
        s.execute(
            "INSERT INTO clases_grupales VALUES ("
            + "'CLA-004','Body Pump',"
            + "'" + cl4 + "','MON-002',25,0)");

        s.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LECTURA (CARGA EN MEMORIA)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Carga todos los socios desde la tabla socios.
     * Columnas: 1=id, 2=nombre, 3=telefono, 4=email,
     *           5=fecha_alta, 6=qr_code, 7=login,
     *           8=password, 9=puntos
     */
    public static List<Socio> cargarSocios(Connection c)
            throws SQLException {
        List<Socio> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT id, nombre, telefono, email,"
            + " fecha_alta, qr_code, login, password, puntos"
            + " FROM socios";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id       = rs.getString(1);
            String nombre   = rs.getString(2);
            String tel      = rs.getString(3);
            String email    = rs.getString(4);
            LocalDate alta  = rs.getDate(5).toLocalDate();
            String qr       = rs.getString(6);
            String login    = rs.getString(7);
            String pass     = rs.getString(8);
            int puntos      = rs.getInt(9);
            Socio socio = new Socio(
                id, nombre, tel, email, alta, qr, login, pass);
            socio.setPuntos(puntos);
            lista.add(socio);
        }
        rs.close();
        s.close();
        return lista;
    }

    /**
     * Carga todos los admins (JOIN empleados + admins_extra).
     * Columnas: 1=id, 2=nombre, 3=telefono, 4=email,
     *           5=fecha_alta, 6=salario_base, 7=horario,
     *           8=login, 9=password
     */
    public static List<Admin> cargarAdmins(Connection c)
            throws SQLException {
        List<Admin> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT e.id, e.nombre, e.telefono, e.email,"
            + " e.fecha_alta, e.salario_base, e.horario,"
            + " a.login, a.password"
            + " FROM empleados e"
            + " JOIN admins_extra a ON e.id = a.id";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id       = rs.getString(1);
            String nombre   = rs.getString(2);
            String tel      = rs.getString(3);
            String email    = rs.getString(4);
            LocalDate alta  = rs.getDate(5).toLocalDate();
            double salario  = rs.getDouble(6);
            String horario  = rs.getString(7);
            String login    = rs.getString(8);
            String pass     = rs.getString(9);
            Admin admin = new Admin(
                id, nombre, tel, email, alta,
                salario, horario, login, pass);
            lista.add(admin);
        }
        rs.close();
        s.close();
        return lista;
    }

    /**
     * Carga todos los monitores (JOIN empleados + monitores_extra).
     * Columnas: 1=id, 2=nombre, 3=telefono, 4=email,
     *           5=fecha_alta, 6=salario_base, 7=horario,
     *           8=login, 9=password, 10=especialidad
     */
    public static List<Monitor> cargarMonitores(Connection c)
            throws SQLException {
        List<Monitor> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT e.id, e.nombre, e.telefono, e.email,"
            + " e.fecha_alta, e.salario_base, e.horario,"
            + " m.login, m.password, m.especialidad"
            + " FROM empleados e"
            + " JOIN monitores_extra m ON e.id = m.id";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id           = rs.getString(1);
            String nombre       = rs.getString(2);
            String tel          = rs.getString(3);
            String email        = rs.getString(4);
            LocalDate alta      = rs.getDate(5).toLocalDate();
            double salario      = rs.getDouble(6);
            String horario      = rs.getString(7);
            String login        = rs.getString(8);
            String pass         = rs.getString(9);
            String especialidad = rs.getString(10);
            Monitor monitor = new Monitor(
                id, nombre, tel, email, alta,
                salario, horario, login, pass, especialidad);
            lista.add(monitor);
        }
        rs.close();
        s.close();
        return lista;
    }

    /**
     * Carga los empleados de tipo EMPLEADO (sin credenciales
     * de acceso al sistema: peluqueros, etc.).
     * Columnas: 1=id, 2=nombre, 3=telefono, 4=email,
     *           5=fecha_alta, 6=salario_base, 7=horario
     */
    public static List<Empleado> cargarEmpleadosBase(Connection c)
            throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT id, nombre, telefono, email,"
            + " fecha_alta, salario_base, horario"
            + " FROM empleados WHERE tipo = 'EMPLEADO'";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id       = rs.getString(1);
            String nombre   = rs.getString(2);
            String tel      = rs.getString(3);
            String email    = rs.getString(4);
            LocalDate alta  = rs.getDate(5).toLocalDate();
            double salario  = rs.getDouble(6);
            String horario  = rs.getString(7);
            Empleado emp = new Empleado(
                id, nombre, tel, email, alta, salario, horario);
            lista.add(emp);
        }
        rs.close();
        s.close();
        return lista;
    }

    /**
     * Carga todos los productos del catalogo.
     * Columnas: 1=id_producto, 2=nombre, 3=precio_venta,
     *           4=stock, 5=stock_minimo
     */
    public static List<Producto> cargarProductos(Connection c)
            throws SQLException {
        List<Producto> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT id_producto, nombre, precio_venta,"
            + " stock, stock_minimo FROM productos";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id    = rs.getString(1);
            String nom   = rs.getString(2);
            double prec  = rs.getDouble(3);
            int stock    = rs.getInt(4);
            int stockMin = rs.getInt(5);
            Producto p = new Producto(id, nom, prec, stock, stockMin);
            lista.add(p);
        }
        rs.close();
        s.close();
        return lista;
    }

    /**
     * Carga las suscripciones y las vincula al socio correspondiente.
     * Columnas: 1=id, 2=id_socio, 3=tipo, 4=precio,
     *           5=fecha_inicio, 6=fecha_fin, 7=clases_incluidas
     */
    public static List<Suscripcion> cargarSuscripciones(
            Connection c, List<Socio> socios) throws SQLException {
        List<Suscripcion> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT id, id_socio, tipo, precio,"
            + " fecha_inicio, fecha_fin, clases_incluidas"
            + " FROM suscripciones";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id        = rs.getString(1);
            String idSocio   = rs.getString(2);
            String tipo      = rs.getString(3);
            double precio    = rs.getDouble(4);
            LocalDate inicio = rs.getDate(5).toLocalDate();
            LocalDate fin    = rs.getDate(6).toLocalDate();
            int clases       = rs.getInt(7);
            Socio socio = buscarSocioEnLista(socios, idSocio);
            if (socio != null) {
                Suscripcion sus = new Suscripcion(
                    id, socio, tipo, precio, inicio, fin, clases);
                socio.setSuscripcion(sus);
                lista.add(sus);
            }
        }
        rs.close();
        s.close();
        return lista;
    }

    /**
     * Carga las clases grupales y las vincula al monitor.
     * Columnas: 1=id_clase, 2=nombre, 3=fecha_hora,
     *           4=id_monitor, 5=aforo_maximo, 6=ocupacion_actual
     */
    public static List<ClaseGrupal> cargarClases(
            Connection c, List<Monitor> monitores)
            throws SQLException {
        List<ClaseGrupal> lista = new ArrayList<>();
        Statement s = c.createStatement();
        String sql =
            "SELECT id_clase, nombre, fecha_hora,"
            + " id_monitor, aforo_maximo, ocupacion_actual"
            + " FROM clases_grupales";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String id       = rs.getString(1);
            String nombre   = rs.getString(2);
            LocalDateTime dt =
                rs.getTimestamp(3).toLocalDateTime();
            String idMon    = rs.getString(4);
            int aforo       = rs.getInt(5);
            Monitor mon = buscarMonitorEnLista(monitores, idMon);
            if (mon != null) {
                ClaseGrupal clase = new ClaseGrupal(
                    id, nombre, dt, mon, aforo);
                mon.anadirClase(clase);
                lista.add(clase);
            }
        }
        rs.close();
        s.close();
        return lista;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ESCRITURA: SOCIOS
    // ─────────────────────────────────────────────────────────────────────────

    /** Inserta un nuevo socio en la BD. */
    public static void insertarSocio(Connection c, Socio socio)
            throws SQLException {
        Statement s = c.createStatement();
        String sql =
            "INSERT INTO socios VALUES ("
            + "'" + socio.getId() + "',"
            + "'" + socio.getNombre() + "',"
            + "'" + socio.getTelefono() + "',"
            + "'" + socio.getEmail() + "',"
            + "'" + socio.getFechaAlta() + "',"
            + "'" + socio.getQrCode() + "',"
            + "'" + socio.getLogin() + "',"
            + "'" + socio.getPassword() + "',"
            + socio.getPuntos() + ")";
        s.execute(sql);
        s.close();
    }

    /** Actualiza los datos modificables de un socio. */
    public static void actualizarSocio(Connection c, Socio socio)
            throws SQLException {
        Statement s = c.createStatement();
        String sql =
            "UPDATE socios SET"
            + " nombre='" + socio.getNombre() + "',"
            + " telefono='" + socio.getTelefono() + "',"
            + " email='" + socio.getEmail() + "',"
            + " qr_code='" + socio.getQrCode() + "',"
            + " puntos=" + socio.getPuntos()
            + " WHERE id='" + socio.getId() + "'";
        s.execute(sql);
        s.close();
    }

    /** Elimina un socio por su id. */
    public static void eliminarSocio(Connection c, String id)
            throws SQLException {
        Statement s = c.createStatement();
        s.execute(
            "DELETE FROM suscripciones WHERE id_socio='"
            + id + "'");
        s.execute(
            "DELETE FROM socios WHERE id='" + id + "'");
        s.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ESCRITURA: PRODUCTOS
    // ─────────────────────────────────────────────────────────────────────────

    /** Inserta un nuevo producto en la BD. */
    public static void insertarProducto(
            Connection c, Producto prod) throws SQLException {
        Statement s = c.createStatement();
        String sql =
            "INSERT INTO productos VALUES ("
            + "'" + prod.getIdProducto() + "',"
            + "'" + prod.getNombre() + "',"
            + prod.getPrecioVenta() + ","
            + prod.getStock() + ","
            + prod.getStockMinimo() + ")";
        s.execute(sql);
        s.close();
    }

    /** Actualiza stock y precio de un producto. */
    public static void actualizarProducto(
            Connection c, Producto prod) throws SQLException {
        Statement s = c.createStatement();
        String sql =
            "UPDATE productos SET"
            + " nombre='" + prod.getNombre() + "',"
            + " precio_venta=" + prod.getPrecioVenta() + ","
            + " stock=" + prod.getStock() + ","
            + " stock_minimo=" + prod.getStockMinimo()
            + " WHERE id_producto='"
            + prod.getIdProducto() + "'";
        s.execute(sql);
        s.close();
    }

    /** Elimina un producto por su id. */
    public static void eliminarProducto(Connection c, String id)
            throws SQLException {
        Statement s = c.createStatement();
        s.execute(
            "DELETE FROM productos WHERE id_producto='"
            + id + "'");
        s.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ESCRITURA: EMPLEADOS
    // ─────────────────────────────────────────────────────────────────────────

    /** Inserta un empleado base en la BD. */
    public static void insertarEmpleado(
            Connection c, Empleado emp) throws SQLException {
        Statement s = c.createStatement();
        String sql =
            "INSERT INTO empleados VALUES ("
            + "'" + emp.getId() + "',"
            + "'" + emp.getNombre() + "',"
            + "'" + emp.getTelefono() + "',"
            + "'" + emp.getEmail() + "',"
            + "'" + emp.getFechaAlta() + "',"
            + emp.getSalarioBase() + ","
            + "'" + emp.getHorario() + "',"
            + "'EMPLEADO')";
        s.execute(sql);
        s.close();
    }

    /** Inserta un monitor (empleados + monitores_extra). */
    public static void insertarMonitor(
            Connection c, Monitor mon) throws SQLException {
        Statement s = c.createStatement();
        s.execute(
            "INSERT INTO empleados VALUES ("
            + "'" + mon.getId() + "',"
            + "'" + mon.getNombre() + "',"
            + "'" + mon.getTelefono() + "',"
            + "'" + mon.getEmail() + "',"
            + "'" + mon.getFechaAlta() + "',"
            + mon.getSalarioBase() + ","
            + "'" + mon.getHorario() + "',"
            + "'MONITOR')");
        s.execute(
            "INSERT INTO monitores_extra VALUES ("
            + "'" + mon.getId() + "',"
            + "'" + mon.getLogin() + "',"
            + "'" + mon.getPassword() + "',"
            + "'" + mon.getEspecialidad() + "')");
        s.close();
    }

    /** Inserta un admin (empleados + admins_extra). */
    public static void insertarAdmin(
            Connection c, Admin admin) throws SQLException {
        Statement s = c.createStatement();
        s.execute(
            "INSERT INTO empleados VALUES ("
            + "'" + admin.getId() + "',"
            + "'" + admin.getNombre() + "',"
            + "'" + admin.getTelefono() + "',"
            + "'" + admin.getEmail() + "',"
            + "'" + admin.getFechaAlta() + "',"
            + admin.getSalarioBase() + ","
            + "'" + admin.getHorario() + "',"
            + "'ADMIN')");
        s.execute(
            "INSERT INTO admins_extra VALUES ("
            + "'" + admin.getId() + "',"
            + "'" + admin.getLogin() + "',"
            + "'" + admin.getPassword() + "')");
        s.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ESCRITURA: MENSUALIDADES
    // ─────────────────────────────────────────────────────────────────────────

    /** Inserta una mensualidad en la BD. */
    public static void insertarMensualidad(Connection c,
            String idMen, String idSus,
            double importe, java.time.LocalDate fecha,
            String estado) throws SQLException {
        Statement s = c.createStatement();
        String sql =
            "INSERT INTO mensualidades VALUES ("
            + "'" + idMen + "',"
            + "'" + idSus + "',"
            + importe + ","
            + "'" + fecha + "',"
            + "'" + estado + "')";
        s.execute(sql);
        s.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AUXILIARES PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────

    private static Socio buscarSocioEnLista(
            List<Socio> socios, String id) {
        Socio resultado = null;
        for (Socio s : socios) {
            if (s.getId().equals(id) && resultado == null) {
                resultado = s;
            }
        }
        return resultado;
    }

    private static Monitor buscarMonitorEnLista(
            List<Monitor> monitores, String id) {
        Monitor resultado = null;
        for (Monitor m : monitores) {
            if (m.getId().equals(id) && resultado == null) {
                resultado = m;
            }
        }
        return resultado;
    }
}
