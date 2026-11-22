import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { Publicacion } from '../../models/foro.model';
import { ForoService } from '../../services/foro.service';

@Component({
  selector: 'app-moderacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './moderacion.component.html'
})
export class ModeracionComponent implements OnInit {
  private foroService = inject(ForoService);
  publicaciones: Publicacion[] = [];

  ngOnInit(): void {
    this.foroService.publicaciones$.subscribe(lista => {
      this.publicaciones = lista;
    });
  }

  nombreCategoria(idCategoria: number): string {
    return this.foroService.obtenerCategoriaPorId(idCategoria)?.nombre ?? 'General';
  }

  toggleBanPublicacion(idPublicacion: number): void {
    this.foroService.alternarBanPublicacion(idPublicacion);
  }

  toggleBanComentario(idPublicacion: number, idComentario: number): void {
    this.foroService.alternarBanComentario(idPublicacion, idComentario);
  }
}