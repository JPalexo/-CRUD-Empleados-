import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';

interface SessionState {
  email: string;
  basicToken: string;
  lastActivityAt: number;
}

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private static readonly STORAGE_KEY = 'crud-empleados-session';
  private readonly state = signal<SessionState | null>(this.restore());

  isAuthenticated(): boolean {
    const current = this.state();
    if (!current) {
      return false;
    }
    if (Date.now() - current.lastActivityAt > environment.sessionIdleTimeoutMs) {
      this.logout();
      return false;
    }
    return true;
  }

  login(email: string, password: string): void {
    const token = btoa(`${email}:${password}`);
    const next: SessionState = {
      email,
      basicToken: token,
      lastActivityAt: Date.now()
    };
    this.state.set(next);
    localStorage.setItem(AuthSessionService.STORAGE_KEY, JSON.stringify(next));
  }

  logout(): void {
    this.state.set(null);
    localStorage.removeItem(AuthSessionService.STORAGE_KEY);
  }

  getAuthorizationHeader(): string | null {
    if (!this.isAuthenticated()) {
      return null;
    }
    return `Basic ${this.state()?.basicToken ?? ''}`;
  }

  touchActivity(): void {
    const current = this.state();
    if (!current) {
      return;
    }
    const updated: SessionState = { ...current, lastActivityAt: Date.now() };
    this.state.set(updated);
    localStorage.setItem(AuthSessionService.STORAGE_KEY, JSON.stringify(updated));
  }

  private restore(): SessionState | null {
    const raw = localStorage.getItem(AuthSessionService.STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as SessionState;
    } catch {
      return null;
    }
  }
}
