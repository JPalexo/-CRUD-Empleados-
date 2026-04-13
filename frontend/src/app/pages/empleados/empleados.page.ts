import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ApiClientService } from '../../core/api-client.service';
import { httpErrorMessage } from '../../core/http-error.util';
import { Departamento, Empleado } from '../../core/models';

@Component({
  selector: 'app-empleados-page',
  imports: [ReactiveFormsModule],
  templateUrl: './empleados.page.html',
  styleUrl: './empleados.page.css'
})
export class EmpleadosPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiClientService);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');

  protected readonly empleados = signal<Empleado[]>([]);
  protected readonly departamentos = signal<Departamento[]>([]);

  protected readonly page = signal(0);
  protected readonly size = signal(10);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);

  protected readonly editingClave = signal<string | null>(null);

  protected readonly createForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]],
    departamentoClave: ['', [Validators.required]]
  });

  protected readonly editForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.maxLength(100)]],
    departamentoClave: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.loadDepartamentos();
    this.loadEmpleados();
  }

  protected create(): void {
    if (this.createForm.invalid || this.loading()) {
      this.createForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .createEmpleado(this.createForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.createForm.reset({
            nombre: '',
            direccion: '',
            telefono: '',
            email: '',
            password: '',
            departamentoClave: ''
          });
          this.page.set(0);
          this.loadEmpleados();
          this.loadDepartamentos();
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo crear el empleado.'));
        }
      });
  }

  protected startEdit(empleado: Empleado): void {
    this.editingClave.set(empleado.clave);
    this.editForm.setValue({
      nombre: empleado.nombre,
      direccion: empleado.direccion,
      telefono: empleado.telefono,
      departamentoClave: empleado.departamentoClave
    });
  }

  protected cancelEdit(): void {
    this.editingClave.set(null);
    this.editForm.reset({
      nombre: '',
      direccion: '',
      telefono: '',
      departamentoClave: ''
    });
  }

  protected saveEdit(clave: string): void {
    if (this.editForm.invalid || this.loading()) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .updateEmpleado(clave, this.editForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.cancelEdit();
          this.loadEmpleados();
          this.loadDepartamentos();
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo actualizar el empleado.'));
        }
      });
  }

  protected remove(clave: string): void {
    if (this.loading() || !window.confirm(`Eliminar empleado ${clave}?`)) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .deleteEmpleado(clave)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.cancelEdit();
          this.loadEmpleados();
          this.loadDepartamentos();
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo eliminar el empleado.'));
        }
      });
  }

  protected previousPage(): void {
    if (this.page() === 0 || this.loading()) {
      return;
    }

    this.page.update((value) => value - 1);
    this.loadEmpleados();
  }

  protected nextPage(): void {
    if (this.loading() || this.page() + 1 >= this.totalPages()) {
      return;
    }

    this.page.update((value) => value + 1);
    this.loadEmpleados();
  }

  private loadEmpleados(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.api
      .listEmpleados(this.page(), this.size())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.empleados.set(response.data);
          this.totalPages.set(response.pagination.totalPages);
          this.totalElements.set(response.pagination.totalElements);
        },
        error: (error: unknown) => {
          this.errorMessage.set(httpErrorMessage(error, 'No se pudo cargar empleados.'));
        }
      });
  }

  private loadDepartamentos(): void {
    this.api.listDepartamentos(0, 100).subscribe({
      next: (response) => {
        this.departamentos.set(response.data);
      },
      error: (error: unknown) => {
        this.errorMessage.set(httpErrorMessage(error, 'No se pudo cargar departamentos para el formulario.'));
      }
    });
  }
}
