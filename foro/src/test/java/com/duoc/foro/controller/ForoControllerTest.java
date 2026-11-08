package com.duoc.foro.controller;

import com.duoc.foro.model.Categoria;
import com.duoc.foro.service.ForoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForoController.class)
public class ForoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForoService foroService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testObtenerCategorias() throws Exception {
        Categoria cat1 = new Categoria();
        cat1.setIdCategoria(1L);
        cat1.setNombre("Arquitectura y Microservicios");
        cat1.setDescripcion("Pruebas de microservicios");

        given(foroService.listarCategorias()).willReturn(Arrays.asList(cat1));

        mockMvc.perform(get("/api/foro/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Arquitectura y Microservicios"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testCrearCategoriaComoAdmin() throws Exception {
        Categoria cat = new Categoria();
        cat.setIdCategoria(1L);
        cat.setNombre("DevOps");
        cat.setDescripcion("CI/CD y Contenedores");

        given(foroService.crearCategoria(any(Categoria.class))).willReturn(cat);

        mockMvc.perform(post("/api/foro/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("DevOps"));
    }

    @Test
    @WithMockUser(authorities = "USUARIO")
    public void testCrearCategoriaSinPermisoDenegado() throws Exception {
        Categoria cat = new Categoria();
        cat.setNombre("DevOps");

        mockMvc.perform(post("/api/foro/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isForbidden());
    }
}