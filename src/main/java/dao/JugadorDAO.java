package dao;

import database.ConexionBD;
import model.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {
    private final ConexionBD conexionBD;

    public JugadorDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public void guardar(Jugador jugador) {
        Connection conn = conexionBD.conectar();
        if (jugador.getId() == null) {
            String sql = "INSERT INTO jugadores (nombre, nivel, puntos) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"})) {
                ps.setString(1, jugador.getNombre());
                ps.setInt(2, jugador.getNivel());
                ps.setInt(3, jugador.getPuntos());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        jugador.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar jugador: " + e.getMessage(), e);
            }
        } else {
            String sql = "UPDATE jugadores SET nombre = ?, nivel = ?, puntos = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, jugador.getNombre());
                ps.setInt(2, jugador.getNivel());
                ps.setInt(3, jugador.getPuntos());
                ps.setLong(4, jugador.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar jugador: " + e.getMessage(), e);
            }
        }
    }

    public Jugador consultar(Long id) {
        if (id == null) {
            return null;
        }
        Connection conn = conexionBD.conectar();
        String sql = "SELECT id, nombre, nivel, puntos FROM jugadores WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar jugador: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Jugador> listar() {
        Connection conn = conexionBD.conectar();
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, nivel, puntos FROM jugadores ORDER BY puntos DESC, id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar jugadores: " + e.getMessage(), e);
        }
        return lista;
    }

    private Jugador mapear(ResultSet rs) throws SQLException {
        return new Jugador(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getInt("nivel"),
                rs.getInt("puntos")
        );
    }
}
