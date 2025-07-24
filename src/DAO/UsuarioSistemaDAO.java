package DAO;

import Modelo.UsuarioSistema;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                    usuario.setId(rs.getLong("id"));
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

    /**
     * Obtiene una lista de todos los usuarios del sistema.
     * @return Una lista de objetos UsuarioSistema.
     */

    public List<UsuarioSistema> obtenerTodosLosUsuarios() {
        List<UsuarioSistema> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios_sistema ORDER BY username";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UsuarioSistema usuario = new UsuarioSistema();
                usuario.setId(rs.getLong("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password")); // Se obtiene la contraseña (texto plano por ahora)
                usuario.setRol(rs.getString("rol"));
                usuario.setEstado(rs.getString("estado"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * @param usuario El objeto UsuarioSistema con los datos a registrar.
     * @return true si se guardó correctamente, false si hubo un error.
     */

    public boolean registrarUsuario(UsuarioSistema usuario) {
        // <-- CAMBIO: Se eliminan los castings "::rol_usuario" y "::estado_general".
        String sql = "INSERT INTO usuarios_sistema (username, password, rol, estado) VALUES (?, ?, ?, ?)";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            // ¡IMPORTANTE! En un sistema real, la contraseña DEBE ser hasheada aquí antes de guardarla.
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getEstado());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            // El código de error '23505' es para violaciones de unicidad (ej. username duplicado)
            if (e.getSQLState().equals("23505")) {
                System.err.println("El nombre de usuario '" + usuario.getUsername() + "' ya existe.");
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     * @param usuario El objeto UsuarioSistema con los datos actualizados.
     * @return true si se actualizó correctamente, false si hubo un error.
     */

    public boolean actualizarUsuario(UsuarioSistema usuario) {
        // La contraseña se actualiza solo si se proporciona una nueva.
        // <-- CAMBIO: Se eliminan los castings y se simplifica la consulta.
        String sql = "UPDATE usuarios_sistema SET rol = ?, estado = ?"
                + (usuario.getPassword() != null && !usuario.getPassword().isEmpty() ? ", password = ?" : "")
                + " WHERE id = ?";
        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getRol());
            ps.setString(2, usuario.getEstado());

            int paramIndex = 3;
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                // ¡IMPORTANTE! Hashear la contraseña aquí también.
                ps.setString(paramIndex++, usuario.getPassword());
            }
            ps.setLong(paramIndex, usuario.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}