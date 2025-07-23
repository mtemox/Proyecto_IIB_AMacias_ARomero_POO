package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // --- DATOS DE TU CONEXIÓN NEON (POSTGRESQL) ---
    // ¡OBTÉN ESTOS DATOS DE TU DASHBOARD DE NEON!
    private static final String HOST = "ep-sweet-sun-acn09m5a-pooler.sa-east-1.aws.neon.tech";
    private static final String PUERTO = "5432";
    private static final String BASE_DE_DATOS = "SIBIBLI";
    private static final String USUARIO = "neondb_owner";
    private static final String CONTRASENA = "npg_g6fXtWcnL3zV";

    // La URL para PostgreSQL es diferente y Neon requiere SSL.
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + BASE_DE_DATOS + "?sslmode=require";

    private static Connection conexion = null;

    private ConexionBD() { }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                try {
                    // ¡IMPORTANTE! El nombre del driver de PostgreSQL es diferente.
                    Class.forName("org.postgresql.Driver");

                    conexion = DriverManager.getConnection("jdbc:postgresql://sibibli-13617.j77.aws-us-east-1.cockroachlabs.cloud:26257/SIBIBLI?sslmode=verify-full&password=_KaHpoLnCkm9ZNdE9YmkRA&user=ariel", USUARIO, CONTRASENA);
                    System.out.println("¡Conexión a Neon (PostgreSQL) exitosa!");

                } catch (ClassNotFoundException e) {
                    System.err.println("Error: No se encontró el driver de PostgreSQL. ¿Añadiste el .jar al proyecto?");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.err.println("Error al conectar con la base de datos de Neon.");
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