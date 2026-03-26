import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiClientService } from './api-client.service';
import {
  Empleado,
  EmpleadoCreateRequest,
  EmpleadoPageResponse,
  EmpleadoUpdateRequest,
  LoginRequest,
  LoginResponse
} from '../models/empleado.model';

@Injectable({ providedIn: 'root' })
export class EmpleadosService {
  constructor(private readonly api: ApiClientService) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.api.post<LoginRequest, LoginResponse>('/empleados/login', payload);
  }

  list(page = 0, size = 10): Observable<EmpleadoPageResponse> {
    return this.api.get<EmpleadoPageResponse>('/empleados', { page, size });
  }

  create(payload: EmpleadoCreateRequest): Observable<Empleado> {
    return this.api.post<EmpleadoCreateRequest, Empleado>('/empleados', payload);
  }

  update(clave: string, payload: EmpleadoUpdateRequest): Observable<Empleado> {
    return this.api.put<EmpleadoUpdateRequest, Empleado>(`/empleados/${clave}`, payload);
  }

  delete(clave: string): Observable<void> {
    return this.api.delete(`/empleados/${clave}`);
  }
}
