import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginPageComponent } from './pages/login/login-page.component';
import { EmpleadosListPageComponent } from './pages/empleados/empleados-list-page.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'empleados' },
  { path: 'login', component: LoginPageComponent },
  { path: 'empleados', component: EmpleadosListPageComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'empleados' }
];
