export type RolUsuario = 'ADMIN' | 'MODERADOR' | 'USUARIO';

export interface Usuario {
  idUsuario: number;
  username: string;
  email: string;
  password: string;
  nombreCompleto: string;
  rol: RolUsuario;
  activo: boolean;
}