import { Component, input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'orvion-kpi-card',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  template: `
    <div class="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow">
      <div class="flex items-start justify-between">
        <div class="w-10 h-10 rounded-lg flex items-center justify-center" [style.background-color]="iconBg()">
          <mat-icon [style.color]="iconColorFromType()" class="text-lg">{{ icon() }}</mat-icon>
        </div>
        @if (trend()) {
          <span
            class="text-xs font-medium flex items-center gap-0.5"
            [class.text-green-600]="trend() === 'up'"
            [class.text-red-600]="trend() === 'down'"
            [class.text-gray-400]="trend() === 'neutral'"
          >
            <mat-icon class="text-sm">
              {{ trend() === 'up' ? 'arrow_upward' : trend() === 'down' ? 'arrow_downward' : 'remove' }}
            </mat-icon>
            {{ change() }}%
          </span>
        }
      </div>
      <div class="mt-3">
        <p class="text-sm text-gray-500">{{ label() }}</p>
        <p class="text-xl font-bold text-gray-900 mt-0.5">{{ displayValue() }}</p>
      </div>
    </div>
  `,
})
export class OrvionKpiCardComponent {
  label = input.required<string>();
  value = input.required<number | string>();
  icon = input.required<string>();
  trend = input<'up' | 'down' | 'neutral'>('neutral');
  change = input<number>(0);

  private numericValue = computed(() => typeof this.value() === 'number' ? this.value() as number : 0);

  displayValue = computed(() => {
    const v = this.value();
    if (typeof v === 'string') return v;
    return '$' + v.toLocaleString();
  });

  iconBg = computed(() => {
    const t = this.trend();
    if (t === 'up') return '#d1fae5';
    if (t === 'down') return '#fee2e2';
    return '#f3f4f6';
  });

  iconColorFromType = computed(() => {
    const t = this.trend();
    if (t === 'up') return '#059669';
    if (t === 'down') return '#dc2626';
    return '#9ca3af';
  });
}
