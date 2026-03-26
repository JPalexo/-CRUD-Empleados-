import { Component } from '@angular/core';
import { Empleado } from '../../models/empleado.model';

@Component({
  selector: 'app-empleado-delete-dialog',
  template: ''
})
export class EmpleadoDeleteDialogComponent {
  confirmDelete(empleado: Empleado): boolean {
    return window.confirm(
      `Se eliminara el empleado ${empleado.nombre} (${empleado.clave}). Esta accion no se puede deshacer.`
    );
  }
}
