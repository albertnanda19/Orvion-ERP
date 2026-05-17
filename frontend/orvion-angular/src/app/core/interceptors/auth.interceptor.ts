import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { from, switchMap, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);
  const excludedUrls = ['/assets/', environment.keycloak.url];

  const isExcluded = excludedUrls.some(u => req.url.includes(u));
  if (isExcluded) return next(req);

  return from(keycloak.getToken()).pipe(
    switchMap(token => {
      const authReq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
      return next(authReq);
    }),
    catchError(err => {
      if (err instanceof HttpErrorResponse && err.status === 401) {
        keycloak.login();
      }
      return throwError(() => err);
    })
  );
};
