package com.duoc.foro.controller;

import com.duoc.foro.model.Categoria;
import com.duoc.foro.model.Comentario;
import com.duoc.foro.model.Publicacion;
import com.duoc.foro.service.ForoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foro")
@CrossOrigin(origins = "*")
public class ForoController {

    private final ForoService foroService;

    ForoController(ForoService foroService) {
        this.foroService = foroService;
    }

    // --- CATEGORIAS ---
    @GetMapping("/categorias")
    public List<Categoria> obtenerCategorias() {
        return foroService.listarCategorias();
    }

    @PostMapping("/categorias")
    public Categoria crearCategoria(@Valid @RequestBody Categoria categoria) {
        return foroService.crearCategoria(categoria);
    }

    // --- PUBLICACIONES ---
    @GetMapping("/publicaciones")
    public List<Publicacion> obtenerPublicaciones() {
        return foroService.listarPublicaciones();
    }

    @GetMapping("/publicaciones/{id}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable Long id) {
        return foroService.obtenerPublicacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/publicaciones/categoria/{idCategoria}")
    public List<Publicacion> buscarPorCategoria(@PathVariable Long idCategoria) {
        return foroService.buscarPorCategoria(idCategoria);
    }

    @GetMapping("/publicaciones/buscar")
    public List<Publicacion> buscarPorTitulo(@RequestParam String q) {
        return foroService.buscarPorTitulo(q);
    }

    @PostMapping("/publicaciones")
    public Publicacion crearPublicacion(@Valid @RequestBody Publicacion publicacion) {
        return foroService.crearPublicacion(publicacion);
    }

    @PutMapping("/publicaciones/{id}")
    public ResponseEntity<Publicacion> actualizarPublicacion(@PathVariable Long id, @Valid @RequestBody Publicacion publicacion) {
        Publicacion actualizada = foroService.actualizarPublicacion(id, publicacion);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    // --- MODERAR PUBLICACIONES ---
    @PutMapping("/publicaciones/{id}/estado")
    public ResponseEntity<Publicacion> moderarPublicacion(@PathVariable Long id, @RequestParam String estado) {
        Publicacion moderada = foroService.cambiarEstadoPublicacion(id, estado);
        return moderada != null ? ResponseEntity.ok(moderada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/publicaciones/{id}")
    public ResponseEntity<Void> eliminarPublicacion(@PathVariable Long id) {
        foroService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }

    // --- COMENTARIOS ---
    @GetMapping("/comentarios/publicacion/{idPublicacion}")
    public List<Comentario> obtenerComentariosPorPublicacion(@PathVariable Long idPublicacion) {
        return foroService.listarComentariosPorPublicacion(idPublicacion);
    }

    @PostMapping("/comentarios")
    public Comentario crearComentario(@Valid @RequestBody Comentario comentario) {
        return foroService.crearComentario(comentario);
    }

    @PutMapping("/comentarios/{id}")
    public ResponseEntity<Comentario> actualizarComentario(@PathVariable Long id, @Valid @RequestBody Comentario comentario) {
        Comentario actualizado = foroService.actualizarComentario(id, comentario);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @PutMapping("/comentarios/{id}/estado")
    public ResponseEntity<Comentario> moderarComentario(@PathVariable Long id, @RequestParam String estado) {
        Comentario moderado = foroService.cambiarEstadoComentario(id, estado);
        return moderado != null ? ResponseEntity.ok(moderado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long id) {
        foroService.eliminarComentario(id);
        return ResponseEntity.noContent().build();
    }
}