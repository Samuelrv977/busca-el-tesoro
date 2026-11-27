package model;

import enums.NivelDificultad;
import enums.TipoIsla;

public class Isla {
    private Long id;
    private String nombre;
    private String descripcion;
    private NivelDificultad dificultad;
    private TipoIsla tipo;
    private Pista pista;
    private Tesoro tesoro;
    private boolean visitada;
    private double relX;
    private double relY;

    public Isla() {
    }

    public Isla(Long id, String nombre, String descripcion, NivelDificultad dificultad, TipoIsla tipo,
                Pista pista, Tesoro tesoro, double relX, double relY) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.dificultad = dificultad;
        this.tipo = tipo;
        this.pista = pista;
        this.tesoro = tesoro;
        this.relX = relX;
        this.relY = relY;
        this.visitada = false;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public NivelDificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(NivelDificultad dificultad) {
        this.dificultad = dificultad;
    }

    public TipoIsla getTipo() {
        return tipo;
    }

    public void setTipo(TipoIsla tipo) {
        this.tipo = tipo;
    }

    public Pista getPista() {
        return pista;
    }

    public void setPista(Pista pista) {
        this.pista = pista;
    }

    public Tesoro getTesoro() {
        return tesoro;
    }

    public void setTesoro(Tesoro tesoro) {
        this.tesoro = tesoro;
    }

    public boolean isVisitada() {
        return visitada;
    }

    public void setVisitada(boolean visitada) {
        this.visitada = visitada;
    }

    public double getRelX() {
        return relX;
    }

    public void setRelX(double relX) {
        this.relX = relX;
    }

    public double getRelY() {
        return relY;
    }

    public void setRelY(double relY) {
        this.relY = relY;
    }

    @Override
    public String toString() {
        return nombre + " [" + tipo + "]";
    }
}
