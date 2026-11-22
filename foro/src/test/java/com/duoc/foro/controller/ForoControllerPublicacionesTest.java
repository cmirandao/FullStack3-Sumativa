package com.duoc.foro.controller;

import com.duoc.foro.config.SecurityConfig;
import com.duoc.foro.model.Categoria;
import com.duoc.foro.model.Comentario;
import com.duoc.foro.model.Publicacion;
import com.duoc.foro.service.ForoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForoController.class)
@Import(SecurityConfig.class)
class ForoControllerPublicacionesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForoService foroService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("General");
        categoria.setDescripcion("Categoria general");
        return categoria;
    }

    private Publicacion publicacionValida() {
        Publicacion publicacion = new Publicacion();
        publicacion.setIdPublicacion(10L);
        publicacion.setTitulo("Titulo valido");
        publicacion.setContenido("Contenido de prueba");
        publicacion.setEstado("ACTIVO");
        publicacion.setIdUsuario(99L);
        publicacion.setCategoria(categoria());
        return publicacion;
    }

    private Comentario comentarioValido() {
        Comentario comentario = new Comentario();
        comentario.setIdComentario(7L);
        comentario.setContenido("Buen aporte");
        comentario.setEstado("ACTIVO");
        comentario.setIdPublicacion(10L);
        comentario.setIdUsuario(99L);
        return comentario;
    }

    @Test
    void obtenerPublicacionesRetornaLista() throws Exception {
        given(foroService.listarPublicaciones()).willReturn(List.of(publicacionValida()));

        mockMvc.perform(get("/api/foro/publicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Titulo valido"));
    }

    @Test
    void obtenerPublicacionPorIdExistenteRetornaOk() throws Exception {
        given(foroService.obtenerPublicacionPorId(10L)).willReturn(Optional.of(publicacionValida()));

        mockMvc.perform(get("/api/foro/publicaciones/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPublicacion").value(10));
    }

    @Test
    void obtenerPublicacionPorIdInexistenteRetornaNotFound() throws Exception {
        given(foroService.obtenerPublicacionPorId(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/foro/publicaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPublicacionesPorCategoriaYTituloRetornaResultados() throws Exception {
        given(foroService.buscarPorCategoria(1L)).willReturn(List.of(publicacionValida()));
        given(foroService.buscarPorTitulo("Titulo")).willReturn(List.of(publicacionValida()));

        mockMvc.perform(get("/api/foro/publicaciones/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPublicacion").value(10));

        mockMvc.perform(get("/api/foro/publicaciones/buscar").param("q", "Titulo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Titulo valido"));
    }

    @Test
    @WithMockUser(authorities = "USUARIO")
    void crearPublicacionComoUsuarioRetornaOk() throws Exception {
        given(foroService.crearPublicacion(any(Publicacion.class))).willReturn(publicacionValida());

        mockMvc.perform(post("/api/foro/publicaciones")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(publicacionValida()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Titulo valido"));
    }

    @Test
    @WithMockUser(authorities = "USUARIO")
    void actualizarPublicacionExistenteRetornaOk() throws Exception {
        given(foroService.actualizarPublicacion(eq(10L), any(Publicacion.class))).willReturn(publicacionValida());

        mockMvc.perform(put("/api/foro/publicaciones/10")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(publicacionValida()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPublicacion").value(10));
    }

    @Test
    @WithMockUser(authorities = "USUARIO")
    void actualizarPublicacionInexistenteRetornaNotFound() throws Exception {
        given(foroService.actualizarPublicacion(eq(99L), any(Publicacion.class))).willReturn(null);

        mockMvc.perform(put("/api/foro/publicaciones/99")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(publicacionValida()))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "MODERADOR")
    void moderarPublicacionExistenteRetornaApiResponse() throws Exception {
        Publicacion moderada = publicacionValida();
        moderada.setEstado("BANEADO");
        given(foroService.cambiarEstadoPublicacion(10L, "BANEADO")).willReturn(moderada);

        mockMvc.perform(put("/api/foro/publicaciones/10/estado").param("estado", "BANEADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.estado").value("BANEADO"));
    }

    @Test
    @WithMockUser(authorities = "MODERADOR")
    void moderarPublicacionInexistenteRetornaNotFoundConMensaje() throws Exception {
        given(foroService.cambiarEstadoPublicacion(99L, "BANEADO")).willReturn(null);

        mockMvc.perform(put("/api/foro/publicaciones/99/estado").param("estado", "BANEADO"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Publicación no encontrada"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void eliminarPublicacionRetornaNoContent() throws Exception {
        mockMvc.perform(delete("/api/foro/publicaciones/10"))
                .andExpect(status().isNoContent());

        verify(foroService).eliminarPublicacion(10L);
    }

    @Test
    void obtenerComentariosPorPublicacionRetornaLista() throws Exception {
        given(foroService.listarComentariosPorPublicacion(10L)).willReturn(List.of(comentarioValido()));

        mockMvc.perform(get("/api/foro/comentarios/publicacion/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contenido").value("Buen aporte"));
    }

    @Test
    @WithMockUser(authorities = "USUARIO")
    void crearYActualizarComentarioRetornaOk() throws Exception {
        given(foroService.crearComentario(any(Comentario.class))).willReturn(comentarioValido());
        given(foroService.actualizarComentario(eq(7L), any(Comentario.class))).willReturn(comentarioValido());
        given(foroService.actualizarComentario(eq(77L), any(Comentario.class))).willReturn(null);

        mockMvc.perform(post("/api/foro/comentarios")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(comentarioValido()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value("Buen aporte"));

        mockMvc.perform(put("/api/foro/comentarios/7")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(comentarioValido()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idComentario").value(7));

        mockMvc.perform(put("/api/foro/comentarios/77")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(comentarioValido()))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "MODERADOR")
    void moderarYEliminarComentarioRetornaResultadosEsperados() throws Exception {
        Comentario moderado = comentarioValido();
        moderado.setEstado("OCULTO");
        given(foroService.cambiarEstadoComentario(7L, "OCULTO")).willReturn(moderado);
        given(foroService.cambiarEstadoComentario(77L, "OCULTO")).willReturn(null);

        mockMvc.perform(put("/api/foro/comentarios/7/estado").param("estado", "OCULTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("OCULTO"));

        mockMvc.perform(put("/api/foro/comentarios/77/estado").param("estado", "OCULTO"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/foro/comentarios/7"))
                .andExpect(status().isNoContent());

        verify(foroService).eliminarComentario(7L);
    }
}
