package com.duoc.foro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "PUBLICACION")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PUBLICACION")
    private Long idPublicacion;

    @NotBlank(message = "El título de la publicación es obligatorio")
    @Size(min = 5, max = 150, message = "El título debe tener entre 5 y 150 caracteres")
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 1000, message = "El contenido no puede exceder los 1000 caracteres")
    @Column(name = "CONTENIDO", nullable = false)
    private String contenido;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @NotBlank(message = "El estado es obligatorio (ACTIVO o BANEADO)")
    @Column(name = "ESTADO", nullable = false)
    private String estado = "ACTIVO";

    @NotNull(message = "Debe indicar el ID del usuario autor")
    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @NotNull(message = "Debe asociar una categoría a la publicación")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;

    public Publicacion() {}

    public Long getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(Long idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}