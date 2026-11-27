package service;

import dao.EstadisticaDAO;
import dao.PartidaDAO;
import dto.EstadisticaDTO;
import dto.PartidaDTO;
import model.Estadistica;
import model.Jugador;
import model.Partida;

import java.util.List;

public class EstadisticaService {
    private final EstadisticaDAO estadisticaDAO;
    private final PartidaDAO partidaDAO;

    public EstadisticaService(EstadisticaDAO estadisticaDAO, PartidaDAO partidaDAO) {
        this.estadisticaDAO = estadisticaDAO;
        this.partidaDAO = partidaDAO;
    }

    public void registrarPartida(Jugador jugador) {
        if (jugador == null || jugador.getId() == null) {
            return;
        }
        registrarResultado(jugador.getId(), false);
    }

    public void registrarPartida(Partida partida) {
        if (partida == null || partida.getJugador() == null || partida.getJugador().getId() == null) {
            return;
        }
        registrarResultado(partida.getJugador().getId(), partida.isTesoroEncontrado());
    }

    private void registrarResultado(Long jugadorId, boolean tesoroEncontrado) {
        Estadistica est = estadisticaDAO.consultarPorJugador(jugadorId);
        if (est == null) {
            est = new Estadistica(null, jugadorId, 0, 0);
        }
        est.setPartidasJugadas(est.getPartidasJugadas() + 1);
        if (tesoroEncontrado) {
            est.setTesorosEncontrados(est.getTesorosEncontrados() + 1);
        }
        estadisticaDAO.guardar(est);
    }

    public EstadisticaDTO consultarEstadisticas(Long id) {
        Estadistica est = estadisticaDAO.consultarPorJugador(id);
        if (est == null) {
            return new EstadisticaDTO(0, 0);
        }
        return new EstadisticaDTO(est.getPartidasJugadas(), est.getTesorosEncontrados());
    }

    public List<PartidaDTO> consultarHistorial(Long id) {
        return partidaDAO.listarPorJugador(id).stream()
                .map(p -> new PartidaDTO(p.getId(), p.getFechaInicio(), p.getEstado().name()))
                .toList();
    }
}
