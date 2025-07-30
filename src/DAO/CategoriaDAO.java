package DAO;

import Modelo.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public List<Categoria> obtenerTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setId(rs.getLong("id"));
                cat.setNombre(rs.getString("nombre"));
                categorias.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    /**
     * Registra una nueva categoria en la base de datos.
     * @param categoria El objeto Categoria con los datos a insertar.
     * @return El objeto Categoria con el ID asignado, o null si falla.
     */
    public Categoria registrarCategoria(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre, descripcion) VALUES (?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getLong(1));
                    return categoria;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Error de unicidad: La categoría '" + categoria.getNombre() + "' ya existe.");
            } else {
                e.printStackTrace();
            }
            return null;
        }
    }
}