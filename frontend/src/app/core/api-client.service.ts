import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import {
  Departamento,
  DepartamentoUpsertRequest,
  Empleado,
  EmpleadoCreateRequest,
  EmpleadoLoginRequest,
  EmpleadoLoginResponse,
  EmpleadoUpdateRequest,
  PageResponse
} from './models';
import { SessionService } from './session.service';

@Injectable({ providedIn: 'root' })
export class ApiClientService {
  private readonly baseUrl = '/api/v1';

  constructor(
    private readonly http: HttpClient,
    private readonly session: SessionService
  ) {}

  verifyAdminCredentials(username: string, password: string): Observable<void> {
    const headers = this.createAdminHeaders({ username, password });
    const params = new HttpParams().set('page', 0).set('size', 1);

    return this.http
      .get<PageResponse<Departamento>>(`${this.baseUrl}/departamentos`, { headers, params })
      .pipe(map(() => void 0));
  }

  listDepartamentos(page: number, size: number): Observable<PageResponse<Departamento>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Departamento>>(`${this.baseUrl}/departamentos`, {
      headers: this.createAdminHeaders(),
      params
    });
  }

  createDepartamento(request: DepartamentoUpsertRequest): Observable<Departamento> {
    return this.http.post<Departamento>(`${this.baseUrl}/departamentos`, request, {
      headers: this.createAdminHeaders()
    });
  }

  updateDepartamento(clave: string, request: DepartamentoUpsertRequest): Observable<Departamento> {
    return this.http.put<Departamento>(`${this.baseUrl}/departamentos/${clave}`, request, {
      headers: this.createAdminHeaders()
    });
  }

  deleteDepartamento(clave: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/departamentos/${clave}`, {
      headers: this.createAdminHeaders()
    });
  }

  listEmpleados(page: number, size: number): Observable<PageResponse<Empleado>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Empleado>>(`${this.baseUrl}/empleados`, {
      headers: this.createAdminHeaders(),
      params
    });
  }

  createEmpleado(request: EmpleadoCreateRequest): Observable<Empleado> {
    return this.http.post<Empleado>(`${this.baseUrl}/empleados`, request, {
      headers: this.createAdminHeaders()
    });
  }

  updateEmpleado(clave: string, request: EmpleadoUpdateRequest): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.baseUrl}/empleados/${clave}`, request, {
      headers: this.createAdminHeaders()
    });
  }

  deleteEmpleado(clave: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/empleados/${clave}`, {
      headers: this.createAdminHeaders()
    });
  }

  loginEmpleado(request: EmpleadoLoginRequest): Observable<EmpleadoLoginResponse> {
    return this.http.post<EmpleadoLoginResponse>(`${this.baseUrl}/empleados/login`, request);
  }

  private createAdminHeaders(credentials?: { username: string; password: string }): HttpHeaders {
    const authHeader = this.session.buildBasicAuthHeader(credentials);
    return authHeader
      ? new HttpHeaders({ Authorization: authHeader })
      : new HttpHeaders();
  }
}
