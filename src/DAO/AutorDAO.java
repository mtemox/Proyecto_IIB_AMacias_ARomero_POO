package DAO;

import Modelo.Autor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {
    public List<Autor> obtenerTodos() {
        List<Autor> autores = new ArrayList<>();
        // <-- CAMBIO: Se usa el operador '+' en lugar de CONCAT para SQL Server.
        String sql = "SELECT id, nombre + ' ' + apellido AS nombre_completo FROM autores ORDER BY apellido, nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Autor autor = new Autor();
                autor.setId(rs.getLong("id"));
                autor.setNombreCompleto(rs.getString("nombre_completo"));
                autores.add(autor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return autores;
    }

    /**
     * Registra un nuevo autor en la base de datos.
     * @param autor El objeto Autor con los datos a insertar (sin ID).
     * @return El objeto Autor con el ID asignado por la base de datos, o null si falla.
     */
    public Autor registrarAutor(Autor autor) {
        String sql = "INSERT INTO autores (nombre, apellido, fecha_nacimiento, nacionalidad) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            if (autor.getFechaNacimiento() != null) {
                ps.setDate(3, java.sql.Date.valueOf(autor.getFechaNacimiento()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, autor.getNacionalidad());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    autor.setId(generatedKeys.getLong(1));
                    autor.setNombreCompleto(autor.getNombre() + " " + autor.getApellido());
                    return autor;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}