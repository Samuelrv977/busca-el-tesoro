package service;

import dao.JugadorDAO;
import dto.JugadorDTO;
import model.Jugador;

import java.util.List;

public class JugadorService {
    private final JugadorDAO jugadorDAO;

    public JugadorService(JugadorDAO jugadorDAO) {
        this.jugadorDAO = jugadorDAO;
    }

    public Jugador crearJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        Jugador jugador = new Jugador(nombre.trim());
        jugadorDAO.guardar(jugador);
        return jugador;
    }

    public Jugador obtenerPerfil(Long id) {
        return jugadorDAO.consultar(id);
    }

    public void actualizarPuntos(Jugador jugador, int puntos) {
        if (jugador == null) {
            return;
        }
        jugador.setPuntos(Math.max(0, jugador.getPuntos() + puntos));
        jugadorDAO.guardar(jugador);
    }

    public List<Jugador> listarJugadores() {
        return jugadorDAO.listar();
    }

    public List<JugadorDTO> listarJugadoresDTO() {
        return jugadorDAO.listar().stream()
                .map(j -> new JugadorDTO(j.getId(), j.getNombre(), j.getPuntos()))
                .toList();
    }
}
