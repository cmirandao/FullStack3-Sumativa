import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Categoria, Comentario, Publicacion } from '../models/foro.model';

@Injectable({
  providedIn: 'root'
})
export class ForoService {
  readonly categorias: Categoria[] = [
    {
      idCategoria: 1,
      nombre: 'Arquitectura y Microservicios',
      descripcion: 'Spring Boot, diseño de servicios, patrones y escalabilidad.',
      icono: 'bi-diagram-3'
    },
    {
      idCategoria: 2,
      nombre: 'Frontend y UI/UX',
      descripcion: 'Angular, componentes, estilos oscuros sobrios y diseño adaptable.',
      icono: 'bi-window-stack'
    },
    {
      idCategoria: 3,
      nombre: 'Bases de Datos y Cloud',
      descripcion: 'Oracle Cloud, consultas SQL, wallets, Docker y despliegue.',
      icono: 'bi-database-gear'
    },
    {
      idCategoria: 4,
      nombre: 'Seguridad y Buenas Prácticas',
      descripcion: 'Control de roles, validaciones, autenticación y calidad de código.',
      icono: 'bi-shield-lock'
    },
    {
      idCategoria: 5,
      nombre: 'Comunidad y Dudas Generales',
      descripcion: 'Consultas de cursado, organización de proyectos y consejos.',
      icono: 'bi-chat-dots'
    }
  ];

  private publicacionesIniciales: Publicacion[] = [
    {
      idPublicacion: 101,
      idCategoria: 1,
      titulo: '¿Cómo estructurar respuestas consistentes en microservicios REST?',
      contenido: 'Compartamos enfoques limpios para estandarizar las respuestas JSON entre servicios sin repetir código.',
      autor: 'cmiranda_admin',
      fechaCreacion: '2026-11-20 18:30',
      estado: 'ACTIVO',
      comentarios: [
        {
          idComentario: 1,
          idPublicacion: 101,
          autor: 'sofia_dev',
          contenido: 'Usar una estructura envolvente con estado, mensaje y datos ayuda mucho a predecir la respuesta.',
          fechaCreacion: '2026-11-20 19:05',
          estado: 'ACTIVO'
        }
      ]
    },
    {
      idPublicacion: 102,
      idCategoria: 2,
      titulo: 'Paletas oscuras cómodas para sesiones largas de programación',
      contenido: 'Evitar blancos puros y acentos saturados reduce bastante el cansancio visual al revisar código o foros.',
      autor: 'sofia_dev',
      fechaCreacion: '2026-11-21 10:15',
      estado: 'ACTIVO',
      comentarios: []
    },
    {
      idPublicacion: 103,
      idCategoria: 5,
      titulo: 'Publicación de prueba marcada para moderación',
      contenido: 'Ejemplo de hilo con contenido fuera de contexto para demostrar el flujo de baneo y restauración.',
      autor: 'sofia_dev',
      fechaCreacion: '2026-11-21 16:40',
      estado: 'BANEADO',
      comentarios: [
        {
          idComentario: 2,
          idPublicacion: 103,
          autor: 'victoria_mod',
          contenido: 'Hilo ocultado/baneado por moderación como ejemplo de control de normas.',
          fechaCreacion: '2026-11-21 17:00',
          estado: 'ACTIVO'
        }
      ]
    }
  ];

  private publicacionesSubject = new BehaviorSubject<Publicacion[]>(this.publicacionesIniciales);
  publicaciones$: Observable<Publicacion[]> = this.publicacionesSubject.asObservable();

  get publicacionesActuales(): Publicacion[] {
    return this.publicacionesSubject.value;
  }

  obtenerCategoriaPorId(idCategoria: number): Categoria | undefined {
    return this.categorias.find(c => c.idCategoria === idCategoria);
  }

  crearPublicacion(datos: { idCategoria: number; titulo: string; contenido: string; autor: string }): void {
    const actual = this.publicacionesActuales;
    const maxId = actual.reduce((acc, p) => Math.max(acc, p.idPublicacion), 100);
    const nueva: Publicacion = {
      idPublicacion: maxId + 1,
      idCategoria: datos.idCategoria,
      titulo: datos.titulo.trim(),
      contenido: datos.contenido.trim(),
      autor: datos.autor,
      fechaCreacion: this.fechaActual(),
      estado: 'ACTIVO',
      comentarios: []
    };
    this.publicacionesSubject.next([nueva, ...actual]);
  }

  agregarComentario(idPublicacion: number, autor: string, contenido: string): void {
    const actualizadas = this.publicacionesActuales.map(pub => {
      if (pub.idPublicacion !== idPublicacion) return pub;
      const maxComId = pub.comentarios.reduce((acc, c) => Math.max(acc, c.idComentario), 0);
      const nuevoComentario: Comentario = {
        idComentario: maxComId + 1,
        idPublicacion,
        autor,
        contenido: contenido.trim(),
        fechaCreacion: this.fechaActual(),
        estado: 'ACTIVO'
      };
      return {
        ...pub,
        comentarios: [...pub.comentarios, nuevoComentario]
      };
    });
    this.publicacionesSubject.next(actualizadas);
  }

  alternarBanPublicacion(idPublicacion: number): void {
    const actualizadas = this.publicacionesActuales.map(pub => {
      if (pub.idPublicacion !== idPublicacion) return pub;
      return {
        ...pub,
        estado: pub.estado === 'ACTIVO' ? ('BANEADO' as const) : ('ACTIVO' as const)
      };
    });
    this.publicacionesSubject.next(actualizadas);
  }

  alternarBanComentario(idPublicacion: number, idComentario: number): void {
    const actualizadas = this.publicacionesActuales.map(pub => {
      if (pub.idPublicacion !== idPublicacion) return pub;
      const comentariosActualizados = pub.comentarios.map(com => {
        if (com.idComentario !== idComentario) return com;
        return {
          ...com,
          estado: com.estado === 'ACTIVO' ? ('BANEADO' as const) : ('ACTIVO' as const)
        };
      });
      return { ...pub, comentarios: comentariosActualizados };
    });
    this.publicacionesSubject.next(actualizadas);
  }

  private fechaActual(): string {
    const ahora = new Date();
    const fecha = ahora.toISOString().slice(0, 10);
    const hora = ahora.toTimeString().slice(0, 5);
    return `${fecha} ${hora}`;
  }
}