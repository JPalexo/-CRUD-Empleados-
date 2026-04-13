import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ApiClientService } from '../../core/api-client.service';
import { httpErrorMessage } from '../../core/http-error.util';
import { Departamento } from '../../core/models';

@Component({
  selector: 'app-departamentos-page',
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './departamentos.page.html',
  styleUrl: './departamentos.page.css'
})
export class DepartamentosPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiClientService);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly departamentos = signal<Departamento[]>([]);

  protected readonly page = signal(0);
  protected readonly size = signal(10);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);

  protected readonly editingClave = signal<string | null>(null);

  protected readonly createForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]]
  });

  protected readonly editForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]]
  });

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .listDepartamentos(this.page(), this.size())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.departamentos.set(response.data);
          this.totalPages.set(response.pagination.totalPages);
          this.totalElements.set(response.pagination.totalElements);
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo cargar departamentos.'));
        }
      });
  }

  protected create(): void {
    if (this.createForm.invalid || this.loading()) {
      this.createForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .createDepartamento(this.createForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.createForm.reset({ nombre: '' });
          this.page.set(0);
          this.load();
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo crear el departamento.'));
        }
      });
  }

  protected startEdit(departamento: Departamento): void {
    this.editingClave.set(departamento.clave);
    this.editForm.setValue({ nombre: departamento.nombre });
  }

  protected cancelEdit(): void {
    this.editingClave.set(null);
    this.editForm.reset({ nombre: '' });
  }

  protected saveEdit(clave: string): void {
    if (this.editForm.invalid || this.loading()) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .updateDepartamento(clave, this.editForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.cancelEdit();
          this.load();
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo actualizar el departamento.'));
        }
      });
  }

  protected remove(clave: string): void {
    if (this.loading() || !window.confirm(`Eliminar departamento ${clave}?`)) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .deleteDepartamento(clave)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.cancelEdit();
          this.load();
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo eliminar el departamento.'));
        }
      });
  }

  protected previousPage(): void {
    if (this.page() === 0 || this.loading()) {
      return;
    }

    this.page.update((value) => value - 1);
    this.load();
  }

  protected nextPage(): void {
    if (this.loading() || this.page() + 1 >= this.totalPages()) {
      return;
    }

    this.page.update((value) => value + 1);
    this.load();
  }
}
