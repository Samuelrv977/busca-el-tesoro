package controller;

import model.Isla;
import model.Mapa;
import model.Partida;
import model.Pista;
import service.PartidaService;
import service.PistaService;
import service.TesoroService;

public class ExploradorController {
    private final PartidaService partidaService;
    private final PistaService pistaService;
    private final TesoroService tesoroService;
    private final JuegoController juegoController;

    private String ultimoMensaje;
    private Runnable onActualizarVista;

    public ExploradorController(PartidaService partidaService, PistaService pistaService,
                                TesoroService tesoroService, JuegoController juegoController) {
        this.partidaService = partidaService;
        this.pistaService = pistaService;
        this.tesoroService = tesoroService;
        this.juegoController = juegoController;
        this.ultimoMensaje = "";
    }

    public void setOnActualizarVista(Runnable onActualizarVista) {
        this.onActualizarVista = onActualizarVista;
    }

    public void mostrarMapa() {
        Partida partida = juegoController.getPartidaActual();
        if (partida != null && partida.getMapa() != null) {
            ultimoMensaje = "Mostrando archipiélago con " + partida.getMapa().getIslas().size() + " islas.";
            notificarVista();
        }
    }

    public void seleccionarIsla(Long id) {
        Partida partida = juegoController.getPartidaActual();
        if (partida == null || partida.getMapa() == null || id == null) {
            return;
        }
        Mapa mapa = partida.getMapa();
        Isla destino = mapa.getIslas().stream()
                .filter(i -> id.equals(i.getId()))
                .findFirst()
                .orElse(null);

        if (destino != null) {
            juegoController.procesarMovimiento(destino);
            ultimoMensaje = juegoController.getUltimoMensaje();
            notificarVista();
        }
    }

    public void mostrarPista() {
        Pista pista = obtenerPistaActual();
        if (pista != null) {
            ultimoMensaje = "[" + pista.getTipo() + "] " + pista.getDescripcion();
        } else {
            ultimoMensaje = "Esta isla no tiene pistas disponibles.";
        }
        notificarVista();
    }

    public void verificarTesoro() {
        Partida partida = juegoController.getPartidaActual();
        if (partida == null || partida.getMapa() == null) {
            ultimoMensaje = "No hay una partida en curso.";
            notificarVista();
            return;
        }
        Isla actual = partida.getMapa().getPosicionActual();
        boolean hayTesoro = tesoroService.verificarTesoro(actual);
        ultimoMensaje = juegoController.verificarYReclamarTesoro();
        if (hayTesoro && ultimoMensaje.isEmpty()) {
            ultimoMensaje = "¡Tesoro detectado en " + actual.getNombre() + "!";
        }
        notificarVista();
    }

    public Pista obtenerPistaActual() {
        Partida partida = juegoController.getPartidaActual();
        if (partida == null || partida.getMapa() == null) {
            return null;
        }
        return pistaService.obtenerPista(partida.getMapa().getPosicionActual());
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    private void notificarVista() {
        if (onActualizarVista != null) {
            onActualizarVista.run();
        }
    }
}
