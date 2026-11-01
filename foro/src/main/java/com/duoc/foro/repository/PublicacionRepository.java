package com.duoc.foro.repository;

import com.duoc.foro.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByCategoriaIdCategoria(Long idCategoria);
    List<Publicacion> findByTituloContainingIgnoreCase(String palabraClave);
    List<Publicacion> findByEstado(String estado);
}