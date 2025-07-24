package DAO;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import Modelo.Penalizacion;

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
        // <-- CAMBIO: Se elimina el casting "::estado_prestamo_tipo".
        String sqlInsertPrestamo = "INSERT INTO prestamos (libro_id, socio_id, usuario_sistema_id, fecha_prestamo, fecha_devolucion_estimada, estado_prestamo) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdateLibro = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";

        try {
            // --- INICIO DE LA TRANSACCIÓN ---
            // Desactivamos el auto-commit para manejar la transacción manualmente
            con.setAutoCommit(false);

            // 1. Insertar el préstamo
            try (PreparedStatement psPrestamo = con.prepareStatement(sqlInsertPrestamo)) {
                psPrestamo.setLong(1, prestamo.getLibroId());
                psPrestamo.setLong(2, prestamo.getSocioId());
                psPrestamo.setLong(3, prestamo.getUsuarioSistemaId()); // Idealmente, el ID del usuario logueado
                psPrestamo.setDate(4, java.sql.Date.valueOf(prestamo.getFechaPrestamo()));
                psPrestamo.setDate(5, java.sql.Date.valueOf(prestamo.getFechaDevolucionEstimada()));
                psPrestamo.setString(6, "EN_CURSO");
                psPrestamo.executeUpdate();
            }

            // 2. Actualizar la cantidad del libro
            try (PreparedStatement psLibro = con.prepareStatement(sqlUpdateLibro)) {
                psLibro.setLong(1, prestamo.getLibroId());
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

    /**
     * Busca los préstamos activos (en curso o vencidos) de un socio por su cédula.
     * @param cedulaSocio La cédula del socio.
     * @return Una lista de arrays de objetos con los datos del préstamo para la tabla.
     */
    public List<Object[]> buscarPrestamosActivosPorCedula(String cedulaSocio) {
        List<Object[]> prestamosActivos = new ArrayList<>();
        // Consulta con JOINs para obtener información legible
        // <-- CAMBIO: Se usa CONCAT() en lugar de || para unir nombres.
        String sql = "SELECT p.id, l.titulo, CONCAT(s.nombre, ' ', s.apellido) AS socio_nombre, " +
                "p.fecha_prestamo, p.fecha_devolucion_estimada, p.estado_prestamo " +
                "FROM prestamos p " +
                "JOIN libros l ON p.libro_id = l.id " +
                "JOIN socios s ON p.socio_id = s.id " +
                "WHERE s.cedula = ? AND (p.estado_prestamo = 'EN_CURSO' OR p.estado_prestamo = 'VENCIDO')";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedulaSocio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prestamosActivos.add(new Object[]{
                        rs.getLong("id"),
                        rs.getString("titulo"),
                        rs.getString("socio_nombre"),
                        rs.getDate("fecha_devolucion_estimada").toLocalDate(),
                        rs.getString("estado_prestamo")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamosActivos;
    }

    /**
     * Procesa la devolución de un libro. Actualiza el estado del préstamo y la cantidad
     * disponible del libro. Si hay retraso, genera una penalización.
     * Todo se ejecuta dentro de una transacción para garantizar la integridad de los datos.
     * @param prestamoId El ID del préstamo a devolver.
     * @return true si la operación fue exitosa, false en caso de error.
     */

    public boolean registrarDevolucion(long prestamoId) {
        Connection con = ConexionBD.getConexion();
        String sqlInfoPrestamo = "SELECT libro_id, socio_id, fecha_devolucion_estimada FROM prestamos WHERE id = ?";
        String sqlUpdatePrestamo = "UPDATE prestamos SET estado_prestamo = 'DEVUELTO', fecha_devolucion_real = ? WHERE id = ?";
        String sqlUpdateLibro = "UPDATE libros SET cantidad_disponible = cantidad_disponible + 1 WHERE id = ?";
        String sqlUpdateSocio = "UPDATE socios SET estado_socio = 'CON_MULTAS' WHERE id = ?";

        try {
            // --- INICIO DE LA TRANSACCIÓN ---
            con.setAutoCommit(false);

            // 1. Obtener información del préstamo
            long libroId, socioId;
            LocalDate fechaDevolucionEstimada;
            try (PreparedStatement ps = con.prepareStatement(sqlInfoPrestamo)) {
                ps.setLong(1, prestamoId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) throw new SQLException("Préstamo no encontrado, ID: " + prestamoId);
                libroId = rs.getLong("libro_id");
                socioId = rs.getLong("socio_id");
                fechaDevolucionEstimada = rs.getDate("fecha_devolucion_estimada").toLocalDate();
            }

            // 2. Actualizar el préstamo
            LocalDate fechaHoy = LocalDate.now();
            try (PreparedStatement ps = con.prepareStatement(sqlUpdatePrestamo)) {
                ps.setDate(1, java.sql.Date.valueOf(fechaHoy));
                ps.setLong(2, prestamoId);
                ps.executeUpdate();
            }

            // 3. Actualizar la cantidad del libro
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateLibro)) {
                ps.setLong(1, libroId);
                ps.executeUpdate();
            }

            // 4. Verificar y crear penalización si hay retraso
            if (fechaHoy.isAfter(fechaDevolucionEstimada)) {
                long diasDeRetraso = ChronoUnit.DAYS.between(fechaDevolucionEstimada, fechaHoy);
                BigDecimal montoMulta = new BigDecimal(diasDeRetraso).multiply(new BigDecimal("0.50")); // Ejemplo: $0.50 por día

                Penalizacion penalizacion = new Penalizacion();
                penalizacion.setPrestamoId(prestamoId);
                penalizacion.setSocioId(socioId);
                penalizacion.setMonto(montoMulta);
                penalizacion.setFechaGeneracion(fechaHoy);
                penalizacion.setEstadoPenalizacion("PENDIENTE");
                penalizacion.setObservaciones("Retraso de " + diasDeRetraso + " día(s).");

                // 4.1. Registrar la penalización
                new PenalizacionDAO().registrarPenalizacion(penalizacion, con);

                // 4.2. Actualizar estado del socio a 'CON_MULTAS'
                try (PreparedStatement ps = con.prepareStatement(sqlUpdateSocio)) {
                    ps.setLong(1, socioId);
                    ps.executeUpdate();
                }
            }

            // --- FIN DE LA TRANSACCIÓN ---
            con.commit(); // Confirmar todos los cambios
            return true;

        } catch (SQLException e) {
            System.err.println("Error en la transacción de devolución. Revirtiendo cambios...");
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Revertir cambios si algo falla
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true); // Dejar la conexión lista
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}