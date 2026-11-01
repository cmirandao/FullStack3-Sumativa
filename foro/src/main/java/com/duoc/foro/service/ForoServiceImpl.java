package com.duoc.foro.service;

import com.duoc.foro.model.Categoria;
import com.duoc.foro.model.Comentario;
import com.duoc.foro.model.Publicacion;
import com.duoc.foro.repository.CategoriaRepository;
import com.duoc.foro.repository.ComentarioRepository;
import com.duoc.foro.repository.PublicacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ForoServiceImpl implements ForoService {

    private final CategoriaRepository categoriaRepository;

    private final PublicacionRepository publicacionRepository;

    private final ComentarioRepository comentarioRepository;

    ForoServiceImpl(CategoriaRepository categoriaRepository, PublicacionRepository publicacionRepository, ComentarioRepository comentarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.publicacionRepository = publicacionRepository;
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    public List<Categoria> listarCategorias() {
        log.info("Listando todas las categorías del foro");
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria crearCategoria(Categoria categoria) {
        log.info("Creando nueva categoría: {}", categoria.getNombre());
        return categoriaRepository.save(categoria);
    }

    @Override
    public List<Publicacion> listarPublicaciones() {
        log.info("Obteniendo todas las publicaciones");
        return publicacionRepository.findAll();
    }

    @Override
    public Optional<Publicacion> obtenerPublicacionPorId(Long id) {
        log.debug("Buscando publicación con ID: {}", id);
        return publicacionRepository.findById(id);
    }

    @Override
    public List<Publicacion> buscarPorCategoria(Long idCategoria) {
        log.info("Filtrando publicaciones por categoría ID: {}", idCategoria);
        return publicacionRepository.findByCategoriaIdCategoria(idCategoria);
    }

    @Override
    public List<Publicacion> buscarPorTitulo(String titulo) {
        log.info("Buscando publicaciones que contengan: {}", titulo);
        return publicacionRepository.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public Publicacion crearPublicacion(Publicacion publicacion) {
        log.info("Creando nueva publicación: {}", publicacion.getTitulo());
        return publicacionRepository.save(publicacion);
    }

    @Override
    public Publicacion actualizarPublicacion(Long id, Publicacion publicacion) {
        return publicacionRepository.findById(id).map(existente -> {
            log.info("Actualizando publicación ID: {}", id);
            existente.setTitulo(publicacion.getTitulo());
            existente.setContenido(publicacion.getContenido());
            existente.setEstado(publicacion.getEstado());
            existente.setCategoria(publicacion.getCategoria());
            return publicacionRepository.save(existente);
        }).orElse(null);
    }

    @Override
    public Publicacion cambiarEstadoPublicacion(Long id, String nuevoEstado) {
        return publicacionRepository.findById(id).map(existente -> {
            log.warn("Moderación: Cambiando estado de publicación ID {} a {}", id, nuevoEstado);
            existente.setEstado(nuevoEstado);
            return publicacionRepository.save(existente);
        }).orElse(null);
    }

    @Override
    public void eliminarPublicacion(Long id) {
        log.info("Eliminando publicación ID: {}", id);
        publicacionRepository.deleteById(id);
    }

    @Override
    public List<Comentario> listarComentariosPorPublicacion(Long idPublicacion) {
        log.info("Listando comentarios de la publicación ID: {}", idPublicacion);
        return comentarioRepository.findByIdPublicacion(idPublicacion);
    }

    @Override
    public Comentario crearComentario(Comentario comentario) {
        log.info("Agregando comentario en publicación ID: {}", comentario.getIdPublicacion());
        return comentarioRepository.save(comentario);
    }

    @Override
    public Comentario actualizarComentario(Long id, Comentario comentario) {
        return comentarioRepository.findById(id).map(existente -> {
            existente.setContenido(comentario.getContenido());
            existente.setEstado(comentario.getEstado());
            return comentarioRepository.save(existente);
        }).orElse(null);
    }

    @Override
    public Comentario cambiarEstadoComentario(Long id, String nuevoEstado) {
        return comentarioRepository.findById(id).map(existente -> {
            log.warn("Moderación: Cambiando estado de comentario ID {} a {}", id, nuevoEstado);
            existente.setEstado(nuevoEstado);
            return comentarioRepository.save(existente);
        }).orElse(null);
    }

    @Override
    public void eliminarComentario(Long id) {
        log.info("Eliminando comentario ID: {}", id);
        comentarioRepository.deleteById(id);
    }
}