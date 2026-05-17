import { Component, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'orvion-empty-state',
  standalone: true,
  imports: [MatIconModule, MatButtonModule],
  template: `
    <div class="flex flex-col items-center justify-center py-16 px-4">
      <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-4">
        <mat-icon class="text-3xl text-gray-400">{{ icon() }}</mat-icon>
      </div>
      @if (title()) {
        <h3 class="text-lg font-semibold text-gray-900 mb-1">{{ title() }}</h3>
      }
      @if (message()) {
        <p class="text-sm text-gray-500 text-center max-w-sm mb-6">{{ message() }}</p>
      }
      @if (actionLabel()) {
        <button mat-raised-button color="primary" (click)="action.emit()">
          {{ actionLabel() }}
        </button>
      }
    </div>
  `,
})
export class OrvionEmptyStateComponent {
  icon = input('inbox');
  title = input<string>('');
  message = input<string>('');
  actionLabel = input<string>('');
  action = output<void>();
}
