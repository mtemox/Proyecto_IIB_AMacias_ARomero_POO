package DAO;

import Modelo.Editorial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditorialDAO {
    public List<Editorial> obtenerTodas() {
        List<Editorial> editoriales = new ArrayList<>();
        String sql = "SELECT id, nombre FROM editoriales ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Editorial edt = new Editorial();
                edt.setId(rs.getLong("id"));
                edt.setNombre(rs.getString("nombre"));
                editoriales.add(edt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return editoriales;
    }

    /**
     * Registra una nueva editorial en la base de datos.
     * @param editorial El objeto Editorial con los datos a insertar.
     * @return El objeto Editorial con el ID asignado, o null si falla.
     */
    public Editorial registrarEditorial(Editorial editorial) {
        String sql = "INSERT INTO editoriales (nombre, pais_origen) VALUES (?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, editorial.getNombre());
            ps.setString(2, editorial.getPaisOrigen());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    editorial.setId(generatedKeys.getLong(1));
                    return editorial;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Error de unicidad: La editorial '" + editorial.getNombre() + "' ya existe.");
            } else {
                e.printStackTrace();
            }
            return null;
        }
    }
}