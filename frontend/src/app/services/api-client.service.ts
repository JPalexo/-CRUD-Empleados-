import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiClientService {
  constructor(private readonly http: HttpClient) {}

  get<T>(path: string, query?: Record<string, string | number | boolean | undefined>): Observable<T> {
    let params = new HttpParams();
    if (query) {
      Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined) {
          params = params.set(key, String(value));
        }
      });
    }
    return this.http.get<T>(`${environment.apiBaseUrl}${path}`, { params });
  }

  post<TRequest, TResponse>(path: string, body: TRequest): Observable<TResponse> {
    return this.http.post<TResponse>(`${environment.apiBaseUrl}${path}`, body);
  }

  put<TRequest, TResponse>(path: string, body: TRequest): Observable<TResponse> {
    return this.http.put<TResponse>(`${environment.apiBaseUrl}${path}`, body);
  }

  delete(path: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}${path}`);
  }
}
