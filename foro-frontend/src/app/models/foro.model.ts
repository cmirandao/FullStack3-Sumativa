export interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion: string;
  icono: string;
}

export interface Comentario {
  idComentario: number;
  idPublicacion: number;
  autor: string;
  contenido: string;
  fechaCreacion: string;
  estado: 'ACTIVO' | 'BANEADO';
}

export interface Publicacion {
  idPublicacion: number;
  idCategoria: number;
  titulo: string;
  contenido: string;
  autor: string;
  fechaCreacion: string;
  estado: 'ACTIVO' | 'BANEADO';
  comentarios: Comentario[];
}