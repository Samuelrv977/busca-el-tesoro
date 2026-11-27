package controller;

import dto.EstadisticaDTO;
import dto.JugadorDTO;
import dto.PartidaDTO;
import model.Isla;
import model.Jugador;
import model.Partida;
import service.EstadisticaService;
import service.JuegoService;
import service.JugadorService;
import service.MapaService;
import service.NivelService;

import java.util.List;

public class JuegoController {
    private final JuegoService juegoService;
    private final JugadorService jugadorService;
    private final MapaService mapaService;
    private final EstadisticaService estadisticaService;
    private final NivelService nivelService;

    private Jugador jugadorActual;
    private Partida partidaActual;
    private String ultimoMensaje;

    public JuegoController(JuegoService juegoService, JugadorService jugadorService,
                           MapaService mapaService, EstadisticaService estadisticaService,
                           NivelService nivelService) {
        this.juegoService = juegoService;
        this.jugadorService = jugadorService;
        this.mapaService = mapaService;
        this.estadisticaService = estadisticaService;
        this.nivelService = nivelService;
        this.ultimoMensaje = "Bienvenido a la Búsqueda del Tesoro.";
    }

    public void iniciarJuego() {
        if (jugadorActual == null) {
            List<Jugador> existentes = jugadorService.listarJugadores();
            jugadorActual = existentes.isEmpty()
                    ? jugadorService.crearJugador("Explorador")
                    : existentes.get(0);
        } else {
            Jugador refrescado = jugadorService.obtenerPerfil(jugadorActual.getId());
            if (refrescado != null) {
                jugadorActual.setNombre(refrescado.getNombre());
                jugadorActual.setNivel(refrescado.getNivel());
                jugadorActual.setPuntos(refrescado.getPuntos());
            }
        }
        partidaActual = juegoService.iniciarNuevaPartida(jugadorActual);
        ultimoMensaje = "¡Nueva expedición iniciada en dificultad "
                + nivelService.obtenerNivelActual(jugadorActual)
                + "! Haz clic en las islas del mapa para explorar y resolver sus pistas.";
    }

    public void procesarMovimiento(Isla isla) {
        if (partidaActual == null) {
            ultimoMensaje = "No hay una partida activa.";
            return;
        }
        if (!mapaService.validarDestino(partidaActual.getMapa(), isla)) {
            ultimoMensaje = "Destino inválido en el mapa actual.";
            return;
        }
        ultimoMensaje = juegoService.moverAIsla(partidaActual, isla);
    }

    public void resolverPista(String respuesta) {
        if (partidaActual == null) {
            ultimoMensaje = "No hay una partida activa.";
            return;
        }
        ultimoMensaje = juegoService.resolverPistaActual(partidaActual, respuesta);
    }

    public void finalizarJuego() {
        if (partidaActual != null) {
            juegoService.finalizarPartida(partidaActual);
            ultimoMensaje = "Partida finalizada con estado: " + partidaActual.getEstado() + ".";
        }
    }

    public String verificarYReclamarTesoro() {
        if (partidaActual == null) {
            ultimoMensaje = "No hay una partida activa.";
            return ultimoMensaje;
        }
        ultimoMensaje = juegoService.verificarYReclamarTesoro(partidaActual);
        return ultimoMensaje;
    }

    public Jugador crearJugador(String nombre) {
        jugadorActual = jugadorService.crearJugador(nombre);
        return jugadorActual;
    }

    public List<Jugador> listarJugadores() {
        return jugadorService.listarJugadores();
    }

    public List<JugadorDTO> listarJugadoresDTO() {
        return jugadorService.listarJugadoresDTO();
    }

    public void seleccionarJugador(Jugador jugador) {
        this.jugadorActual = jugador;
    }

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public Partida getPartidaActual() {
        return partidaActual;
    }

    public List<Isla> obtenerIslasMapaActual() {
        if (partidaActual == null) {
            return List.of();
        }
        return mapaService.obtenerIslas(partidaActual.getMapa());
    }

    public EstadisticaDTO consultarEstadisticas(Long jugadorId) {
        return estadisticaService.consultarEstadisticas(jugadorId);
    }

    public List<PartidaDTO> consultarHistorial(Long jugadorId) {
        return estadisticaService.consultarHistorial(jugadorId);
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }
}
