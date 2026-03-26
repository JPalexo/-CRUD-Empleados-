import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthSessionService } from '../../services/auth-session.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthSessionService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    auth.touchActivity();
    return true;
  }

  return router.createUrlTree(['/login']);
};
