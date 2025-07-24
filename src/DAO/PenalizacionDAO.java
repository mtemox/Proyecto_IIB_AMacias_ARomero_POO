package DAO;

import Modelo.Penalizacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}