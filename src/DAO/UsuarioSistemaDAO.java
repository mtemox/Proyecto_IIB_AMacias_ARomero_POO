package DAO;

import Modelo.UsuarioSistema;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioSistemaDAO {

    /**
     * Busca un usuario por su username.
     * @param username El nombre de usuario a buscar.
     * @return Un objeto UsuarioSistema si se encuentra, de lo contrario null.
     */
    public UsuarioSistema obtenerUsuarioPorUsername(String username) {
        Connection con = ConexionBD.getConexion();
        String sql = "SELECT * FROM usuarios_sistema WHERE username = ? AND estado = 'ACTIVO'";
        UsuarioSistema usuario = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioSistema();
                    usuario.setId(rs.getInt("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password")); // ¡IMPORTANTE! Obtenemos la contraseña ENCRIPTADA
                    usuario.setRol(rs.getString("rol"));
                    usuario.setEstado(rs.getString("estado"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }
}