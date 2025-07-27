package DAO;

import Modelo.Libro;

import java.sql.*;
import java.util.Arrays;
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
        // <-- CAMBIO: Se usa STRING_AGG en lugar de GROUP_CONCAT y '+' para concatenar.
        String sql = "SELECT " +
                "    l.id, l.titulo, l.portada_url, l.cantidad_disponible, " +
                "    STRING_AGG(a.nombre + ' ' + a.apellido, ', ') AS autores, " +
                "    c.nombre AS categoria, " +
                "    e.nombre AS editorial " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id " +
                "LEFT JOIN editoriales e ON l.editorial_id = e.id " +
                "GROUP BY l.id, l.titulo, l.portada_url, l.cantidad_disponible, c.nombre, e.nombre " +
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
        // <-- CAMBIO: Se usa STRING_AGG y '+'.
        String sql = "SELECT " +
                "    l.id, l.titulo, l.portada_url, l.cantidad_disponible, " +
                "    STRING_AGG(a.nombre + ' ' + a.apellido, ', ') AS autores " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "WHERE l.isbn = ? AND l.cantidad_disponible > 0 " +
                "GROUP BY l.id, l.titulo, l.portada_url, l.cantidad_disponible";

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
        // <-- CAMBIO: Se usa STRING_AGG y se convierte a.id a NVARCHAR para la agregación.
        String sql = "SELECT " +
                "  l.*, " +
                "  c.id AS categoria_id, c.nombre AS categoria, c.descripcion AS descripcion_categoria, " +
                "  e.id AS editorial_id, e.nombre AS editorial, " +
                "  STRING_AGG(CAST(a.id AS NVARCHAR(MAX)), ',') AS autores_ids, " +
                "  STRING_AGG(a.nombre + ' ' + a.apellido, ', ') AS autores " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id " +
                "LEFT JOIN editoriales e ON l.editorial_id = e.id " +
                "WHERE l.id = ? " +
                "GROUP BY l.id, l.isbn, l.titulo, l.anio_publicacion, l.portada_url, l.cantidad_total, " +
                "l.cantidad_disponible, l.editorial_id, l.categoria_id, c.id, c.nombre, " +
                "c.descripcion, e.id, e.nombre";

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

                    // Nombres para mostrar
                    libro.setAutores(rs.getString("autores"));
                    libro.setCategoria(rs.getString("categoria"));
                    libro.setDescripcionCategoria(rs.getString("descripcion_categoria"));
                    libro.setEditorial(rs.getString("editorial"));

                    // <-- CAMBIO: Ahora sí poblamos los IDs necesarios para el formulario de edición
                    libro.setCategoriaId(rs.getInt("categoria_id"));
                    libro.setEditorialId(rs.getInt("editorial_id"));

                    String autoresIdsStr = rs.getString("autores_ids");
                    if (autoresIdsStr != null) {
                        libro.setAutoresIds(Arrays.asList(autoresIdsStr.split(",")));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los detalles del libro: " + e.getMessage());
            e.printStackTrace();
        }
        return libro;
    }

    public boolean registrarLibro(Libro libro, List<Long> autoresIds) { // <-- CAMBIO 1: de Integer a Long
        Connection con = ConexionBD.getConexion();
        // Se pide que la BD devuelva las llaves generadas (el ID del nuevo libro)
        String sqlLibro = "INSERT INTO libros (isbn, titulo, anio_publicacion, portada_url, cantidad_total, cantidad_disponible, editorial_id, categoria_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlLibroAutor = "INSERT INTO libros_autores (libro_id, autor_id) VALUES (?, ?)";

        try {
            con.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar el libro y obtener su ID generado
            long libroId;
            try (PreparedStatement ps = con.prepareStatement(sqlLibro, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, libro.getIsbn());
                ps.setString(2, libro.getTitulo());
                ps.setInt(3, libro.getAnioPublicacion());
                ps.setString(4, libro.getPortadaUrl());
                ps.setInt(5, libro.getCantidadTotal());
                ps.setInt(6, libro.getCantidadDisponible());
                ps.setLong(7, libro.getEditorialId());
                ps.setLong(8, libro.getCategoriaId());
                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        libroId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del libro creado.");
                    }
                }
            }

            // 2. Insertar las relaciones en la tabla libros_autores
            try (PreparedStatement ps = con.prepareStatement(sqlLibroAutor)) {
                for (Long autorId : autoresIds) { // <-- CAMBIO 2: de Integer a Long
                    ps.setLong(1, libroId);
                    ps.setLong(2, autorId); // <-- CAMBIO 3: de setInt a setLong
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            // ... (tu manejo de errores se queda igual)
            System.err.println("Error en transacción de registro de libro. Revirtiendo cambios...");
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean actualizarLibro(Libro libro, List<Long> autoresIds) { // <-- CAMBIO 1: de Integer a Long
        Connection con = ConexionBD.getConexion();
        String sqlUpdateLibro = "UPDATE libros SET isbn = ?, titulo = ?, anio_publicacion = ?, portada_url = ?, cantidad_total = ?, cantidad_disponible = ?, editorial_id = ?, categoria_id = ? WHERE id = ?";
        String sqlDeleteAutores = "DELETE FROM libros_autores WHERE libro_id = ?";
        String sqlInsertAutores = "INSERT INTO libros_autores (libro_id, autor_id) VALUES (?, ?)";

        try {
            con.setAutoCommit(false);

            // 1. Actualizar los datos del libro
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateLibro)) {
                ps.setString(1, libro.getIsbn());
                ps.setString(2, libro.getTitulo());
                ps.setInt(3, libro.getAnioPublicacion());
                ps.setString(4, libro.getPortadaUrl());
                ps.setInt(5, libro.getCantidadTotal());
                ps.setInt(6, libro.getCantidadDisponible());
                ps.setLong(7, libro.getEditorialId());
                ps.setLong(8, libro.getCategoriaId());
                ps.setLong(9, libro.getId());
                ps.executeUpdate();
            }

            // 2. Borrar los autores antiguos
            try (PreparedStatement ps = con.prepareStatement(sqlDeleteAutores)) {
                ps.setLong(1, libro.getId());
                ps.executeUpdate();
            }

            // 3. Insertar los nuevos autores
            try (PreparedStatement ps = con.prepareStatement(sqlInsertAutores)) {
                for (Long autorId : autoresIds) { // <-- CAMBIO 2: de Integer a Long
                    ps.setLong(1, libro.getId());
                    ps.setLong(2, autorId); // <-- CAMBIO 3: de setInt a setLong
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            // ... (tu manejo de errores se queda igual)
            System.err.println("Error al actualizar libro. Revirtiendo cambios...");
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public boolean eliminarLibro(long libroId) {
        String sql = "DELETE FROM libros WHERE id = ?";
        Connection con = ConexionBD.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, libroId);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el libro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Genera un reporte con los 10 libros más prestados.
     * @return Una lista de arrays de objetos con [Título, Cantidad de Préstamos].
     */

    public List<Object[]> getReporteLibrosMasPrestados() {
        List<Object[]> reporte = new ArrayList<>();
        // <-- CAMBIO: Se usa TOP 10 en lugar de LIMIT 10.
        String sql = "SELECT TOP 10 l.titulo, COUNT(p.id) AS total_prestamos " +
                "FROM prestamos p " +
                "JOIN libros l ON p.libro_id = l.id " +
                "GROUP BY l.titulo " +
                "ORDER BY total_prestamos DESC "; // Limitamos a los 10 primeros
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reporte.add(new Object[]{
                        rs.getString("titulo"),
                        rs.getInt("total_prestamos")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reporte;
    }

    /**
     * Busca libros cuyo título, autor, ISBN, categoría o editorial coincidan con el término de búsqueda.
     * Si el término de búsqueda está vacío, devuelve todos los libros.
     * @param termino El texto a buscar.
     * @return Una lista de objetos Libro que coinciden con la búsqueda.
     */
    public List<Libro> buscarLibros(String termino) {
        List<Libro> libros = new ArrayList<>();
        // <-- CAMBIO: Se usa STRING_AGG y '+'.
        String sql = "SELECT " +
                "  l.id, l.titulo, l.portada_url, l.cantidad_disponible, " +
                "  STRING_AGG(a.nombre + ' ' + a.apellido, ', ') AS autores, " +
                "  c.nombre AS categoria, " +
                "  e.nombre AS editorial " +
                "FROM libros l " +
                "LEFT JOIN libros_autores la ON l.id = la.libro_id " +
                "LEFT JOIN autores a ON la.autor_id = a.id " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id " +
                "LEFT JOIN editoriales e ON l.editorial_id = e.id " +
                "WHERE l.titulo LIKE ? OR a.nombre LIKE ? OR a.apellido LIKE ? OR l.isbn LIKE ? " +
                "GROUP BY l.id, l.titulo, l.portada_url, l.cantidad_disponible, c.nombre, e.nombre " +
                "ORDER BY l.titulo";

        Connection con = ConexionBD.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String parametroBusqueda = "%" + termino + "%"; // Añadimos wildcards para búsqueda parcial
            ps.setString(1, parametroBusqueda);
            ps.setString(2, parametroBusqueda);
            ps.setString(3, parametroBusqueda);
            ps.setString(4, parametroBusqueda);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getLong("id"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setPortadaUrl(rs.getString("portada_url"));
                libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                libro.setAutores(rs.getString("autores"));
                libro.setCategoria(rs.getString("categoria"));
                libro.setEditorial(rs.getString("editorial"));
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar libros: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

}