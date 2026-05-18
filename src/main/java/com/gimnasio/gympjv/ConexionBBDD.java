package com.gimnasio.gympjv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a MySQL (root/2006/gympjv).
 *
 * @author Ayoub Atidi Belbaz 1º DAM
 */
public class ConexionBBDD {

    private static final String URL =
        "jdbc:mysql://localhost:3306/gympjv"
        + "?createDatabaseIfNotExist=true"
        + "&useSSL=false"
        + "&serverTimezone=UTC"
        + "&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "2006";

    private static Connection conexion = null;

    private ConexionBBDD() { }

    /** Devuelve la conexión, la abre si no estaba. Null si MySQL no está disponible. */
    public static Connection getConexion() {
        if (conexion == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(
                    URL, USUARIO, CLAVE);
                System.out.println(
                    "[BBDD] Conexion con MySQL establecida.");
            } catch (ClassNotFoundException e) {
                System.out.println(
                    "[BBDD] Driver no encontrado: "
                    + e.getMessage());
            } catch (SQLException e) {
                System.out.println(
                    "[BBDD] Error de conexion: "
                    + e.getMessage());
            }
        }
        return conexion;
    }

    /** Cierra la conexión. */
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("[BBDD] Conexion cerrada.");
            } catch (SQLException e) {
                System.out.println(
                    "[BBDD] Error al cerrar: "
                    + e.getMessage());
            }
        }
    }
}
