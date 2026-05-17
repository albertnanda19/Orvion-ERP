import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoadingService } from '../services/loading.service';
import { finalize } from 'rxjs';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);
  loadingService.activeRequests.update(n => n + 1);
  return next(req).pipe(finalize(() => loadingService.activeRequests.update(n => n - 1)));
};
