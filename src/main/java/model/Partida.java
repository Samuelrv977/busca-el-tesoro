package model;

import enums.EstadoJuego;
import java.time.LocalDateTime;

public class Partida {
    private Long id;
    private Jugador jugador;
    private Mapa mapa;
    private EstadoJuego estado;
    private LocalDateTime fechaInicio;
    private int intentosRestantes;
    private boolean tesoroEncontrado;

    public Partida() {
        this.estado = EstadoJuego.EN_CURSO;
        this.fechaInicio = LocalDateTime.now();
        this.intentosRestantes = 4;
        this.tesoroEncontrado = false;
    }

    public Partida(Long id, Jugador jugador, Mapa mapa, EstadoJuego estado, LocalDateTime fechaInicio) {
        this.id = id;
        this.jugador = jugador;
        this.mapa = mapa;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.intentosRestantes = 4;
        this.tesoroEncontrado = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    public void setEstado(EstadoJuego estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }

    public void setIntentosRestantes(int intentosRestantes) {
        this.intentosRestantes = intentosRestantes;
    }

    public boolean isTesoroEncontrado() {
        return tesoroEncontrado;
    }

    public void setTesoroEncontrado(boolean tesoroEncontrado) {
        this.tesoroEncontrado = tesoroEncontrado;
    }
}
