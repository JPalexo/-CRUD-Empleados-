import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiError } from '../../models/api-error.model';
import { LoginResponse } from '../../models/empleado.model';
import { AuthSessionService } from '../../services/auth-session.service';
import { EmpleadosService } from '../../services/empleados.service';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule],
  templateUrl: './login-page.component.html'
})
export class LoginPageComponent {
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]]
  });

  constructor(
    private readonly service: EmpleadosService,
    private readonly auth: AuthSessionService,
    private readonly router: Router
  ) {}

  submit(): void {
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.service.login(this.form.getRawValue()).subscribe({
      next: (response: LoginResponse) => {
        if (!response.authenticated) {
          this.error.set('Credenciales invalidas.');
          this.loading.set(false);
          return;
        }

        this.auth.login(this.form.controls.email.value, this.form.controls.password.value);
        void this.router.navigate(['/empleados']);
        this.loading.set(false);
      },
      error: (err: ApiError) => {
        this.error.set(err.message || 'No se pudo iniciar sesion.');
        this.loading.set(false);
      }
    });
  }
}
