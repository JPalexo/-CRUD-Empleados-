import { Component, computed } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { SessionService } from './core/session.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly currentYear = new Date().getFullYear();
  protected readonly adminCredentials = computed(() => this.session.adminCredentials());
  protected readonly employeeIdentity = computed(() => this.session.employeeIdentity());

  constructor(private readonly session: SessionService) {}

  protected clearAdminSession(): void {
    this.session.clearAdminCredentials();
  }

  protected clearEmployeeSession(): void {
    this.session.clearEmployeeIdentity();
  }
}
