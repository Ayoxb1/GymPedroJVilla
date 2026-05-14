package com.gimnasio.gympjv;

import com.gimnasio.gympjv.clases.*;
import com.gimnasio.gympjv.clases.enums.EstadoPago;
import com.gimnasio.gympjv.clases.enums.MetodoPago;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria. Singleton.
 * Centraliza el acceso a las listas globales del sistema y carga datos demo.
 *
 * Usuarios demo:
 *   - Admin:  Ayoub / 1234
 *   - Socio:  Ayoub Atidi Belbaz / 1234  (suscripcion activa, mensualidad pagada)
 *   - Otros:  laura / 1234, javier / 1234 (monitores)
 */
public class Repositorio {

    private static Repositorio instancia;

    private final List<Socio> socios = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<Admin> admins = new ArrayList<>();
    private final List<Monitor> monitores = new ArrayList<>();
    private final List<Suscripcion> suscripciones = new ArrayList<>();
    private final List<ClaseGrupal> clases = new ArrayList<>();
    private final List<SesionPersonal> sesionesPersonales = new ArrayList<>();
    private final List<PlanDieta> planesDieta = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<Peluqueria> serviciosPeluqueria = new ArrayList<>();
    private final List<Venta> ventas = new ArrayList<>();
    private final List<Asistencia> asistencias = new ArrayList<>();
    private final GestorStock gestorStock = new GestorStock();

    private Reportes reportes;

    private Repositorio() {
        cargarDesdeBBDD();
        this.reportes = new Reportes(socios, ventas, clases, gestorStock);
    }

    /**
     * Intenta cargar todos los datos desde MySQL.
     * Si la conexion falla, hace fallback a cargarDemo().
     */
    private void cargarDesdeBBDD() {
        Connection c = ConexionBBDD.getConexion();
        boolean exito = false;
        if (c != null) {
            try {
                GestorBBDD.crearTablas(c);
                if (GestorBBDD.estaVacia(c, "socios")) {
                    System.out.println(
                        "[Repo] BD vacia, insertando demo...");
                    GestorBBDD.insertarDatosDemo(c);
                }
                List<Admin> listaAdmins =
                    GestorBBDD.cargarAdmins(c);
                for (Admin a : listaAdmins) {
                    admins.add(a);
                    empleados.add(a);
                }
                List<Monitor> listaMonitores =
                    GestorBBDD.cargarMonitores(c);
                for (Monitor m : listaMonitores) {
                    monitores.add(m);
                    empleados.add(m);
                }
                List<Empleado> listaBase =
                    GestorBBDD.cargarEmpleadosBase(c);
                empleados.addAll(listaBase);
                socios.addAll(GestorBBDD.cargarSocios(c));
                List<Producto> listaProds =
                    GestorBBDD.cargarProductos(c);
                for (Producto p : listaProds) {
                    gestorStock.anadirProducto(p);
                }
                suscripciones.addAll(
                    GestorBBDD.cargarSuscripciones(c, socios));
                clases.addAll(
                    GestorBBDD.cargarClases(c, monitores));
                System.out.println(
                    "[Repo] Datos cargados desde MySQL.");
                exito = true;
            } catch (SQLException e) {
                System.out.println(
                    "[Repo] Error cargando de BD: "
                    + e.getMessage());
            }
        }
        if (!exito) {
            System.out.println(
                "[Repo] Usando datos en memoria (demo).");
            cargarDemo();
        }
    }

    public static synchronized Repositorio getInstancia() {
        if (instancia == null) instancia = new Repositorio();
        return instancia;
    }

