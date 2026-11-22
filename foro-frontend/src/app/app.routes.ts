import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { ForoComponent } from './pages/foro/foro.component';
import { LoginComponent } from './pages/login/login.component';
import { ModeracionComponent } from './pages/moderacion/moderacion.component';
import { PerfilComponent } from './pages/perfil/perfil.component';
import { RecuperarComponent } from './pages/recuperar/recuperar.component';
import { RegistroComponent } from './pages/registro/registro.component';

export const routes: Routes = [
  { path: '', redirectTo: 'foro', pathMatch: 'full' },
  { path: 'foro', component: ForoComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'recuperar', component: RecuperarComponent },
  { path: 'perfil', component: PerfilComponent, canActivate: [authGuard] },
  {
    path: 'moderacion',
    component: ModeracionComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'MODERADOR'] }
  },
  { path: '**', redirectTo: 'foro' }
];