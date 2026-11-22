package com.duoc.usuarios.service;

import com.duoc.usuarios.model.Rol;
import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.repository.RolRepository;
import com.duoc.usuarios.repository.UsuarioRepository;
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
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void listarRolesRetornaTodos() {
        Rol rol = new Rol();
        rol.setNombre("ADMIN");
        when(rolRepository.findAll()).thenReturn(List.of(rol));

        List<Rol> resultado = usuarioService.listarRoles();

        assertEquals(1, resultado.size());
        assertEquals("ADMIN", resultado.get(0).getNombre());
    }

    @Test
    void crearRolGuardaYRetorna() {
        Rol rol = new Rol();
        rol.setNombre("MODERADOR");
        when(rolRepository.save(rol)).thenReturn(rol);

        assertSame(rol, usuarioService.crearRol(rol));
        verify(rolRepository).save(rol);
    }

    @Test
    void listarUsuariosYConsultasDeleganEnRepositorio() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByRolIdRol(2L)).thenReturn(List.of(usuario));

        assertEquals(1, usuarioService.listarUsuarios().size());
        assertTrue(usuarioService.obtenerPorId(1L).isPresent());
        assertEquals(1, usuarioService.listarPorRol(2L).size());
    }

    @Test
    void crearUsuarioGuarda() {
        Usuario usuario = new Usuario();
        usuario.setUsername("nuevo");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertSame(usuario, usuarioService.crearUsuario(usuario));
    }

    @Test
    void actualizarUsuarioExistenteActualizaCampos() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        Usuario existente = new Usuario();
        existente.setUsername("viejo");
        existente.setEmail("viejo@correo.com");
        existente.setPassword("123456");
        existente.setActivo(1);
        existente.setRol(rol);

        Rol nuevoRol = new Rol();
        nuevoRol.setIdRol(2L);
        Usuario cambios = new Usuario();
        cambios.setUsername("nuevo");
        cambios.setEmail("nuevo@correo.com");
        cambios.setPassword("654321");
        cambios.setActivo(0);
        cambios.setRol(nuevoRol);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.actualizarUsuario(10L, cambios);

        assertNotNull(resultado);
        assertEquals("nuevo", resultado.getUsername());
        assertEquals("nuevo@correo.com", resultado.getEmail());
        assertEquals("654321", resultado.getPassword());
        assertEquals(0, resultado.getActivo());
        assertEquals(2L, resultado.getRol().getIdRol());
    }

    @Test
    void actualizarUsuarioInexistenteRetornaNull() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.empty());

        assertNull(usuarioService.actualizarUsuario(10L, new Usuario()));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminarUsuarioDeleganEnRepositorio() {
        usuarioService.eliminarUsuario(5L);

        verify(usuarioRepository).deleteById(5L);
    }

    @Test
    void loginConCredencialesValidasYActivoRetornaUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsername("caro");
        usuario.setActivo(1);
        when(usuarioRepository.findByUsernameAndPassword("caro", "secreto")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.login("caro", "secreto");

        assertTrue(resultado.isPresent());
        assertEquals("caro", resultado.get().getUsername());
    }

    @Test
    void loginConUsuarioInactivoRetornaVacio() {
        Usuario usuario = new Usuario();
        usuario.setUsername("caro");
        usuario.setActivo(0);
        when(usuarioRepository.findByUsernameAndPassword("caro", "secreto")).thenReturn(Optional.of(usuario));

        assertTrue(usuarioService.login("caro", "secreto").isEmpty());
    }

    @Test
    void loginConCredencialesInvalidasRetornaVacio() {
        when(usuarioRepository.findByUsernameAndPassword("caro", "mal")).thenReturn(Optional.empty());

        assertTrue(usuarioService.login("caro", "mal").isEmpty());
    }
}
