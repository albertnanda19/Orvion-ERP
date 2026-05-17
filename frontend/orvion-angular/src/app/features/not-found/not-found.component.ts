import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink, MatButtonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50">
      <div class="text-center max-w-md px-6">
        <h1 class="text-7xl font-bold text-primary-600">404</h1>
        <h2 class="text-2xl font-semibold text-gray-900 mt-4">Page Not Found</h2>
        <p class="text-gray-500 mt-2">The page you are looking for does not exist or has been moved.</p>
        <button mat-raised-button color="primary" routerLink="/dashboard" class="mt-6">
          Back to Dashboard
        </button>
      </div>
    </div>
  `,
})
export class NotFoundComponent {}
