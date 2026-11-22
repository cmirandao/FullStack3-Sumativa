package com.duoc.usuarios.controller;

import com.duoc.usuarios.dto.ApiResponse;
import com.duoc.usuarios.dto.LoginRequest;
import com.duoc.usuarios.model.Rol;
import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- ROLES (Solo Administradores pueden gestionar roles) ---
    @GetMapping("/roles")
    public List<Rol> obtenerRoles() {
        return usuarioService.listarRoles();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/roles")
    public Rol crearRol(@Valid @RequestBody Rol rol) {
        return usuarioService.crearRol(rol);
    }

    // --- USUARIOS ---

    // Listar usuarios permitido para Administradores y Moderadores
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERADOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> obtenerUsuarios() {
        List<Usuario> lista = usuarioService.listarUsuarios();
        return ResponseEntity.ok(
                new ApiResponse.Builder<List<Usuario>>()
                        .exito(true)
                        .mensaje("Listado de usuarios obtenido correctamente")
                        .datos(lista)
                        .build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(usuario -> ResponseEntity.ok(
                    new ApiResponse.Builder<Usuario>()
                        .exito(true)
                        .mensaje("Usuario obtenido correctamente")
                        .datos(usuario)
                        .build()
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERADOR')")
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<ApiResponse<List<Usuario>>> obtenerUsuariosPorRol(@PathVariable Long idRol) {
        List<Usuario> lista = usuarioService.listarPorRol(idRol);
        return ResponseEntity.ok(
                new ApiResponse.Builder<List<Usuario>>()
                        .exito(true)
                        .mensaje("Usuarios por rol obtenidos correctamente")
                        .datos(lista)
                        .build());
    }

    @PostMapping
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    // Solo el ADMIN puede eliminar usuarios
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- LOGIN (Libre para autenticarse) ---
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> iniciarSesion(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.login(request.getUsername(), request.getPassword());

        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            Map<String, Object> datosUsuario = new HashMap<>();
            datosUsuario.put("idUsuario", u.getIdUsuario());
            datosUsuario.put("username", u.getUsername());
            datosUsuario.put("email", u.getEmail());
            datosUsuario.put("rol", u.getRol().getNombre());

            ApiResponse<Object> respuesta = new ApiResponse.Builder<Object>()
                    .exito(true)
                    .mensaje("Inicio de sesión exitoso")
                    .datos(datosUsuario)
                    .build();

            return ResponseEntity.ok(respuesta);
        } else {
            ApiResponse<Object> error = new ApiResponse.Builder<Object>()
                    .exito(false)
                    .mensaje("Credenciales inválidas o usuario inactivo")
                    .datos(null)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}