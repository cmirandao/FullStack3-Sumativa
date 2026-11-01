package com.duoc.usuarios.service;

import com.duoc.usuarios.model.Rol;
import com.duoc.usuarios.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    // Roles
    List<Rol> listarRoles();
    Rol crearRol(Rol rol);

    // Usuarios
    List<Usuario> listarUsuarios();
    Optional<Usuario> obtenerPorId(Long id);
    List<Usuario> listarPorRol(Long idRol);
    Usuario crearUsuario(Usuario usuario);
    Usuario actualizarUsuario(Long id, Usuario usuario);
    void eliminarUsuario(Long id);
    Optional<Usuario> login(String username, String password);
}