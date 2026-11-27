package model;

public class Estadistica {
    private Long id;
    private Long jugadorId;
    private int partidasJugadas;
    private int tesorosEncontrados;

    public Estadistica() {
    }

    public Estadistica(Long id, Long jugadorId, int partidasJugadas, int tesorosEncontrados) {
        this.id = id;
        this.jugadorId = jugadorId;
        this.partidasJugadas = partidasJugadas;
        this.tesorosEncontrados = tesorosEncontrados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int getTesorosEncontrados() {
        return tesorosEncontrados;
    }

    public void setTesorosEncontrados(int tesorosEncontrados) {
        this.tesorosEncontrados = tesorosEncontrados;
    }
}
