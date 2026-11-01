package com.duoc.usuarios.controller;

import com.duoc.usuarios.dto.LoginRequest;
import com.duoc.usuarios.model.Rol;
import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // --- ROLES ---
    @GetMapping("/roles")
    public List<Rol> obtenerRoles() {
        return usuarioService.listarRoles();
    }

    @PostMapping("/roles")
    public Rol crearRol(@Valid @RequestBody Rol rol) {
        return usuarioService.crearRol(rol);
    }

    // --- USUARIOS ---
    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{idRol}")
    public List<Usuario> obtenerUsuariosPorRol(@PathVariable Long idRol) {
        return usuarioService.listarPorRol(idRol);
    }

    @PostMapping
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- LOGIN ---
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