package service;

import enums.EstadoJuego;
import model.Isla;
import model.Partida;
import model.Pista;

public class ReglaService {

    public boolean validarMovimiento(Isla isla) {
        return isla != null && isla.getId() != null;
    }

    public boolean validarMovimiento(Partida partida, Isla isla) {
        if (partida == null || partida.getEstado() != EstadoJuego.EN_CURSO || partida.getMapa() == null) {
            return false;
        }
        return validarMovimiento(isla) && partida.getMapa().getIslas().contains(isla);
    }

    public boolean validarPista(Pista pista) {
        return pista != null && !pista.isResuelta();
    }

    public boolean validarCondicionVictoria(Partida partida) {
        return partida != null && partida.isTesoroEncontrado();
    }

    public boolean validarCondicionDerrota(Partida partida) {
        return partida != null && !partida.isTesoroEncontrado() && partida.getIntentosRestantes() <= 0;
    }
}
