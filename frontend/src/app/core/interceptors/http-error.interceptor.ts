import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize, throwError } from 'rxjs';
import { ApiError } from '../../models/api-error.model';
import { LoadingStateService } from '../../services/loading-state.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const loading = inject(LoadingStateService);
  loading.start();

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const mapped: ApiError = {
        status: error.status,
        message: error.error?.message ?? error.message ?? 'Error inesperado',
        code: error.error?.code,
        fieldErrors: error.error?.fieldErrors,
        timestamp: error.error?.timestamp
      };
      return throwError(() => mapped);
    }),
    finalize(() => loading.stop())
  );
};
