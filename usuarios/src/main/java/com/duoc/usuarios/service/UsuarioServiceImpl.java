package com.duoc.usuarios.service;

import com.duoc.usuarios.model.Rol;
import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.repository.RolRepository;
import com.duoc.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final RolRepository rolRepository;

    UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public List<Rol> listarRoles() {
        log.info("Obteniendo listado de roles");
        return rolRepository.findAll();
    }

    @Override
    public Rol crearRol(Rol rol) {
        log.info("Creando nuevo rol: {}", rol.getNombre());
        return rolRepository.save(rol);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarPorRol(Long idRol) {
        log.info("Buscando usuarios con ID de rol: {}", idRol);
        return usuarioRepository.findByRolIdRol(idRol);
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        log.info("Registrando nuevo usuario: {}", usuario.getUsername());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        return usuarioRepository.findById(id).map(existente -> {
            log.info("Actualizando datos del usuario ID: {}", id);
            existente.setUsername(usuario.getUsername());
            existente.setEmail(usuario.getEmail());
            existente.setPassword(usuario.getPassword());
            existente.setActivo(usuario.getActivo());
            existente.setRol(usuario.getRol());
            return usuarioRepository.save(existente);
        }).orElse(null);
    }

    @Override
    public void eliminarUsuario(Long id) {
        log.warn("Eliminando usuario ID: {}", id);
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> login(String username, String password) {
        log.info("Intento de inicio de sesión para el usuario: {}", username);
        return usuarioRepository.findByUsernameAndPassword(username, password)
                .filter(u -> u.getActivo() == 1);
    }
}