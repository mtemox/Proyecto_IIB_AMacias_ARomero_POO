package DAO;

import Modelo.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    /**
     * Obtiene una lista de todos los libros de la base de datos, incluyendo
     * información de sus autores, categoría y editorial.
     * @return Una lista de objetos Libro.
     */

    public List<Libro> obtenerTodosLosLibros() {
        List<Libro> libros = new ArrayList<>();
        Connection con = ConexionBD.getConexion();

        // Esta consulta usa JOINs para unir las tablas y STRING_AGG para juntar
        // los nombres de los autores si un libro tiene más de uno.
        String sql = "SELECT " +
                "    l.id, l.titulo, l.portada_url, l.cantidad_disponible, " +
                "    STRING_AGG(a.nombre || ' ' || a.apellido, ', ') AS autores, " +
                "    c.nombre AS categoria, " +
                "    e.nombre AS editorial " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id " +
                "LEFT JOIN editoriales e ON l.editorial_id = e.id " +
                "GROUP BY l.id, c.nombre, e.nombre " +
                "ORDER BY l.titulo";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getInt("id"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setPortadaUrl(rs.getString("portada_url"));
                libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                libro.setAutores(rs.getString("autores"));
                libro.setCategoria(rs.getString("categoria"));
                libro.setEditorial(rs.getString("editorial"));

                libros.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los libros: " + e.getMessage());
            e.printStackTrace();
        }

        return libros;
    }
}