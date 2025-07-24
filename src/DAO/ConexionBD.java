package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // --- DATOS DE TU CONEXIÓN MySQL ---
    // ¡REEMPLAZA ESTOS DATOS CON LOS DE TU SERVIDOR MYSQL!
    private static final String HOST = "bef6ikoyhy17ojjj0hqk-mysql.services.clever-cloud.com"; // o la IP de tu servidor en la nube
    private static final String PUERTO = "3306";
    private static final String BASE_DE_DATOS = "bef6ikoyhy17ojjj0hqk";
    private static final String USUARIO = "ui8tnouqmbqfdxoe"; // tu usuario de MySQL
    private static final String CONTRASENA = "MGhaYmlzbPAvGyvHUYD8"; // tu contraseña de MySQL

    // <-- CAMBIO: La URL para MySQL tiene un formato diferente.
    // Opciones como 'useSSL=false' y 'serverTimezone' previenen errores comunes.
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DE_DATOS + "?useSSL=false&serverTimezone=UTC";

    private static Connection conexion = null;

    private ConexionBD() { }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                try {
                    // <-- CAMBIO: El nombre del driver de MySQL.
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // <-- CAMBIO: Se usa la nueva URL y los datos de MySQL.
                    conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                    System.out.println("¡Conexión a MySQL exitosa!");

                } catch (ClassNotFoundException e) {
                    System.err.println("Error: No se encontró el driver de MySQL. ¿Añadiste el .jar al proyecto?");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.err.println("Error al conectar con la base de datos de MySQL.");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al comprobar el estado de la conexión.");
            e.printStackTrace();
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión a la base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión.");
            e.printStackTrace();
        }
    }
}