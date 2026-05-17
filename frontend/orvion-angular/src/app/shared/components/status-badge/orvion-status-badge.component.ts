import { Component, input, computed } from '@angular/core';

@Component({
  selector: 'orvion-status-badge',
  standalone: true,
  imports: [],
  template: `
    <span
      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
      [class]="badgeClass()"
    >
      {{ status() }}
    </span>
  `,
})
export class OrvionStatusBadgeComponent {
  status = input.required<string>();
  type = input<'invoice' | 'order' | 'default'>('default');

  private colorMap: Record<string, string> = {
    DRAFT: 'bg-gray-100 text-gray-700',
    PENDING: 'bg-yellow-100 text-yellow-700',
    APPROVED: 'bg-green-100 text-green-700',
    ACTIVE: 'bg-green-100 text-green-700',
    PAID: 'bg-blue-100 text-blue-700',
    SENT: 'bg-purple-100 text-purple-700',
    RECEIVED: 'bg-green-100 text-green-700',
    COMPLETED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-red-100 text-red-700',
    REJECTED: 'bg-red-100 text-red-700',
    VOID: 'bg-red-100 text-red-700',
    OVERDUE: 'bg-orange-100 text-orange-700',
    FAILED: 'bg-red-100 text-red-700',
    IN_PROGRESS: 'bg-yellow-100 text-yellow-700',
    PLANNED: 'bg-gray-100 text-gray-700',
    CONFIRMED: 'bg-blue-100 text-blue-700',
    SHIPPED: 'bg-purple-100 text-purple-700',
    DELIVERED: 'bg-green-100 text-green-700',
    RUNNING: 'bg-green-100 text-green-700',
    IDLE: 'bg-yellow-100 text-yellow-700',
    MAINTENANCE: 'bg-red-100 text-red-700',
    DOWN: 'bg-gray-100 text-gray-700',
    NEW: 'bg-blue-100 text-blue-700',
    CONTACTED: 'bg-yellow-100 text-yellow-700',
    QUALIFIED: 'bg-green-100 text-green-700',
    PROPOSAL: 'bg-purple-100 text-purple-700',
    NEGOTIATION: 'bg-orange-100 text-orange-700',
    CLOSED_WON: 'bg-green-100 text-green-700',
    CLOSED_LOST: 'bg-red-100 text-red-700',
  };

  badgeClass = computed(() => {
    const s = this.status()?.toUpperCase() || '';
    return this.colorMap[s] || 'bg-gray-100 text-gray-700';
  });
}
