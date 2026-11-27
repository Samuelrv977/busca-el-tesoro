package dao;

import database.ConexionBD;
import enums.EstadoJuego;
import model.Jugador;
import model.Partida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    private final ConexionBD conexionBD;
    private final JugadorDAO jugadorDAO;

    public PartidaDAO(ConexionBD conexionBD, JugadorDAO jugadorDAO) {
        this.conexionBD = conexionBD;
        this.jugadorDAO = jugadorDAO;
    }

    public void guardar(Partida partida) {
        Connection conn = conexionBD.conectar();
        if (partida.getId() == null) {
            String sql = "INSERT INTO partidas (jugador_id, estado, fecha_inicio, tesoro_encontrado) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"})) {
                ps.setLong(1, partida.getJugador().getId());
                ps.setString(2, partida.getEstado().name());
                ps.setTimestamp(3, Timestamp.valueOf(partida.getFechaInicio()));
                ps.setBoolean(4, partida.isTesoroEncontrado());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        partida.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar partida: " + e.getMessage(), e);
            }
        } else {
            String sql = "UPDATE partidas SET estado = ?, tesoro_encontrado = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, partida.getEstado().name());
                ps.setBoolean(2, partida.isTesoroEncontrado());
                ps.setLong(3, partida.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar partida: " + e.getMessage(), e);
            }
        }
    }

    public Partida consultar(Long id) {
        if (id == null) {
            return null;
        }
        Connection conn = conexionBD.conectar();
        String sql = "SELECT id, jugador_id, estado, fecha_inicio, tesoro_encontrado FROM partidas WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar partida: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Partida> listar() {
        Connection conn = conexionBD.conectar();
        List<Partida> lista = new ArrayList<>();
        String sql = "SELECT id, jugador_id, estado, fecha_inicio, tesoro_encontrado FROM partidas ORDER BY fecha_inicio DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar partidas: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Partida> listarPorJugador(Long jugadorId) {
        Connection conn = conexionBD.conectar();
        List<Partida> lista = new ArrayList<>();
        if (jugadorId == null) {
            return lista;
        }
        String sql = "SELECT id, jugador_id, estado, fecha_inicio, tesoro_encontrado FROM partidas WHERE jugador_id = ? ORDER BY fecha_inicio DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, jugadorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar partidas del jugador: " + e.getMessage(), e);
        }
        return lista;
    }

    private Partida mapear(ResultSet rs) throws SQLException {
        Jugador jugador = jugadorDAO.consultar(rs.getLong("jugador_id"));
        Partida partida = new Partida(
                rs.getLong("id"),
                jugador,
                null,
                EstadoJuego.valueOf(rs.getString("estado")),
                rs.getTimestamp("fecha_inicio").toLocalDateTime()
        );
        partida.setTesoroEncontrado(rs.getBoolean("tesoro_encontrado"));
        return partida;
    }
}
