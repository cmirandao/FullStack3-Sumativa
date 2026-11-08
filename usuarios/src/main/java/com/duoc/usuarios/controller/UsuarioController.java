package com.duoc.usuarios.controller;

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
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERADOR')")
    @GetMapping("/rol/{idRol}")
    public List<Usuario> obtenerUsuariosPorRol(@PathVariable Long idRol) {
        return usuarioService.listarPorRol(idRol);
    }

    // Registro abierto (o restringido a admin según prefieras, aquí abierto para nuevos registros)
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
    public ResponseEntity<Object> iniciarSesion(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.login(request.getUsername(), request.getPassword());
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Inicio de sesión exitoso");
            respuesta.put("idUsuario", u.getIdUsuario());
            respuesta.put("username", u.getUsername());
            respuesta.put("email", u.getEmail());
            respuesta.put("rol", u.getRol().getNombre());
            return ResponseEntity.ok(respuesta);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas o usuario inactivo");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}