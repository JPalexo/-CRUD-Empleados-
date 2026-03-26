import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthSessionService } from './services/auth-session.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  template: `
    <header class="topbar">
      <div class="brand">Admin Empleados</div>
      <nav>
        <a routerLink="/login">Login</a>
        <a routerLink="/empleados">Empleados</a>
      </nav>
      <button type="button" (click)="logout()" [disabled]="!auth.isAuthenticated()">Salir</button>
    </header>

    <main class="page-shell">
      <router-outlet></router-outlet>
    </main>
  `
})
export class AppComponent {
  constructor(public readonly auth: AuthSessionService) {}

  logout(): void {
    this.auth.logout();
  }
}
