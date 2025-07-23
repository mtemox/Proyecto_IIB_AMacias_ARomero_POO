package DAO;

import Modelo.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

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

    /**
     * Busca un libro por su ISBN y verifica que haya disponibles.
     * @param isbn El ISBN del libro a buscar.
     * @return Un objeto Libro si se encuentra y hay disponibles, de lo contrario null.
     */

    public Libro buscarPorIsbn(String isbn) {
        Libro libro = null;
        // La consulta es similar a la de obtener todos, para traer también el autor.
        String sql = "SELECT " +
                "    l.id, l.titulo, l.portada_url, l.cantidad_disponible, " +
                "    STRING_AGG(a.nombre || ' ' || a.apellido, ', ') AS autores " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "WHERE l.isbn = ? AND l.cantidad_disponible > 0 " +
                "GROUP BY l.id";

        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    libro = new Libro();
                    libro.setId(rs.getInt("id"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                    libro.setAutores(rs.getString("autores"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar libro por ISBN: " + e.getMessage());
            e.printStackTrace();
        }
        return libro;
    }

    /**
     * Obtiene todos los detalles de un libro específico por su ID.
     * @param libroId El ID del libro a buscar.
     * @return Un objeto Libro con todos sus datos, o null si no se encuentra.
     */

    public Libro obtenerDetallesLibro(long libroId) {
        Libro libro = null;
        String sql = "SELECT " +
                "  l.*, " +
                "  STRING_AGG(a.nombre || ' ' || a.apellido, ', ') AS autores, " +
                "  c.nombre AS categoria, c.descripcion AS descripcion_categoria, " +
                "  e.nombre AS editorial " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id " +
                "LEFT JOIN editoriales e ON l.editorial_id = e.id " +
                "WHERE l.id = ? " +
                "GROUP BY l.id, c.nombre, c.descripcion, e.nombre";

        Connection con = ConexionBD.getConexion();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    libro = new Libro();
                    libro.setId(rs.getLong("id"));
                    libro.setIsbn(rs.getString("isbn"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAnioPublicacion(rs.getInt("anio_publicacion"));
                    libro.setPortadaUrl(rs.getString("portada_url"));
                    libro.setCantidadTotal(rs.getInt("cantidad_total"));
                    libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                    libro.setAutores(rs.getString("autores"));
                    libro.setCategoria(rs.getString("categoria"));
                    libro.setDescripcionCategoria(rs.getString("descripcion_categoria"));
                    libro.setEditorial(rs.getString("editorial"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los detalles del libro: " + e.getMessage());
            e.printStackTrace();
        }
        return libro;
    }

}