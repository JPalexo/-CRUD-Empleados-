import { Injectable, computed, signal } from '@angular/core';
import { AdminCredentials, EmpleadoLoginIdentity } from './models';

const ADMIN_STORAGE_KEY = 'crud.admin.credentials';
const EMPLOYEE_STORAGE_KEY = 'crud.employee.identity';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly adminCredentialsSignal = signal<AdminCredentials | null>(this.readAdminCredentials());
  private readonly employeeIdentitySignal = signal<EmpleadoLoginIdentity | null>(this.readEmployeeIdentity());

  readonly adminCredentials = computed(() => this.adminCredentialsSignal());
  readonly employeeIdentity = computed(() => this.employeeIdentitySignal());
  readonly hasAdminAccess = computed(() => this.adminCredentialsSignal() !== null);

  setAdminCredentials(credentials: AdminCredentials): void {
    const normalized = {
      username: credentials.username.trim(),
      password: credentials.password
    };

    this.adminCredentialsSignal.set(normalized);
    localStorage.setItem(ADMIN_STORAGE_KEY, JSON.stringify(normalized));
  }

  clearAdminCredentials(): void {
    this.adminCredentialsSignal.set(null);
    localStorage.removeItem(ADMIN_STORAGE_KEY);
  }

  setEmployeeIdentity(identity: EmpleadoLoginIdentity): void {
    this.employeeIdentitySignal.set(identity);
    localStorage.setItem(EMPLOYEE_STORAGE_KEY, JSON.stringify(identity));
  }

  clearEmployeeIdentity(): void {
    this.employeeIdentitySignal.set(null);
    localStorage.removeItem(EMPLOYEE_STORAGE_KEY);
  }

  buildBasicAuthHeader(credentials?: AdminCredentials): string | null {
    const source = credentials ?? this.adminCredentialsSignal();
    if (!source) {
      return null;
    }

    const encoded = btoa(`${source.username}:${source.password}`);
    return `Basic ${encoded}`;
  }

  private readAdminCredentials(): AdminCredentials | null {
    const raw = localStorage.getItem(ADMIN_STORAGE_KEY);
    if (!raw) {
      return null;
    }

    try {
      const parsed = JSON.parse(raw) as AdminCredentials;
      if (!parsed.username || !parsed.password) {
        return null;
      }
      return parsed;
    } catch {
      return null;
    }
  }

  private readEmployeeIdentity(): EmpleadoLoginIdentity | null {
    const raw = localStorage.getItem(EMPLOYEE_STORAGE_KEY);
    if (!raw) {
      return null;
    }

    try {
      const parsed = JSON.parse(raw) as EmpleadoLoginIdentity;
      if (!parsed.clave || !parsed.email) {
        return null;
      }
      return parsed;
    } catch {
      return null;
    }
  }
}