    private void cargarDemo() {

        // ── Admin principal: Ayoub / 1234 ───────────────────────────────────────
        Admin ayoubAdmin = new Admin("ADM-001", "Ayoub Atidi Belbaz", "611000000",
                "ayoub.admin@gym.com", LocalDate.of(2024, 1, 10),
                2800.0, "MANANA", "Ayoub", "Uno234");
        admins.add(ayoubAdmin);
        empleados.add(ayoubAdmin);

        // ── Monitores ───────────────────────────────────────────────────────────
        Monitor laura = new Monitor("MON-001", "Laura Martinez", "611000002",
                "laura@gym.com", LocalDate.of(2023, 3, 1),
                1900.0, "MANANA", "laura", "1234", "Yoga / Pilates");
        Monitor javier = new Monitor("MON-002", "Javier Sanchez", "611000003",
                "javier@gym.com", LocalDate.of(2023, 6, 15),
                1900.0, "TARDE", "javier", "1234", "CrossFit / Musculacion");
        monitores.add(laura);
        monitores.add(javier);
        empleados.add(laura);
        empleados.add(javier);

        // ── Empleado peluqueria (sin acceso al sistema) ─────────────────────────
        Empleado peluquero = new Empleado("EMP-001", "Marta Lopez", "611000004",
                "marta@gym.com", LocalDate.of(2024, 1, 5),
                1700.0, "TARDE");
        empleados.add(peluquero);

        // ── Productos ────────────────────────────────────────────────────────────
        Producto p1 = new Producto("PROD-001", "Proteina Whey 1kg", 39.99, 50, 5);
        Producto p2 = new Producto("PROD-002", "Creatina 500g", 24.99, 30, 5);
        Producto p3 = new Producto("PROD-003", "Botella Termica", 14.99, 40, 5);
        Producto p4 = new Producto("PROD-004", "Camiseta Tecnica", 24.99, 60, 5);
        Producto p5 = new Producto("PROD-005", "Bebida Isotonica", 2.50, 100, 10);
        Producto p6 = new Producto("PROD-006", "Champu Profesional", 12.50, 25, 5);
        gestorStock.anadirProducto(p1);
        gestorStock.anadirProducto(p2);
        gestorStock.anadirProducto(p3);
        gestorStock.anadirProducto(p4);
        gestorStock.anadirProducto(p5);
        gestorStock.anadirProducto(p6);

        // ── SOCIO PRINCIPAL: Ayoub Atidi Belbaz / 1234 ──────────────────────────
        Socio ayoubSocio = new Socio("SOC-001", "Ayoub Atidi Belbaz", "612000001",
                "ayoub.socio@email.com", LocalDate.of(2024, 9, 1), "QR-SOC-001",
                "Ayoub Atidi Belbaz", "1234");
        socios.add(ayoubSocio);

        // Suscripcion mensual ACTIVA
        Suscripcion susAyoub = new Suscripcion("SUS-001", ayoubSocio, "Mensual",
                45.0, LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20), 12);
        ayoubSocio.setSuscripcion(susAyoub);
        suscripciones.add(susAyoub);

        // Mensualidad ya PAGADA
        Mensualidad mensAyoub = new Mensualidad("MEN-001", susAyoub, 45.0,
                LocalDate.now().plusDays(20));
        mensAyoub.setEstado(EstadoPago.PAGADA);
        susAyoub.anadirMensualidad(mensAyoub);

        // ── Otros socios demo ────────────────────────────────────────────────────
        Socio pedro = new Socio("SOC-002", "Pedro Fernandez", "612000002",
                "pedro@email.com", LocalDate.of(2024, 3, 20), "QR-SOC-002",
                "pedro", "1234");
        Socio maria = new Socio("SOC-003", "Maria Gonzalez", "612000003",
                "maria@email.com", LocalDate.of(2025, 2, 1), "QR-SOC-003",
                "maria", "1234");
        Socio luis = new Socio("SOC-004", "Luis Torres", "612000004",
                "luis@email.com", LocalDate.of(2025, 4, 15), "QR-SOC-004",
                "luis", "1234");
        socios.add(pedro);
        socios.add(maria);
        socios.add(luis);

        Suscripcion susPedro = new Suscripcion("SUS-002", pedro, "Trimestral",
                120.0, LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(1), 36);
        pedro.setSuscripcion(susPedro);
        suscripciones.add(susPedro);

        Suscripcion susMaria = new Suscripcion("SUS-003", maria, "Anual",
                400.0, LocalDate.now().minusMonths(3),
                LocalDate.now().plusMonths(9), 200);
        maria.setSuscripcion(susMaria);
        suscripciones.add(susMaria);

        // ── Clases grupales ──────────────────────────────────────────────────────
        ClaseGrupal clase1 = new ClaseGrupal("CLA-001", "Yoga Matutino",
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                laura, 15);
        ClaseGrupal clase2 = new ClaseGrupal("CLA-002", "CrossFit Intensivo",
                LocalDateTime.now().plusDays(2).withHour(18).withMinute(30),
                javier, 20);
        ClaseGrupal clase3 = new ClaseGrupal("CLA-003", "Pilates Core",
                LocalDateTime.now().plusDays(3).withHour(10).withMinute(30),
                laura, 12);
        ClaseGrupal clase4 = new ClaseGrupal("CLA-004", "Body Pump",
                LocalDateTime.now().plusDays(1).withHour(19).withMinute(30),
                javier, 25);
        clases.add(clase1);
        clases.add(clase2);
        clases.add(clase3);
        clases.add(clase4);
        laura.anadirClase(clase1);
        laura.anadirClase(clase3);
        javier.anadirClase(clase2);
        javier.anadirClase(clase4);

        // ── Reserva demo de Ayoub ────────────────────────────────────────────────
        Reserva resAyoub = new Reserva("RES-001", ayoubSocio, clase1,
                LocalDateTime.now().minusDays(1));
        resAyoub.confirmar();
        reservas.add(resAyoub);
        ayoubSocio.anadirReserva(resAyoub);

