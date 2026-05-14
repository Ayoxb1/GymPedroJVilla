package com.gimnasio.gympjv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de la conexion JDBC con MySQL (singleton de conexion).
 * Usuario: root / Clave: 2006 / BD: gympjv
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
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

    /**
     * Devuelve la conexion activa, abriendola si es necesario.
     * Devuelve null si MySQL no esta disponible.
     */
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

    /** Cierra la conexion activa de forma segura. */
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
