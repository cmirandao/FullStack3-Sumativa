import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RolUsuario } from '../../models/usuario.model';
import { AuthService } from '../../services/auth.service';

export function reglaLetraNumero(control: AbstractControl): ValidationErrors | null {
  const val = String(control.value || '');
  const tieneLetra = /[A-Za-z]/.test(val);
  const tieneNumero = /\d/.test(val);
  return tieneLetra && tieneNumero ? null : { letraNumero: true };
}

export function reglaCaracterEspecial(control: AbstractControl): ValidationErrors | null {
  const val = String(control.value || '');
  return /[^A-Za-z0-9]/.test(val) ? null : { caracterEspecial: true };
}

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registro.component.html'
})
export class RegistroComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  mensajeEstado = '';
  esError = false;

//   Reglas:
// - mínimo 8 caracteres
// - máximo 24 caracteres
// - debe contener letras y números
// - debe contener al menos un carácter especial

  form = this.fb.nonNullable.group({
    nombreCompleto: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(80)]],
    username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(25), Validators.pattern(/^[a-zA-Z0-9_]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    rol: ['USUARIO' as RolUsuario, [Validators.required]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(24),
        reglaLetraNumero,
        reglaCaracterEspecial
      ]
    ]
  });

  get pw() {
    return this.form.controls.password;
  }

  onSubmit(): void {
    this.mensajeEstado = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const res = this.authService.registrar(this.form.getRawValue());
    this.esError = !res.exito;
    this.mensajeEstado = res.mensaje;

    if (res.exito) {
      setTimeout(() => this.router.navigate(['/login']), 1000);
    }
  }
}