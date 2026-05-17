import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  activeRequests = signal(0);
  isLoading = computed(() => this.activeRequests() > 0);
}