        // ── Plan dieta demo ──────────────────────────────────────────────────────
        PlanDieta planPedro = new PlanDieta("DIE-001", pedro, javier, 3000);
        planPedro.anadirComida("Lunes", "Avena + claras + platano");
        planPedro.anadirComida("Martes", "Pollo + arroz + verduras");
        planesDieta.add(planPedro);
        pedro.setPlanDieta(planPedro);
        javier.anadirPlanDieta(planPedro);

        // ── Plan dieta para AYOUB SOCIO (asignado por la monitora Laura) ────────
        PlanDieta planAyoub = new PlanDieta("DIE-002", ayoubSocio, laura, 2400);
        planAyoub.anadirComida("Lunes", "Tostada integral + tortilla 2 huevos + cafe");
        planAyoub.anadirComida("Martes", "Yogur griego + frutos secos + manzana");
        planAyoub.anadirComida("Miercoles", "Pollo plancha + quinoa + ensalada");
        planAyoub.anadirComida("Jueves", "Atun + pasta integral + verduras al horno");
        planAyoub.anadirComida("Viernes", "Salmon + boniato asado + brocoli");
        planAyoub.anadirComida("Sabado", "Libre (comida saludable)");
        planAyoub.anadirComida("Domingo", "Tortilla francesa + ensalada + fruta");
        planesDieta.add(planAyoub);
        ayoubSocio.setPlanDieta(planAyoub);
        laura.anadirPlanDieta(planAyoub);

        // ── Bono demo de 10 sesiones para Ayoub socio ────────────────────────────
        ayoubSocio.comprarBono(10);

        // ── Sesion personal demo ─────────────────────────────────────────────────
        SesionPersonal ses1 = new SesionPersonal("SES-001", maria, javier,
                LocalDateTime.now().plusDays(2).withHour(17).withMinute(0),
                60, "Tonificacion", "MEDIA");
        sesionesPersonales.add(ses1);

        // ── Servicio peluqueria demo ─────────────────────────────────────────────
        Peluqueria pel1 = new Peluqueria("PEL-001", pedro, peluquero,
                LocalDateTime.now().plusDays(1).withHour(17).withMinute(0));
        pel1.anadirServicio("Pelo");
        pel1.anadirServicio("Barba");
        serviciosPeluqueria.add(pel1);

        // ── Asistencias demo ─────────────────────────────────────────────────────
        Asistencia as1 = new Asistencia("ASI-001", ayoubSocio, clase1,
                LocalDateTime.now().minusDays(1));
        as1.marcarPresente();
        asistencias.add(as1);
        Asistencia as2 = new Asistencia("ASI-002", pedro, clase2,
                LocalDateTime.now().minusDays(2));
        as2.marcarPresente();
        asistencias.add(as2);
        Asistencia as3 = new Asistencia("ASI-003", maria, clase1,
                LocalDateTime.now().minusDays(1));
        asistencias.add(as3);

        // ── Venta demo a nombre de Ayoub socio ───────────────────────────────────
        Venta v1 = new Venta("VEN-001", ayoubSocio, MetodoPago.TARJETA);
        v1.agregarItem(new ItemVenta(p1, 1));
        v1.agregarItem(new ItemVenta(p5, 2));
        v1.aplicarDescuento(ayoubSocio);
        v1.confirmar();
        ayoubSocio.anadirVenta(v1);
        ventas.add(v1);
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────
    public List<Socio> getSocios() { return socios; }
    public List<Empleado> getEmpleados() { return empleados; }
    public List<Admin> getAdmins() { return admins; }
    public List<Monitor> getMonitores() { return monitores; }
    public List<Suscripcion> getSuscripciones() { return suscripciones; }
    public List<ClaseGrupal> getClases() { return clases; }
    public List<SesionPersonal> getSesionesPersonales() { return sesionesPersonales; }
    public List<PlanDieta> getPlanesDieta() { return planesDieta; }
    public List<Reserva> getReservas() { return reservas; }
    public List<Peluqueria> getServiciosPeluqueria() { return serviciosPeluqueria; }
    public List<Venta> getVentas() { return ventas; }
    public List<Asistencia> getAsistencias() { return asistencias; }
    public GestorStock getGestorStock() { return gestorStock; }
    public Reportes getReportes() { return reportes; }

    // ─── Busquedas ───────────────────────────────────────────────────────────────
    public Socio buscarSocioPorId(String idSocio) {
        for (Socio s : socios) if (s.getId().equals(idSocio)) return s;
        return null;
    }

    public Socio buscarSocioPorQR(String qrCode) {
        for (Socio s : socios) if (s.getQrCode().equals(qrCode)) return s;
        return null;
    }

    public Socio buscarSocioPorLogin(String login) {
        if (login == null) return null;
        for (Socio s : socios) {
            if (login.equals(s.getLogin())) return s;
        }
        return null;
    }

    public UsuarioSistema buscarUsuarioPorLogin(String login) {
        if (login == null) return null;
        for (Admin a : admins) if (login.equals(a.getLogin())) return a;
        for (Monitor m : monitores) if (login.equals(m.getLogin())) return m;
        return null;
    }
}
