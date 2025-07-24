package DAO;

import Modelo.Penalizacion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PenalizacionDAO {

    /**
     * Registra una nueva penalización en la base de datos.
     * Este metodo es llamado dentro de una transacción en PrestamoDAO.
     * @param penalizacion El objeto Penalizacion a registrar.
     * @param con La conexión de base de datos existente (para la transacción).
     * @return true si se registra correctamente.
     * @throws SQLException si ocurre un error.
     */
    public boolean registrarPenalizacion(Penalizacion penalizacion, Connection con) throws SQLException {
        // <-- CAMBIO: Se elimina el casting de tipo "::estado_penalizacion_tipo".
        String sql = "INSERT INTO penalizaciones (prestamo_id, socio_id, monto, fecha_generacion, estado_penalizacion, observaciones) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, penalizacion.getPrestamoId());
            ps.setLong(2, penalizacion.getSocioId());
            ps.setBigDecimal(3, penalizacion.getMonto());
            ps.setDate(4, java.sql.Date.valueOf(penalizacion.getFechaGeneracion()));
            ps.setString(5, penalizacion.getEstadoPenalizacion());
            ps.setString(6, penalizacion.getObservaciones());

            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Busca penalizaciones aplicando filtros opcionales por cédula y estado.
     * @param cedulaSocio Cédula para filtrar (o cadena vacía/null para no filtrar).
     * @param estado Estado para filtrar ('PENDIENTE', 'PAGADA', o null para todos).
     * @return Una lista de arrays de objetos para poblar la tabla.
     */
    public List<Object[]> buscarPenalizaciones(String cedulaSocio, String estado) {
        List<Object[]> penalizaciones = new ArrayList<>();
        // Consulta compleja con JOINs para obtener datos legibles
        String sql = "SELECT pen.id, s.id as socio_id, s.cedula, CONCAT(s.nombre, ' ', s.apellido) AS socio, " +
                "pen.monto, pen.estado_penalizacion, pen.fecha_generacion, l.titulo " +
                "FROM penalizaciones pen " +
                "JOIN socios s ON pen.socio_id = s.id " +
                "JOIN prestamos p ON pen.prestamo_id = p.id " +
                "JOIN libros l ON p.libro_id = l.id " +
                "WHERE (? IS NULL OR s.cedula = ?) " + // Filtro por cédula
                "AND (? IS NULL OR pen.estado_penalizacion = ?) " + // Filtro por estado
                "ORDER BY pen.fecha_generacion DESC";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Se usa un truco para hacer los filtros opcionales.
            // Si el string es nulo, la condición WHERE se cumple y no filtra.
            ps.setString(1, cedulaSocio.isEmpty() ? null : cedulaSocio);
            ps.setString(2, cedulaSocio.isEmpty() ? null : cedulaSocio);
            ps.setString(3, estado.equals("TODOS") ? null : estado);
            ps.setString(4, estado.equals("TODOS") ? null : estado);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                penalizaciones.add(new Object[]{
                        rs.getLong("id"),
                        rs.getLong("socio_id"),
                        rs.getString("cedula"),
                        rs.getString("socio"),
                        rs.getBigDecimal("monto"),
                        rs.getString("estado_penalizacion"),
                        rs.getDate("fecha_generacion").toLocalDate(),
                        rs.getString("titulo")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return penalizaciones;
    }

    /**
     * Marca una penalización como pagada y, si el socio ya no tiene otras
     * multas pendientes, actualiza su estado a 'ACTIVO'.
     * @param penalizacionId El ID de la penalización a pagar.
     * @param socioId El ID del socio asociado.
     * @return true si la operación fue exitosa, false en caso de error.
     */
    public boolean pagarPenalizacion(long penalizacionId, long socioId) {
        Connection con = ConexionBD.getConexion();
        String sqlUpdatePenalizacion = "UPDATE penalizaciones SET estado_penalizacion = 'PAGADA', fecha_pago = CURDATE() WHERE id = ?";
        String sqlCheckOtrasPenalizaciones = "SELECT COUNT(*) FROM penalizaciones WHERE socio_id = ? AND estado_penalizacion = 'PENDIENTE'";
        String sqlUpdateSocio = "UPDATE socios SET estado_socio = 'ACTIVO' WHERE id = ?";

        try {
            con.setAutoCommit(false); // --- INICIO DE LA TRANSACCIÓN ---

            // 1. Marcar la penalización como pagada
            try (PreparedStatement ps = con.prepareStatement(sqlUpdatePenalizacion)) {
                ps.setLong(1, penalizacionId);
                ps.executeUpdate();
            }

            // 2. Comprobar si al socio le quedan otras multas pendientes
            int multasPendientes = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlCheckOtrasPenalizaciones)) {
                ps.setLong(1, socioId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    multasPendientes = rs.getInt(1);
                }
            }

            // 3. Si no le quedan multas, actualizar su estado a 'ACTIVO'
            if (multasPendientes == 0) {
                try (PreparedStatement ps = con.prepareStatement(sqlUpdateSocio)) {
                    ps.setLong(1, socioId);
                    ps.executeUpdate();
                }
            }

            con.commit(); // --- FIN DE LA TRANSACCIÓN ---
            return true;
        } catch (SQLException e) {
            System.err.println("Error en la transacción de pago. Revirtiendo cambios...");
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Genera un reporte de ingresos por penalizaciones pagadas en un rango de fechas.
     * @param fechaInicio La fecha de inicio del rango.
     * @param fechaFin La fecha de fin del rango.
     * @return Un array de objeto con [Total Ingresos, Cantidad de Multas Pagadas], o null si no hay datos.
     */
    public Object[] getReporteIngresosPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        Object[] reporte = null;
        String sql = "SELECT SUM(monto) AS total_ingresos, COUNT(id) AS total_pagadas " +
                "FROM penalizaciones " +
                "WHERE estado_penalizacion = 'PAGADA' AND fecha_pago BETWEEN ? AND ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Incluso si no hay ingresos, la consulta devuelve una fila con valores NULL.
                // Nos aseguramos de manejarlo.
                BigDecimal totalIngresos = rs.getBigDecimal("total_ingresos");
                if (totalIngresos == null) {
                    totalIngresos = BigDecimal.ZERO;
                }
                reporte = new Object[]{
                        totalIngresos,
                        rs.getInt("total_pagadas")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reporte;
    }

}