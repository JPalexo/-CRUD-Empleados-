export interface PaginationMetadata {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface Empleado {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string;
}

export interface EmpleadoPageResponse {
  data: Empleado[];
  pagination: PaginationMetadata;
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
  departamentoClave?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  authenticated: boolean;
  empleado?: {
    clave: string;
    nombre: string;
    email: string;
  };
}
