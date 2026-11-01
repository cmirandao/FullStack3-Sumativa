package com.duoc.foro.service;

import com.duoc.foro.model.Categoria;
import com.duoc.foro.model.Comentario;
import com.duoc.foro.model.Publicacion;
import java.util.List;
import java.util.Optional;

public interface ForoService {
    // Categorias
    List<Categoria> listarCategorias();
    Categoria crearCategoria(Categoria categoria);

    // Publicaciones
    List<Publicacion> listarPublicaciones();
    Optional<Publicacion> obtenerPublicacionPorId(Long id);
    List<Publicacion> buscarPorCategoria(Long idCategoria);
    List<Publicacion> buscarPorTitulo(String titulo);
    Publicacion crearPublicacion(Publicacion publicacion);
    Publicacion actualizarPublicacion(Long id, Publicacion publicacion);
    Publicacion cambiarEstadoPublicacion(Long id, String nuevoEstado);
    void eliminarPublicacion(Long id);

    // Comentarios
    List<Comentario> listarComentariosPorPublicacion(Long idPublicacion);
    Comentario crearComentario(Comentario comentario);
    Comentario actualizarComentario(Long id, Comentario comentario);
    Comentario cambiarEstadoComentario(Long id, String nuevoEstado);
    void eliminarComentario(Long id);
}