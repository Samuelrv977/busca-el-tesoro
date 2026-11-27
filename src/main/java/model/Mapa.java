package model;

import java.util.ArrayList;
import java.util.List;

public class Mapa {
    private Long id;
    private List<Isla> islas;
    private Isla posicionActual;

    public Mapa() {
        this.islas = new ArrayList<>();
    }

    public Mapa(Long id, List<Isla> islas, Isla posicionActual) {
        this.id = id;
        this.islas = islas != null ? islas : new ArrayList<>();
        this.posicionActual = posicionActual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Isla> getIslas() {
        return islas;
    }

    public void setIslas(List<Isla> islas) {
        this.islas = islas;
    }

    public Isla getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Isla posicionActual) {
        this.posicionActual = posicionActual;
    }
}
