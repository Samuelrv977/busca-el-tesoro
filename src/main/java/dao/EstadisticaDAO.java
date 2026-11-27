package dao;

import database.ConexionBD;
import model.Estadistica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstadisticaDAO {
    private final ConexionBD conexionBD;

    public EstadisticaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public void guardar(Estadistica estadistica) {
        Connection conn = conexionBD.conectar();
        if (estadistica.getId() == null) {
            String sql = "INSERT INTO estadisticas (jugador_id, partidas_jugadas, tesoros_encontrados) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"})) {
                ps.setLong(1, estadistica.getJugadorId());
                ps.setInt(2, estadistica.getPartidasJugadas());
                ps.setInt(3, estadistica.getTesorosEncontrados());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        estadistica.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar estadística: " + e.getMessage(), e);
            }
        } else {
            String sql = "UPDATE estadisticas SET partidas_jugadas = ?, tesoros_encontrados = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, estadistica.getPartidasJugadas());
                ps.setInt(2, estadistica.getTesorosEncontrados());
                ps.setLong(3, estadistica.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar estadística: " + e.getMessage(), e);
            }
        }
    }

    public Estadistica consultar(Long id) {
        if (id == null) {
            return null;
        }
        Connection conn = conexionBD.conectar();
        String sql = "SELECT id, jugador_id, partidas_jugadas, tesoros_encontrados FROM estadisticas WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar estadística: " + e.getMessage(), e);
        }
        return null;
    }

    public Estadistica consultarPorJugador(Long jugadorId) {
        if (jugadorId == null) {
            return null;
        }
        Connection conn = conexionBD.conectar();
        String sql = "SELECT id, jugador_id, partidas_jugadas, tesoros_encontrados FROM estadisticas WHERE jugador_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, jugadorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar estadística del jugador: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Estadistica> listar() {
        Connection conn = conexionBD.conectar();
        List<Estadistica> lista = new ArrayList<>();
        String sql = "SELECT id, jugador_id, partidas_jugadas, tesoros_encontrados FROM estadisticas ORDER BY tesoros_encontrados DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar estadísticas: " + e.getMessage(), e);
        }
        return lista;
    }

    private Estadistica mapear(ResultSet rs) throws SQLException {
        return new Estadistica(
                rs.getLong("id"),
                rs.getLong("jugador_id"),
                rs.getInt("partidas_jugadas"),
                rs.getInt("tesoros_encontrados")
        );
    }
}
