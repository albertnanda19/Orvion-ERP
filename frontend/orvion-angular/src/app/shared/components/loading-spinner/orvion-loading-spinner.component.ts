import { Component, input } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'orvion-loading-spinner',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  template: `
    <div
      class="flex flex-col items-center justify-center"
      [class.fixed]="fullScreen()"
      [class.inset-0]="fullScreen()"
      [class.bg-white/80]="fullScreen()"
      [class.z-50]="fullScreen()"
      [class.py-16]="!fullScreen()"
    >
      <mat-spinner diameter="40" />
      @if (message()) {
        <p class="mt-3 text-sm text-gray-500">{{ message() }}</p>
      }
    </div>
  `,
})
export class OrvionLoadingSpinnerComponent {
  fullScreen = input(false);
  message = input('Loading...');
}
