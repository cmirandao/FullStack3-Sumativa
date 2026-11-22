import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  mensajeError = '';

  form = this.fb.nonNullable.group({
    identificador: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required]]
  });

  onSubmit(): void {
    this.mensajeError = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { identificador, password } = this.form.getRawValue();
    const res = this.authService.login(identificador.trim(), password);

    if (res.exito) {
      this.router.navigate(['/foro']);
    } else {
      this.mensajeError = res.mensaje;
    }
  }
}