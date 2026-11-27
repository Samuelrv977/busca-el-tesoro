package service;

import dao.JugadorDAO;
import enums.NivelDificultad;
import model.Jugador;

public class NivelService {
    private final JugadorDAO jugadorDAO;

    public NivelService(JugadorDAO jugadorDAO) {
        this.jugadorDAO = jugadorDAO;
    }

    public NivelDificultad obtenerNivelActual(Jugador jugador) {
        if (jugador == null || jugador.getNivel() <= 1) {
            return NivelDificultad.FACIL;
        }
        if (jugador.getNivel() == 2) {
            return NivelDificultad.MEDIO;
        }
        return NivelDificultad.DIFICIL;
    }

    public void avanzarNivel(Jugador jugador) {
        if (jugador == null) {
            return;
        }
        jugador.setNivel(jugador.getNivel() + 1);
        jugadorDAO.guardar(jugador);
    }
}
