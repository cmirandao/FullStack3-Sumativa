package com.duoc.foro.service;

import com.duoc.foro.model.Categoria;
import com.duoc.foro.model.Comentario;
import com.duoc.foro.model.Publicacion;
import com.duoc.foro.repository.CategoriaRepository;
import com.duoc.foro.repository.ComentarioRepository;
import com.duoc.foro.repository.PublicacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class ForoServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private ForoServiceImpl foroService;

    @Test
    void listarCategoriasRetornaTodas() {
        Categoria categoria = new Categoria();
        categoria.setNombre("General");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> resultado = foroService.listarCategorias();

        assertEquals(1, resultado.size());
        assertEquals("General", resultado.get(0).getNombre());
        verify(categoriaRepository).findAll();
    }

    @Test
    void crearCategoriaGuardaYRetorna() {
        Categoria categoria = new Categoria();
        categoria.setNombre("DevOps");
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria resultado = foroService.crearCategoria(categoria);

        assertSame(categoria, resultado);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void listarPublicacionesRetornaTodas() {
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo("Hola mundo");
        when(publicacionRepository.findAll()).thenReturn(List.of(publicacion));

        List<Publicacion> resultado = foroService.listarPublicaciones();

        assertEquals(1, resultado.size());
        verify(publicacionRepository).findAll();
    }

    @Test
    void obtenerPublicacionPorIdRetornaOptional() {
        Publicacion publicacion = new Publicacion();
        publicacion.setIdPublicacion(10L);
        when(publicacionRepository.findById(10L)).thenReturn(Optional.of(publicacion));

        Optional<Publicacion> resultado = foroService.obtenerPublicacionPorId(10L);

        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getIdPublicacion());
    }

    @Test
    void buscarPorCategoriaYTituloDeleganEnRepositorio() {
        when(publicacionRepository.findByCategoriaIdCategoria(3L)).thenReturn(List.of(new Publicacion()));
        when(publicacionRepository.findByTituloContainingIgnoreCase("java")).thenReturn(List.of(new Publicacion(), new Publicacion()));

        assertEquals(1, foroService.buscarPorCategoria(3L).size());
        assertEquals(2, foroService.buscarPorTitulo("java").size());
        verify(publicacionRepository).findByCategoriaIdCategoria(3L);
        verify(publicacionRepository).findByTituloContainingIgnoreCase("java");
    }

    @Test
    void crearPublicacionGuarda() {
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo("Nueva");
        when(publicacionRepository.save(publicacion)).thenReturn(publicacion);

        assertSame(publicacion, foroService.crearPublicacion(publicacion));
        verify(publicacionRepository).save(publicacion);
    }

    @Test
    void actualizarPublicacionExistenteActualizaCampos() {
        Publicacion existente = new Publicacion();
        existente.setIdPublicacion(1L);
        existente.setTitulo("Viejo");
        existente.setContenido("Contenido viejo");
        existente.setEstado("ACTIVO");
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(2L);
        existente.setCategoria(categoria);

        Publicacion cambios = new Publicacion();
        cambios.setTitulo("Nuevo");
        cambios.setContenido("Contenido nuevo");
        cambios.setEstado("BANEADO");
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setIdCategoria(5L);
        cambios.setCategoria(nuevaCategoria);

        when(publicacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Publicacion resultado = foroService.actualizarPublicacion(1L, cambios);

        assertNotNull(resultado);
        assertEquals("Nuevo", resultado.getTitulo());
        assertEquals("Contenido nuevo", resultado.getContenido());
        assertEquals("BANEADO", resultado.getEstado());
        assertEquals(5L, resultado.getCategoria().getIdCategoria());
        verify(publicacionRepository).save(existente);
    }

    @Test
    void actualizarPublicacionInexistenteRetornaNull() {
        when(publicacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(foroService.actualizarPublicacion(99L, new Publicacion()));
        verify(publicacionRepository, never()).save(any());
    }

    @Test
    void cambiarEstadoPublicacionExistenteActualizaEstado() {
        Publicacion existente = new Publicacion();
        existente.setEstado("ACTIVO");
        when(publicacionRepository.findById(7L)).thenReturn(Optional.of(existente));
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Publicacion resultado = foroService.cambiarEstadoPublicacion(7L, "BANEADO");

        assertNotNull(resultado);
        assertEquals("BANEADO", resultado.getEstado());
    }

    @Test
    void cambiarEstadoPublicacionInexistenteRetornaNull() {
        when(publicacionRepository.findById(7L)).thenReturn(Optional.empty());

        assertNull(foroService.cambiarEstadoPublicacion(7L, "BANEADO"));
        verify(publicacionRepository, never()).save(any());
    }

    @Test
    void eliminarPublicacionDeleganEnRepositorio() {
        foroService.eliminarPublicacion(4L);

        verify(publicacionRepository).deleteById(4L);
    }

    @Test
    void listarComentariosPorPublicacionRetornaResultados() {
        Comentario comentario = new Comentario();
        comentario.setContenido("Hola");
        when(comentarioRepository.findByIdPublicacion(8L)).thenReturn(List.of(comentario));

        List<Comentario> resultado = foroService.listarComentariosPorPublicacion(8L);

        assertEquals(1, resultado.size());
        verify(comentarioRepository).findByIdPublicacion(8L);
    }

    @Test
    void crearComentarioGuarda() {
        Comentario comentario = new Comentario();
        comentario.setIdPublicacion(8L);
        when(comentarioRepository.save(comentario)).thenReturn(comentario);

        assertSame(comentario, foroService.crearComentario(comentario));
    }

    @Test
    void actualizarComentarioExistenteActualizaCampos() {
        Comentario existente = new Comentario();
        existente.setContenido("Viejo");
        existente.setEstado("ACTIVO");
        Comentario cambios = new Comentario();
        cambios.setContenido("Nuevo");
        cambios.setEstado("BANEADO");

        when(comentarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(inv -> inv.getArgument(0));

        Comentario resultado = foroService.actualizarComentario(2L, cambios);

        assertNotNull(resultado);
        assertEquals("Nuevo", resultado.getContenido());
        assertEquals("BANEADO", resultado.getEstado());
    }

    @Test
    void actualizarComentarioInexistenteRetornaNull() {
        when(comentarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertNull(foroService.actualizarComentario(2L, new Comentario()));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void cambiarEstadoComentarioExistenteActualizaEstado() {
        Comentario existente = new Comentario();
        existente.setEstado("ACTIVO");
        when(comentarioRepository.findById(3L)).thenReturn(Optional.of(existente));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(inv -> inv.getArgument(0));

        Comentario resultado = foroService.cambiarEstadoComentario(3L, "OCULTO");

        assertNotNull(resultado);
        assertEquals("OCULTO", resultado.getEstado());
    }

    @Test
    void cambiarEstadoComentarioInexistenteRetornaNull() {
        when(comentarioRepository.findById(3L)).thenReturn(Optional.empty());

        assertNull(foroService.cambiarEstadoComentario(3L, "OCULTO"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void eliminarComentarioDeleganEnRepositorio() {
        foroService.eliminarComentario(6L);

        verify(comentarioRepository).deleteById(6L);
    }
}
