import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { ApiClientService } from '../../core/api-client.service';
import { httpErrorMessage } from '../../core/http-error.util';
import { SessionService } from '../../core/session.service';

@Component({
  selector: 'app-admin-access-page',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-access.page.html',
  styleUrl: './admin-access.page.css'
})
export class AdminAccessPage {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiClientService);
  private readonly session = inject(SessionService);
  private readonly router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly successMessage = signal('');

  protected readonly form = this.fb.nonNullable.group({
    username: ['admin', [Validators.required]],
    password: ['admin123', [Validators.required]]
  });

  protected submit(): void {
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.loading.set(true);

    const { username, password } = this.form.getRawValue();

    this.api
      .verifyAdminCredentials(username, password)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.session.setAdminCredentials({ username, password });
          this.successMessage.set('Acceso administrativo validado.');
          this.router.navigate(['/empleados']);
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'Credenciales admin invalidas.'));
        }
      });
  }
}
