package com.duoc.usuarios.controller;

import com.duoc.usuarios.config.SecurityConfig;
import com.duoc.usuarios.dto.LoginRequest;
import com.duoc.usuarios.model.Rol;
import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Rol rol(String nombre) {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre(nombre);
        rol.setDescripcion("Descripcion " + nombre);
        return rol;
    }

    private Usuario usuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(10L);
        usuario.setUsername("carolina");
        usuario.setEmail("caro@example.com");
        usuario.setPassword("secreto1");
        usuario.setActivo(1);
        usuario.setRol(rol("USUARIO"));
        return usuario;
    }

    private String usuarioJsonConPassword(Usuario usuario) {
        return """
                {
                  "idUsuario": %s,
                  "username": "%s",
                  "email": "%s",
                  "password": "%s",
                  "activo": %d,
                  "rol": {
                    "idRol": %d,
                    "nombre": "%s",
                    "descripcion": "%s"
                  }
                }
                """.formatted(
                usuario.getIdUsuario() == null ? "null" : usuario.getIdUsuario().toString(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getActivo(),
                usuario.getRol().getIdRol(),
                usuario.getRol().getNombre(),
                usuario.getRol().getDescripcion()
        );
    }

    @Test
    void obtenerRolesRetornaLista() throws Exception {
        given(usuarioService.listarRoles()).willReturn(List.of(rol("ADMIN")));

        mockMvc.perform(get("/api/usuarios/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void crearRolComoAdminRetornaRol() throws Exception {
        Rol nuevo = rol("MODERADOR");
        given(usuarioService.crearRol(any(Rol.class))).willReturn(nuevo);

        mockMvc.perform(post("/api/usuarios/roles")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(nuevo))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("MODERADOR"));
    }

    @Test
    @WithMockUser(authorities = "USUARIO")
    void crearRolSinPermisoRetornaForbidden() throws Exception {
        mockMvc.perform(post("/api/usuarios/roles")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(rol("MODERADOR")))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MODERADOR")
    void obtenerUsuariosComoModeradorRetornaApiResponse() throws Exception {
        given(usuarioService.listarUsuarios()).willReturn(List.of(usuarioValido()));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos[0].username").value("carolina"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void obtenerUsuarioPorIdExistenteRetornaApiResponse() throws Exception {
        given(usuarioService.obtenerPorId(10L)).willReturn(Optional.of(usuarioValido()));

        mockMvc.perform(get("/api/usuarios/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.username").value("carolina"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void obtenerUsuarioPorIdInexistenteRetornaNotFound() throws Exception {
        given(usuarioService.obtenerPorId(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void obtenerUsuariosPorRolRetornaLista() throws Exception {
        given(usuarioService.listarPorRol(1L)).willReturn(List.of(usuarioValido()));

        mockMvc.perform(get("/api/usuarios/rol/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos[0].idUsuario").value(10));
    }

    @Test
    void crearUsuarioRetornaUsuario() throws Exception {
        Usuario nuevo = usuarioValido();
        nuevo.setIdUsuario(null);
        given(usuarioService.crearUsuario(any(Usuario.class))).willReturn(usuarioValido());

        mockMvc.perform(post("/api/usuarios")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(usuarioJsonConPassword(nuevo))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("carolina"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void actualizarUsuarioExistenteRetornaOk() throws Exception {
        Usuario actualizado = usuarioValido();
        actualizado.setEmail("nuevo@example.com");
        given(usuarioService.actualizarUsuario(eq(10L), any(Usuario.class))).willReturn(actualizado);

        mockMvc.perform(put("/api/usuarios/10")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(usuarioJsonConPassword(actualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void actualizarUsuarioInexistenteRetornaNotFound() throws Exception {
        given(usuarioService.actualizarUsuario(eq(99L), any(Usuario.class))).willReturn(null);

        mockMvc.perform(put("/api/usuarios/99")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(usuarioJsonConPassword(usuarioValido()))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void eliminarUsuarioComoAdminRetornaNoContent() throws Exception {
        mockMvc.perform(delete("/api/usuarios/10"))
                .andExpect(status().isNoContent());

        verify(usuarioService).eliminarUsuario(10L);
    }

    @Test
    @WithMockUser(authorities = "MODERADOR")
    void eliminarUsuarioComoModeradorRetornaForbidden() throws Exception {
        mockMvc.perform(delete("/api/usuarios/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginExitosoRetornaDatosUsuario() throws Exception {
        given(usuarioService.login("carolina", "secreto1")).willReturn(Optional.of(usuarioValido()));
        LoginRequest request = new LoginRequest();
        request.setUsername("carolina");
        request.setPassword("secreto1");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.datos.username").value("carolina"))
                .andExpect(jsonPath("$.datos.rol").value("USUARIO"));
    }

    @Test
    void loginFallidoRetornaUnauthorized() throws Exception {
        given(usuarioService.login("carolina", "mala")).willReturn(Optional.empty());
        LoginRequest request = new LoginRequest();
        request.setUsername("carolina");
        request.setPassword("mala");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas o usuario inactivo"));
    }
}
