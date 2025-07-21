package DAO;

import Modelo.Socio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SocioDAO {

    /**
     * Inserta un nuevo socio en la base de datos.
     * @param socio El objeto Socio con todos los datos.
     * @return true si se guardó correctamente, false si hubo un error.
     */

    public boolean registrarSocio(Socio socio) {
        // Obtenemos la conexión a la base de datos
        Connection con = ConexionBD.getConexion();
        // Preparamos la consulta SQL para evitar inyección SQL
        String sql = "INSERT INTO socios (cedula, nombre, apellido, email, telefono, fecha_registro, estado_socio) VALUES (?, ?, ?, ?, ?, ?, ?::estado_socio_tipo)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Establecemos los valores para los '?' de la consulta
            ps.setString(1, socio.getCedula());
            ps.setString(2, socio.getNombre());
            ps.setString(3, socio.getApellido());
            ps.setString(4, socio.getEmail());
            ps.setString(5, socio.getTelefono());
            ps.setDate(6, java.sql.Date.valueOf(socio.getFechaRegistro()));
            ps.setString(7, socio.getEstadoSocio());

            // Ejecutamos la inserción
            ps.executeUpdate();
            System.out.println("Socio registrado exitosamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar el socio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        // La conexión no se cierra aquí, se gestiona centralmente.
    }
}