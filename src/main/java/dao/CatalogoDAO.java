package dao;

import database.ConexionBD;
import enums.NivelDificultad;
import enums.TipoIsla;
import enums.TipoPista;
import model.Isla;
import model.Pista;
import model.Tesoro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {
    private final ConexionBD conexionBD;

    public CatalogoDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public List<Isla> listarIslasCatalogo(NivelDificultad dificultad) {
        Connection conn = conexionBD.conectar();
        List<Isla> islas = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, tipo, rel_x, rel_y FROM islas_catalogo ORDER BY id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                islas.add(new Isla(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        dificultad,
                        TipoIsla.valueOf(rs.getString("tipo")),
                        null,
                        null,
                        rs.getDouble("rel_x"),
                        rs.getDouble("rel_y")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar catálogo de islas: " + e.getMessage(), e);
        }
        return islas;
    }

    public List<Pista> listarPistasCatalogo() {
        Connection conn = conexionBD.conectar();
        List<Pista> pistas = new ArrayList<>();
        String sql = "SELECT id, descripcion, tipo, categoria, respuesta_correcta, puntos_recompensa FROM pistas_catalogo ORDER BY id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pistas.add(new Pista(
                        rs.getLong("id"),
                        rs.getString("descripcion"),
                        TipoPista.valueOf(rs.getString("tipo")),
                        rs.getString("categoria"),
                        rs.getString("respuesta_correcta"),
                        rs.getInt("puntos_recompensa")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar catálogo de pistas: " + e.getMessage(), e);
        }
        return pistas;
    }

    public List<String> listarRespuestasPorCategoria(String categoria) {
        Connection conn = conexionBD.conectar();
        List<String> respuestas = new ArrayList<>();
        String sql = "SELECT texto FROM respuestas_catalogo WHERE categoria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    respuestas.add(rs.getString("texto"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar respuestas por categoría: " + e.getMessage(), e);
        }
        return respuestas;
    }

    public List<Tesoro> listarTesorosCatalogo() {
        Connection conn = conexionBD.conectar();
        List<Tesoro> tesoros = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, puntos_base FROM tesoros_catalogo ORDER BY id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tesoros.add(new Tesoro(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("puntos_base")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar catálogo de tesoros: " + e.getMessage(), e);
        }
        return tesoros;
    }
}
