import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { reglaCaracterEspecial, reglaLetraNumero } from '../registro/registro.component';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil.component.html'
})
export class PerfilComponent implements OnInit {
  private fb = inject(FormBuilder);
  authService = inject(AuthService);

  mensaje = '';
  esError = false;

  form = this.fb.nonNullable.group({
    nombreCompleto: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email]],
    nuevaPassword: [
      '',
      [
        Validators.minLength(8),
        Validators.maxLength(24),
        reglaLetraNumero,
        reglaCaracterEspecial
      ]
    ]
  });

  ngOnInit(): void {
    const actual = this.authService.usuarioActual;
    if (actual) {
      this.form.patchValue({
        nombreCompleto: actual.nombreCompleto,
        email: actual.email
      });
    }
  }

  get pw() {
    return this.form.controls.nuevaPassword;
  }

  onSubmit(): void {
    this.mensaje = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { nombreCompleto, email, nuevaPassword } = this.form.getRawValue();
    const res = this.authService.actualizarPerfil({
      nombreCompleto: nombreCompleto.trim(),
      email: email.trim(),
      password: nuevaPassword ? nuevaPassword : undefined
    });

    this.esError = !res.exito;
    this.mensaje = res.mensaje;
    if (res.exito) {
      this.form.controls.nuevaPassword.reset('');
    }
  }
}