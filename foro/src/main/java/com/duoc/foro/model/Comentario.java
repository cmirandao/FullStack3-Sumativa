package com.duoc.foro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMENTARIO")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMENTARIO")
    private Long idComentario;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    @Column(name = "CONTENIDO", nullable = false)
    private String contenido;

    @Column(name = "FECHA_COMENTARIO")
    private LocalDateTime fechaComentario = LocalDateTime.now();

    @Column(name = "ESTADO", nullable = false)
    private String estado = "ACTIVO";

    @NotNull(message = "Debe indicar el ID de la publicación")
    @Column(name = "ID_PUBLICACION", nullable = false)
    private Long idPublicacion;

    @NotNull(message = "Debe indicar el ID del usuario")
    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    public Comentario() {}

    public Long getIdComentario() { return idComentario; }
    public void setIdComentario(Long idComentario) { this.idComentario = idComentario; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaComentario() { return fechaComentario; }
    public void setFechaComentario(LocalDateTime fechaComentario) { this.fechaComentario = fechaComentario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(Long idPublicacion) { this.idPublicacion = idPublicacion; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}