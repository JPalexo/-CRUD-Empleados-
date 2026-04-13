export interface Pagination {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PageResponse<T> {
  data: T[];
  pagination: Pagination;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
}

export interface Departamento {
  clave: string;
  nombre: string;
  ocupacionActual: number;
  capacidadMaxima: number;
  porcentajeOcupacion: number;
  estaLleno: boolean;
}

export interface DepartamentoUpsertRequest {
  nombre: string;
}

export interface Empleado {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string;
}

export interface EmpleadoCreateRequest {
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
  password: string;
  departamentoClave: string;
}

export interface EmpleadoUpdateRequest {
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string;
}

export interface EmpleadoLoginRequest {
  email: string;
  password: string;
}

export interface EmpleadoLoginIdentity {
  clave: string;
  nombre: string;
  email: string;
}

export interface EmpleadoLoginResponse {
  authenticated: true;
  empleado: EmpleadoLoginIdentity;
}

export interface AdminCredentials {
  username: string;
  password: string;
}
