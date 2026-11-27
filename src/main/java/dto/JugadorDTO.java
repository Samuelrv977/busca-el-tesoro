package dto;

public class JugadorDTO {
    private Long id;
    private String nombre;
    private int puntos;

    public JugadorDTO() {
    }

    public JugadorDTO(Long id, String nombre, int puntos) {
        this.id = id;
        this.nombre = nombre;
        this.puntos = puntos;
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

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return "#" + id + " " + nombre + " - " + puntos + " pts";
    }
}
