package service;

import model.Isla;
import model.Jugador;
import model.Tesoro;

public class TesoroService {
    private static final int PUNTOS_TESORO_DEFECTO = 150;
    private final JugadorService jugadorService;

    public TesoroService(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    public boolean verificarTesoro(Isla isla) {
        return isla != null && isla.getTesoro() != null && !isla.getTesoro().isReclamado();
    }

    public void reclamarTesoro(Jugador jugador) {
        if (jugador != null) {
            jugadorService.actualizarPuntos(jugador, PUNTOS_TESORO_DEFECTO);
        }
    }

    public Tesoro reclamarTesoro(Jugador jugador, Isla isla) {
        if (jugador == null || !verificarTesoro(isla)) {
            return null;
        }
        Tesoro tesoro = isla.getTesoro();
        tesoro.setReclamado(true);
        jugadorService.actualizarPuntos(jugador, tesoro.getPuntos());
        return tesoro;
    }
}
