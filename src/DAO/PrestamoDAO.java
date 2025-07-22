package DAO;

import Modelo.Prestamo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrestamoDAO {

    /**
     * Registra un nuevo préstamo y actualiza la cantidad de libros.
     * Utiliza una transacción para asegurar que ambas operaciones se realicen correctamente.
     * @param prestamo El objeto Prestamo con los datos a registrar.
     * @return true si la operación fue exitosa, false si falló.
     */

    public boolean registrarPrestamo(Prestamo prestamo) {
        Connection con = ConexionBD.getConexion();
        String sqlInsertPrestamo = "INSERT INTO prestamos (libro_id, socio_id, usuario_sistema_id, fecha_prestamo, fecha_devolucion_estimada, estado_prestamo) VALUES (?, ?, ?, ?, ?, ?::estado_prestamo_tipo)";
        String sqlUpdateLibro = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";

        try {
            // --- INICIO DE LA TRANSACCIÓN ---
            // Desactivamos el auto-commit para manejar la transacción manualmente
            con.setAutoCommit(false);

            // 1. Insertar el préstamo
            try (PreparedStatement psPrestamo = con.prepareStatement(sqlInsertPrestamo)) {
                psPrestamo.setInt(1, prestamo.getLibroId());
                psPrestamo.setInt(2, prestamo.getSocioId());
                psPrestamo.setInt(3, prestamo.getUsuarioSistemaId()); // Idealmente, el ID del usuario logueado
                psPrestamo.setDate(4, java.sql.Date.valueOf(prestamo.getFechaPrestamo()));
                psPrestamo.setDate(5, java.sql.Date.valueOf(prestamo.getFechaDevolucionEstimada()));
                psPrestamo.setString(6, "EN_CURSO");
                psPrestamo.executeUpdate();
            }

            // 2. Actualizar la cantidad del libro
            try (PreparedStatement psLibro = con.prepareStatement(sqlUpdateLibro)) {
                psLibro.setInt(1, prestamo.getLibroId());
                psLibro.executeUpdate();
            }

            // --- FIN DE LA TRANSACCIÓN ---
            // Si todo fue bien, confirmamos los cambios

            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar el préstamo. Revirtiendo cambios...");
            e.printStackTrace();

            try {
                // Si algo falla, revertimos todos los cambios
                if (con != null) {
                    con.rollback();
                }

            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;

        } finally {
            try {
                // Dejamos la conexión lista para la siguiente operación
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}