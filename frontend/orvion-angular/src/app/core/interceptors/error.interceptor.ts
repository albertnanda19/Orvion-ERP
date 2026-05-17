import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (!environment.production) console.error('HTTP Error:', err);

      switch (err.status) {
        case 403: router.navigate(['/unauthorized']); break;
        case 503: snackBar.open('Service temporarily unavailable', 'Close', { duration: 5000 }); break;
        case 500: snackBar.open('An internal error occurred', 'Close', { duration: 5000 }); break;
        case 0: snackBar.open('Cannot connect to server', 'Close', { duration: 5000 }); break;
      }
      return throwError(() => err);
    })
  );
};
