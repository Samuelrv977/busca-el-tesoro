package service;

import dao.PartidaDAO;
import enums.EstadoJuego;
import enums.NivelDificultad;
import model.Isla;
import model.Jugador;
import model.Mapa;
import model.Partida;

import java.time.LocalDateTime;

public class PartidaService {
    private final PartidaDAO partidaDAO;
    private final MapaService mapaService;
    private final NivelService nivelService;
    private final EstadisticaService estadisticaService;

    public PartidaService(PartidaDAO partidaDAO, MapaService mapaService,
                          NivelService nivelService, EstadisticaService estadisticaService) {
        this.partidaDAO = partidaDAO;
        this.mapaService = mapaService;
        this.nivelService = nivelService;
        this.estadisticaService = estadisticaService;
    }

    public Partida iniciarPartida(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Se requiere un jugador válido para iniciar la partida.");
        }
        NivelDificultad dificultad = nivelService.obtenerNivelActual(jugador);
        Mapa mapa = mapaService.generarMapa(dificultad);
        Partida partida = new Partida(null, jugador, mapa, EstadoJuego.EN_CURSO, LocalDateTime.now());
        partida.setIntentosRestantes(switch (dificultad) {
            case FACIL -> 4;
            case MEDIO -> 3;
            case DIFICIL -> 3;
        });
        partidaDAO.guardar(partida);
        return partida;
    }

    public void avanzarIsla(Partida partida, Isla isla) {
        if (partida == null || partida.getMapa() == null || isla == null) {
            return;
        }
        partida.getMapa().setPosicionActual(isla);
        isla.setVisitada(true);
        partidaDAO.guardar(partida);
    }

    public void finalizarPartida(Partida partida) {
        if (partida == null) {
            return;
        }
        if (partida.getEstado() == EstadoJuego.EN_CURSO) {
            partida.setEstado(partida.isTesoroEncontrado() ? EstadoJuego.COMPLETADO : EstadoJuego.PERDIDO);
        }
        partidaDAO.guardar(partida);
        estadisticaService.registrarPartida(partida);
    }
}
