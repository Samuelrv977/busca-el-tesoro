package model;

import enums.TipoPista;

import java.util.ArrayList;
import java.util.List;

public class Pista {
    private Long id;
    private String descripcion;
    private TipoPista tipo;
    private String categoria;
    private String respuestaCorrecta;
    private List<String> opciones;
    private boolean resuelta;
    private int puntosRecompensa;

    public Pista() {
        this.opciones = new ArrayList<>();
    }

    public Pista(Long id, String descripcion, TipoPista tipo, String respuestaCorrecta, int puntosRecompensa) {
        this(id, descripcion, tipo, "GENERAL", respuestaCorrecta, puntosRecompensa);
    }

    public Pista(Long id, String descripcion, TipoPista tipo, String categoria,
                 String respuestaCorrecta, int puntosRecompensa) {
        this.id = id;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.categoria = categoria;
        this.respuestaCorrecta = respuestaCorrecta;
        this.puntosRecompensa = puntosRecompensa;
        this.opciones = new ArrayList<>();
        this.resuelta = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoPista getTipo() {
        return tipo;
    }

    public void setTipo(TipoPista tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones != null ? new ArrayList<>(opciones) : new ArrayList<>();
    }

    public boolean isResuelta() {
        return resuelta;
    }

    public void setResuelta(boolean resuelta) {
        this.resuelta = resuelta;
    }

    public int getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public void setPuntosRecompensa(int puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }
}
