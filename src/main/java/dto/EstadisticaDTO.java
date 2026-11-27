package dto;

public class EstadisticaDTO {
    private int partidasJugadas;
    private int tesorosEncontrados;

    public EstadisticaDTO() {
    }

    public EstadisticaDTO(int partidasJugadas, int tesorosEncontrados) {
        this.partidasJugadas = partidasJugadas;
        this.tesorosEncontrados = tesorosEncontrados;
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
