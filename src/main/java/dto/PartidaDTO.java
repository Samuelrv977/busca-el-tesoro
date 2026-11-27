package dto;

import java.time.LocalDateTime;

public class PartidaDTO {
    private Long id;
    private LocalDateTime fecha;
    private String estado;

    public PartidaDTO() {
    }

    public PartidaDTO(Long id, LocalDateTime fecha, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
