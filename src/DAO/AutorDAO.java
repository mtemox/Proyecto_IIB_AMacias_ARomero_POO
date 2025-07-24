package DAO;

import Modelo.Autor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {
    public List<Autor> obtenerTodos() {
        List<Autor> autores = new ArrayList<>();
        // Usamos CONCAT para unir nombre y apellido para mostrarlo en la lista
        String sql = "SELECT id, CONCAT(nombre, ' ', apellido) AS nombre_completo FROM autores ORDER BY apellido, nombre";
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
}