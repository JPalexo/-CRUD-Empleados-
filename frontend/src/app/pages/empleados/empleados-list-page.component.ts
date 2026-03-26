import { Component, signal } from '@angular/core';
import { ApiError } from '../../models/api-error.model';
import {
  Empleado,
  EmpleadoCreateRequest,
  EmpleadoPageResponse,
  EmpleadoUpdateRequest
} from '../../models/empleado.model';
import { EmpleadosService } from '../../services/empleados.service';
import { EmpleadoDeleteDialogComponent } from '../../components/empleados/empleado-delete-dialog.component';
import { EmpleadoFormComponent } from '../../components/empleados/empleado-form.component';

type EmpleadoFormSubmit =
  | { mode: 'create'; payload: EmpleadoCreateRequest }
  | { mode: 'edit'; payload: EmpleadoUpdateRequest };

@Component({
  selector: 'app-empleados-list-page',
  imports: [EmpleadoFormComponent],
  templateUrl: './empleados-list-page.component.html'
})
export class EmpleadosListPageComponent {
  readonly empleados = signal<Empleado[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly page = signal(0);
  readonly size = signal(10);
  readonly totalPages = signal(0);
  readonly selected = signal<Empleado | null>(null);

  private readonly deleteDialog = new EmpleadoDeleteDialogComponent();

  constructor(private readonly empleadosService: EmpleadosService) {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.empleadosService.list(this.page(), this.size()).subscribe({
      next: (response: EmpleadoPageResponse) => {
        this.empleados.set(response.data);
        this.totalPages.set(response.pagination.totalPages);
        this.loading.set(false);
      },
      error: (err: ApiError) => {
        this.error.set(err.message || 'No se pudo cargar la lista.');
        this.loading.set(false);
      }
    });
  }

  selectForEdit(empleado: Empleado): void {
    this.selected.set(empleado);
  }

  clearSelection(): void {
    this.selected.set(null);
  }

  onFormSubmit(event: EmpleadoFormSubmit): void {
    this.loading.set(true);
    this.error.set(null);

    if (event.mode === 'create') {
      this.empleadosService.create(event.payload).subscribe({
        next: () => {
          this.clearSelection();
          this.load();
        },
        error: (err: ApiError) => {
          this.error.set(err.message || 'No se pudo crear el empleado.');
          this.loading.set(false);
        }
      });
      return;
    }

    const current = this.selected();
    if (!current) {
      this.error.set('No hay empleado seleccionado para editar.');
      this.loading.set(false);
      return;
    }

    this.empleadosService.update(current.clave, event.payload).subscribe({
      next: () => {
        this.clearSelection();
        this.load();
      },
      error: (err: ApiError) => {
        this.error.set(err.message || 'No se pudo actualizar el empleado.');
        this.loading.set(false);
      }
    });
  }

  delete(empleado: Empleado): void {
    if (!this.deleteDialog.confirmDelete(empleado)) {
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.empleadosService.delete(empleado.clave).subscribe({
      next: () => this.load(),
      error: (err: ApiError) => {
        this.error.set(err.message || 'No se pudo eliminar el empleado.');
        this.loading.set(false);
      }
    });
  }

  prevPage(): void {
    if (this.page() === 0) {
      return;
    }
    this.page.set(this.page() - 1);
    this.load();
  }

  nextPage(): void {
    if (this.page() + 1 >= this.totalPages()) {
      return;
    }
    this.page.set(this.page() + 1);
    this.load();
  }
}
