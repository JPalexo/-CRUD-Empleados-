import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ApiClientService } from '../../core/api-client.service';
import { httpErrorMessage } from '../../core/http-error.util';
import { EmpleadoLoginIdentity } from '../../core/models';
import { SessionService } from '../../core/session.service';

@Component({
  selector: 'app-employee-login-page',
  imports: [ReactiveFormsModule],
  templateUrl: './employee-login.page.html',
  styleUrl: './employee-login.page.css'
})
export class EmployeeLoginPage {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiClientService);
  private readonly session = inject(SessionService);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly identity = signal<EmpleadoLoginIdentity | null>(this.session.employeeIdentity());

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  protected submit(): void {
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .loginEmpleado(this.form.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.session.setEmployeeIdentity(response.empleado);
          this.identity.set(response.empleado);
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No fue posible autenticar al empleado.'));
        }
      });
  }

  protected clearIdentity(): void {
    this.session.clearEmployeeIdentity();
    this.identity.set(null);
  }
}
