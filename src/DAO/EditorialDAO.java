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
}