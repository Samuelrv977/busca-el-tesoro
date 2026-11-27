package model;

public class Jugador {
    private Long id;
    private String nombre;
    private int nivel;
    private int puntos;

    public Jugador() {
        this.nivel = 1;
        this.puntos = 0;
    }

    public Jugador(Long id, String nombre, int nivel, int puntos) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.puntos = puntos;
    }

    public Jugador(String nombre) {
        this(null, nombre, 1, 0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return nombre + " (Nivel " + nivel + " - " + puntos + " pts)";
    }
}
