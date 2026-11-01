package com.duoc.foro.repository;

import com.duoc.foro.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByIdPublicacion(Long idPublicacion);
}