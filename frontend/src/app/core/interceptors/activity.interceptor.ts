import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthSessionService } from '../../services/auth-session.service';

export const activityInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthSessionService);
  const token = auth.getAuthorizationHeader();

  if (token) {
    auth.touchActivity();
  }

  const withAuth = token ? req.clone({ setHeaders: { Authorization: token } }) : req;

  return next(withAuth);
};
