package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // --- DATOS DE TU CONEXIÓN SQL SERVER ---
    private static final String HOST = "sibibli-server.database.windows.net"; // O la IP/nombre de tu servidor
    private static final String PUERTO = "1433";
    private static final String BASE_DE_DATOS = "SIBIBLI"; // El nombre de tu base de datos
    private static final String USUARIO = "mtard"; // Tu usuario de SQL Server
    private static final String CONTRASENA = "Mt4rd1234"; // Tu contraseña de SQL Server

    // Opciones como 'encrypt=true' y 'trustServerCertificate=true' son comunes para evitar problemas de SSL.
    private static final String URL = "jdbc:sqlserver://" + HOST + ":" + PUERTO + ";databaseName=" + BASE_DE_DATOS + ";encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    private static Connection conexion = null;

    private ConexionBD() { }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                try {
                    // <-- CAMBIO: El nombre del driver de SQL Server.
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                    // <-- CAMBIO: Se usa la nueva URL y los datos de SQL Server.
                    conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                    System.out.println("¡Conexión a SQL Server exitosa!");

                } catch (ClassNotFoundException e) {
                    System.err.println("Error: No se encontró el driver de SQL Server. ¿Añadiste el .jar al proyecto?");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.err.println("Error al conectar con la base de datos de SQL Server.");
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