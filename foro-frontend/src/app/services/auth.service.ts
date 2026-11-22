import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { RolUsuario, Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root' // Patrón Singleton
})
export class AuthService {
  private readonly STORAGE_KEY = 'foro_usuario_sesion';

  // Lista estática en memoria
  private usuarios: Usuario[] = [
    {
      idUsuario: 1,
      username: 'cmiranda_admin',
      email: 'carolina@duoc.cl',
      password: 'Admin123*',
      nombreCompleto: 'Carolina Miranda',
      rol: 'ADMIN',
      activo: true
    },
    {
      idUsuario: 2,
      username: 'sofia_dev',
      email: 'sofia@correo.cl',
      password: 'User123*',
      nombreCompleto: 'Sofía Dev',
      rol: 'USUARIO',
      activo: true
    },
    {
      idUsuario: 3,
      username: 'victoria_mod',
      email: 'victoria@correo.cl',
      password: 'Mod123*',
      nombreCompleto: 'Victoria Mod',
      rol: 'MODERADOR',
      activo: true
    }
  ];

  // Patrón Observer con BehaviorSubject
  private usuarioActualSubject = new BehaviorSubject<Usuario | null>(this.cargarSesion());
  usuarioActual$: Observable<Usuario | null> = this.usuarioActualSubject.asObservable();

  private cargarSesion(): Usuario | null {
    const data = localStorage.getItem(this.STORAGE_KEY);
    return data ? JSON.parse(data) : null;
  }

  get usuarioActual(): Usuario | null {
    return this.usuarioActualSubject.value;
  }

  estaAutenticado(): boolean {
    return this.usuarioActualSubject.value !== null;
  }

  tieneRol(rolesPermitidos: RolUsuario[]): boolean {
    const user = this.usuarioActualSubject.value;
    return user !== null && rolesPermitidos.includes(user.rol);
  }

  login(identificador: string, password: string): { exito: boolean; mensaje: string } {
    const user = this.usuarios.find(
      u => (u.username === identificador || u.email === identificador) && u.password === password && u.activo
    );

    if (!user) {
      return { exito: false, mensaje: 'Credenciales inválidas o usuario inactivo.' };
    }

    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
    this.usuarioActualSubject.next(user);
    return { exito: true, mensaje: 'Inicio de sesión exitoso.' };
  }

  registrar(nuevo: Omit<Usuario, 'idUsuario' | 'activo'>): { exito: boolean; mensaje: string } {
    const existe = this.usuarios.some(u => u.username === nuevo.username || u.email === nuevo.email);
    if (existe) {
      return { exito: false, mensaje: 'El username o correo ya está registrado.' };
    }

    const creado: Usuario = {
      ...nuevo,
      idUsuario: this.usuarios.length + 1,
      activo: true
    };
    this.usuarios.push(creado);
    return { exito: true, mensaje: 'Usuario registrado correctamente. Ya puedes iniciar sesión.' };
  }

  actualizarPerfil(datos: { nombreCompleto: string; email: string; password?: string }): { exito: boolean; mensaje: string } {
    const actual = this.usuarioActualSubject.value;
    if (!actual) return { exito: false, mensaje: 'No hay sesión activa.' };

    const index = this.usuarios.findIndex(u => u.idUsuario === actual.idUsuario);
    if (index === -1) return { exito: false, mensaje: 'Usuario no encontrado.' };

    const actualizado: Usuario = {
      ...this.usuarios[index],
      nombreCompleto: datos.nombreCompleto,
      email: datos.email,
      password: datos.password ? datos.password : this.usuarios[index].password
    };

    this.usuarios[index] = actualizado;
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(actualizado));
    this.usuarioActualSubject.next(actualizado);
    return { exito: true, mensaje: 'Perfil actualizado correctamente.' };
  }

  recuperarPassword(email: string): { exito: boolean; mensaje: string } {
    const existe = this.usuarios.some(u => u.email === email);
    if (!existe) {
      return { exito: false, mensaje: 'No existe una cuenta asociada a ese correo.' };
    }
    return { exito: true, mensaje: 'Se han enviado las instrucciones de recuperación a tu correo.' };
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.usuarioActualSubject.next(null);
  }
}