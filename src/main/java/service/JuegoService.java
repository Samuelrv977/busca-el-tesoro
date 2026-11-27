package service;

import enums.EstadoJuego;
import model.Isla;
import model.Jugador;
import model.Partida;
import model.Pista;
import model.Tesoro;

public class JuegoService {
    private final PartidaService partidaService;
    private final PistaService pistaService;
    private final TesoroService tesoroService;
    private final ReglaService reglaService;
    private final JugadorService jugadorService;
    private final NivelService nivelService;

    public JuegoService(PartidaService partidaService, PistaService pistaService,
                        TesoroService tesoroService, ReglaService reglaService,
                        JugadorService jugadorService, NivelService nivelService) {
        this.partidaService = partidaService;
        this.pistaService = pistaService;
        this.tesoroService = tesoroService;
        this.reglaService = reglaService;
        this.jugadorService = jugadorService;
        this.nivelService = nivelService;
    }

    public Partida iniciarNuevaPartida(Jugador jugador) {
        return partidaService.iniciarPartida(jugador);
    }

    public String moverAIsla(Partida partida, Isla destino) {
        if (!reglaService.validarMovimiento(partida, destino)) {
            return "No es posible viajar a esa isla en este momento.";
        }
        partidaService.avanzarIsla(partida, destino);
        return "Has navegado hasta " + destino.getNombre() + " (" + destino.getTipo() + ").";
    }

    public String resolverPistaActual(Partida partida, String respuesta) {
        if (partida == null || partida.getEstado() != EstadoJuego.EN_CURSO) {
            return "No hay una partida en curso.";
        }
        Isla islaActual = partida.getMapa().getPosicionActual();
        Pista pista = pistaService.obtenerPista(islaActual);
        if (!reglaService.validarPista(pista)) {
            return "La pista de " + islaActual.getNombre() + " ya fue resuelta.";
        }

        if (pistaService.validarRespuesta(pista, respuesta)) {
            jugadorService.actualizarPuntos(partida.getJugador(), pista.getPuntosRecompensa());
            String pistaExtra = generarIndicioTesoro(partida, islaActual);
            return "¡Respuesta correcta! +" + pista.getPuntosRecompensa() + " pts. " + pistaExtra;
        } else {
            partida.setIntentosRestantes(partida.getIntentosRestantes() - 1);
            if (reglaService.validarCondicionDerrota(partida)) {
                partida.setEstado(EstadoJuego.PERDIDO);
                partidaService.finalizarPartida(partida);
                return "Respuesta incorrecta. Te quedaste sin intentos. ¡Partida PERDIDA!";
            }
            return "Respuesta incorrecta. Te quedan " + partida.getIntentosRestantes() + " intentos.";
        }
    }

    public String verificarYReclamarTesoro(Partida partida) {
        if (partida == null || partida.getEstado() != EstadoJuego.EN_CURSO) {
            return "No hay una partida activa.";
        }
        Isla islaActual = partida.getMapa().getPosicionActual();
        Pista pista = pistaService.obtenerPista(islaActual);
        if (pista != null && !pista.isResuelta()) {
            return "Primero debes resolver la pista de " + islaActual.getNombre() + " para explorar en busca del tesoro.";
        }
        if (tesoroService.verificarTesoro(islaActual)) {
            Tesoro tesoro = tesoroService.reclamarTesoro(partida.getJugador(), islaActual);
            partida.setTesoroEncontrado(true);
            if (reglaService.validarCondicionVictoria(partida)) {
                partida.setEstado(EstadoJuego.COMPLETADO);
                nivelService.avanzarNivel(partida.getJugador());
                partidaService.finalizarPartida(partida);
            }
            return "¡TESORO ENCONTRADO! Reclamaste '" + tesoro.getNombre() + "' (+" + tesoro.getPuntos()
                    + " pts) y ascendiste al Nivel " + partida.getJugador().getNivel() + ".";
        }
        return "Has buscado en toda " + islaActual.getNombre() + ", pero el tesoro no está aquí. ¡Sigue explorando!";
    }

    public void finalizarPartida(Partida partida) {
        if (partida != null && partida.getEstado() == EstadoJuego.EN_CURSO) {
            partidaService.finalizarPartida(partida);
        }
    }

    private String generarIndicioTesoro(Partida partida, Isla islaActual) {
        if (tesoroService.verificarTesoro(islaActual)) {
            return "¡Tu brújula vibra intensamente! Hay un tesoro enterrado en ESTA isla. Pulsa 'Verificar / Reclamar Tesoro'.";
        }
        return partida.getMapa().getIslas().stream()
                .filter(i -> i.getTesoro() != null)
                .findFirst()
                .map(islaTesoro -> "Indicio hallado: el tesoro se esconde en una isla de tipo "
                        + islaTesoro.getTipo() + ". Haz clic en la isla señalada con 'SIGUIENTE AQUÍ' o explora otra.")
                .orElse("Sigue explorando el archipiélago.");
    }
}
