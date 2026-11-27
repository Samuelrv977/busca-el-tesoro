package model;

public class Tesoro {
    private Long id;
    private String nombre;
    private String descripcion;
    private int puntos;
    private boolean reclamado;

    public Tesoro() {
    }

    public Tesoro(Long id, String nombre, String descripcion, int puntos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntos = puntos;
        this.reclamado = false;
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

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public boolean isReclamado() {
        return reclamado;
    }

    public void setReclamado(boolean reclamado) {
        this.reclamado = reclamado;
    }

    @Override
    public String toString() {
        return nombre + " (" + puntos + " pts)";
    }
}
