package DAO;

import Modelo.Socio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        // <-- CAMBIO: Se elimina el casting "::estado_socio_tipo".
        String sql = "INSERT INTO socios (cedula, nombre, apellido, email, telefono, fecha_registro, estado_socio) VALUES (?, ?, ?, ?, ?, ?, ?)";

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

    /**
     * Busca un socio por su cédula y verifica que esté activo.
     * @param cedula La cédula del socio a buscar.
     * @return Un objeto Socio si se encuentra y está activo, de lo contrario null.
     */

    public Socio buscarPorCedula(String cedula) {
        Socio socio = null;
        String sql = "SELECT * FROM socios WHERE cedula = ? AND estado_socio = 'ACTIVO'";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    socio = new Socio(); // Usamos un constructor vacío
                    socio.setId(rs.getLong("id"));
                    socio.setCedula(rs.getString("cedula"));
                    socio.setNombre(rs.getString("nombre"));
                    socio.setApellido(rs.getString("apellido"));
                    socio.setEmail(rs.getString("email"));
                    socio.setTelefono(rs.getString("telefono"));
                    socio.setEstadoSocio(rs.getString("estado_socio"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar socio por cédula: " + e.getMessage());
            e.printStackTrace();
        }
        return socio;
    }

    /**
     * Obtiene una lista de todos los socios de la base de datos.
     * @return Una lista de objetos Socio.
     */
    public List<Socio> obtenerTodosLosSocios() {
        List<Socio> socios = new ArrayList<>();
        String sql = "SELECT * FROM socios ORDER BY apellido, nombre";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Socio socio = new Socio();
                socio.setId(rs.getLong("id"));
                socio.setCedula(rs.getString("cedula"));
                socio.setNombre(rs.getString("nombre"));
                socio.setApellido(rs.getString("apellido"));
                socio.setEmail(rs.getString("email"));
                socio.setTelefono(rs.getString("telefono"));
                // Convertir java.sql.Date a java.time.LocalDate
                socio.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                socio.setEstadoSocio(rs.getString("estado_socio"));
                socios.add(socio);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los socios: " + e.getMessage());
            e.printStackTrace();
        }
        return socios;
    }

    /**
     * Actualiza los datos de un socio existente en la base de datos.
     * @param socio El objeto Socio con los datos actualizados.
     * @return true si se actualizó correctamente, false si hubo un error.
     */
    public boolean actualizarSocio(Socio socio) {
        // <-- CAMBIO: Se elimina el casting "::estado_socio_tipo".
        String sql = "UPDATE socios SET nombre = ?, apellido = ?, email = ?, telefono = ?, estado_socio = ? WHERE id = ?";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, socio.getNombre());
            ps.setString(2, socio.getApellido());
            ps.setString(3, socio.getEmail());
            ps.setString(4, socio.getTelefono());
            ps.setString(5, socio.getEstadoSocio());
            ps.setLong(6, socio.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el socio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Genera un reporte con los 10 socios que más préstamos han realizado.
     * @return Una lista de arrays de objetos con [Nombre del Socio, Cantidad de Préstamos].
     */
    public List<Object[]> getReporteSociosMasActivos() {
        List<Object[]> reporte = new ArrayList<>();
        String sql = "SELECT CONCAT(s.nombre, ' ', s.apellido) AS socio, COUNT(p.id) AS total_prestamos " +
                "FROM prestamos p " +
                "JOIN socios s ON p.socio_id = s.id " +
                "GROUP BY socio " +
                "ORDER BY total_prestamos DESC " +
                "LIMIT 10";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reporte.add(new Object[]{
                        rs.getString("socio"),
                        rs.getInt("total_prestamos")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reporte;
    }

}