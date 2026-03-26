import { Component, EventEmitter, Input, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Empleado, EmpleadoCreateRequest, EmpleadoUpdateRequest } from '../../models/empleado.model';

type EmpleadoFormPayload =
  | { mode: 'create'; payload: EmpleadoCreateRequest }
  | { mode: 'edit'; payload: EmpleadoUpdateRequest };

@Component({
  selector: 'app-empleado-form',
  imports: [ReactiveFormsModule],
  templateUrl: './empleado-form.component.html'
})
export class EmpleadoFormComponent {
  private readonly fb = inject(FormBuilder);

  @Input() empleado: Empleado | null = null;
  @Output() submitted = new EventEmitter<EmpleadoFormPayload>();
  @Output() cancelled = new EventEmitter<void>();

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern('^(?=.*[A-Za-z])(?=.*\\d).+$')]],
    departamentoClave: ['', [Validators.required, Validators.pattern('^DEP-[1-9][0-9]*$')]]
  });
  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['empleado']) {
      return;
    }

    if (!this.empleado) {
      this.form.reset({
        nombre: '',
        direccion: '',
        telefono: '',
        email: '',
        password: '',
        departamentoClave: ''
      });
      this.form.controls.password.addValidators([Validators.required]);
      this.form.controls.password.enable();
      return;
    }

    this.form.patchValue({
      nombre: this.empleado.nombre,
      direccion: this.empleado.direccion,
      telefono: this.empleado.telefono,
      email: '',
      password: '',
      departamentoClave: this.empleado.departamentoClave
    });

    this.form.controls.password.clearValidators();
    this.form.controls.password.disable();
    this.form.controls.password.updateValueAndValidity();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();

    if (this.empleado) {
      const updatePayload: EmpleadoUpdateRequest = {
        nombre: value.nombre,
        direccion: value.direccion,
        telefono: value.telefono,
        departamentoClave: value.departamentoClave
      };
      this.submitted.emit({ mode: 'edit', payload: updatePayload });
      return;
    }

    const createPayload: EmpleadoCreateRequest = {
      nombre: value.nombre,
      direccion: value.direccion,
      telefono: value.telefono,
      email: value.email,
      password: value.password,
      departamentoClave: value.departamentoClave
    };
    this.submitted.emit({ mode: 'create', payload: createPayload });
  }

  cancel(): void {
    this.cancelled.emit();
  }
}
