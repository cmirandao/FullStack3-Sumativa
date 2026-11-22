import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Publicacion } from '../../models/foro.model';
import { AuthService } from '../../services/auth.service';
import { ForoService } from '../../services/foro.service';

@Component({
  selector: 'app-foro',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './foro.component.html',
  styleUrl: './foro.component.css'
})
export class ForoComponent implements OnInit {
  foroService = inject(ForoService);
  authService = inject(AuthService);
  private fb = inject(FormBuilder);

  categoriaSeleccionada: number | 'TODAS' = 'TODAS';
  textoBusqueda = '';
  publicaciones: Publicacion[] = [];

  // Almacenar el texto de comentario por idPublicacion
  textosComentario: Record<number, string> = {};
  errorComentario: Record<number, string> = {};

  formPublicacion = this.fb.nonNullable.group({
    idCategoria: [1, [Validators.required]],
    titulo: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(120)]],
    contenido: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]]
  });

  ngOnInit(): void {
    this.foroService.publicaciones$.subscribe(lista => {
      this.publicaciones = lista;
    });
  }

  get publicacionesFiltradas(): Publicacion[] {
    return this.publicaciones.filter(pub => {
      const coincideCategoria =
        this.categoriaSeleccionada === 'TODAS' || pub.idCategoria === this.categoriaSeleccionada;
      const q = this.textoBusqueda.trim().toLowerCase();
      const coincideTexto =
        !q ||
        pub.titulo.toLowerCase().includes(q) ||
        pub.contenido.toLowerCase().includes(q) ||
        pub.autor.toLowerCase().includes(q);
      return coincideCategoria && coincideTexto;
    });
  }

  seleccionarCategoria(id: number | 'TODAS'): void {
    this.categoriaSeleccionada = id;
  }

  nombreCategoria(idCategoria: number): string {
    return this.foroService.obtenerCategoriaPorId(idCategoria)?.nombre ?? 'General';
  }

  crearTema(): void {
    if (!this.authService.estaAutenticado() || !this.authService.usuarioActual) {
      return;
    }
    if (this.formPublicacion.invalid) {
      this.formPublicacion.markAllAsTouched();
      return;
    }

    const { idCategoria, titulo, contenido } = this.formPublicacion.getRawValue();
    this.foroService.crearPublicacion({
      idCategoria: Number(idCategoria),
      titulo,
      contenido,
      autor: this.authService.usuarioActual.username
    });

    this.formPublicacion.patchValue({ titulo: '', contenido: '' });
    this.formPublicacion.markAsUntouched();
  }

  publicarComentario(idPublicacion: number): void {
    const usuario = this.authService.usuarioActual;
    if (!usuario) return;

    const texto = (this.textosComentario[idPublicacion] || '').trim();
    if (texto.length < 3) {
      this.errorComentario[idPublicacion] = 'El comentario debe tener al menos 3 caracteres.';
      return;
    }

    this.errorComentario[idPublicacion] = '';
    this.foroService.agregarComentario(idPublicacion, usuario.username, texto);
    this.textosComentario[idPublicacion] = '';
  }
}