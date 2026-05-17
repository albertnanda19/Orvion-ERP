import { Component, signal, computed, OnInit } from '@angular/core';
import { inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/services/auth.service';
import { DashboardResponse, KpiData } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
  ],
  template: `
    @if (loading()) {
      <div class="space-y-6">
        <div class="h-8 w-64 bg-gray-200 rounded animate-pulse"></div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          @for (_ of [1,2,3,4,5,6,7,8]; track _) {
            <div class="h-32 bg-gray-200 rounded-xl animate-pulse"></div>
          }
        </div>
      </div>
    } @else {
      <div class="space-y-6">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-semibold text-gray-900">Welcome back, {{ authService.currentUser()?.firstName }}</h1>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          @for (kpi of kpis(); track kpi.label) {
            <mat-card class="p-4 rounded-xl hover:shadow-md transition-shadow">
              <div class="flex items-start justify-between">
                <div
                  class="w-10 h-10 rounded-lg flex items-center justify-center"
                  [style.background-color]="kpi.color + '20'"
                >
                  <mat-icon [style.color]="kpi.color" class="text-lg">{{ kpi.icon }}</mat-icon>
                </div>
                @if (kpi.trend) {
                  <span
                    class="text-xs font-medium flex items-center gap-0.5"
                    [class.text-green-600]="kpi.trend === 'up'"
                    [class.text-red-600]="kpi.trend === 'down'"
                    [class.text-gray-400]="kpi.trend === 'neutral'"
                  >
                    <mat-icon class="text-sm">
                      {{ kpi.trend === 'up' ? 'arrow_upward' : kpi.trend === 'down' ? 'arrow_downward' : 'remove' }}
                    </mat-icon>
                    {{ kpi.change }}%
                  </span>
                }
              </div>
              <div class="mt-3">
                <p class="text-sm text-gray-500">{{ kpi.label }}</p>
                <p class="text-xl font-bold text-gray-900 mt-0.5">{{ formatKpiValue(kpi) }}</p>
              </div>
            </mat-card>
          }
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <mat-card class="lg:col-span-2 p-6 rounded-xl">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Revenue vs Expenses</h3>
            <div class="h-64 bg-gray-100 rounded-lg flex items-center justify-center text-gray-400">
              <div class="text-center">
                <mat-icon class="text-4xl mb-2">bar_chart</mat-icon>
                <p class="text-sm">Chart placeholder</p>
              </div>
            </div>
          </mat-card>

          <mat-card class="p-6 rounded-xl">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
            <div class="space-y-4">
              @for (activity of recentActivities(); track activity.id) {
                <div class="flex items-start gap-3 pb-3 border-b border-gray-100 last:border-0">
                  <div
                    class="w-8 h-8 rounded-full flex items-center justify-center shrink-0"
                    [style.background-color]="activity.color + '20'"
                  >
                    <mat-icon [style.color]="activity.color" class="text-sm">{{ activity.icon }}</mat-icon>
                  </div>
                  <div>
                    <p class="text-sm text-gray-700">{{ activity.message }}</p>
                    <p class="text-xs text-gray-400 mt-0.5">{{ activity.time }}</p>
                  </div>
                </div>
              }
            </div>
          </mat-card>
        </div>
      </div>
    }
  `,
})
export class DashboardComponent implements OnInit {
  protected authService = inject(AuthService);

  dashboardData = signal<DashboardResponse | null>(null);
  loading = signal(true);

  kpis = computed<KpiData[]>(() => {
    const data = this.dashboardData();
    if (!data) return [];
    return [
      { label: 'Total Revenue', value: data.financial.totalRevenue, icon: 'payments', color: '#10b981', trend: 'up', change: 12.5 },
      { label: 'Total Expenses', value: data.financial.totalExpenses, icon: 'account_balance_wallet', color: '#ef4444', trend: 'up', change: 3.2 },
      { label: 'Net Profit', value: data.financial.netProfit, icon: 'trending_up', color: '#3b82f6', trend: 'up', change: 8.7 },
      { label: 'Active Employees', value: data.hr.totalEmployees, icon: 'people', color: '#8b5cf6', trend: 'neutral', change: 0 },
      { label: 'Inventory Value', value: data.inventory.totalStockValue, icon: 'inventory_2', color: '#f59e0b', trend: 'up', change: 5.1 },
      { label: 'Outstanding Invoices', value: data.financial.outstandingInvoices, icon: 'receipt_long', color: '#ec4899', trend: 'down', change: 2.3 },
      { label: 'Open Orders', value: data.sales.openOrders, icon: 'shopping_cart', color: '#14b8a6', trend: 'up', change: 15.0 },
      { label: 'Low Stock Alerts', value: data.inventory.lowStockCount, icon: 'warning_amber', color: '#f97316', trend: 'down', change: 7.8 },
    ];
  });

  recentActivities = signal<{ id: number; icon: string; color: string; message: string; time: string }[]>([
    { id: 1, icon: 'receipt', color: '#3b82f6', message: 'Invoice INV-2024-089 paid', time: '2 minutes ago' },
    { id: 2, icon: 'person_add', color: '#10b981', message: 'New employee Sarah Chen onboarded', time: '15 minutes ago' },
    { id: 3, icon: 'inventory_2', color: '#f59e0b', message: 'Stock level alert: SKU-045 below threshold', time: '1 hour ago' },
    { id: 4, icon: 'assignment', color: '#8b5cf6', message: 'Work order WO-2024-123 completed', time: '2 hours ago' },
    { id: 5, icon: 'trending_up', color: '#14b8a6', message: 'Q4 sales target at 78% attainment', time: '3 hours ago' },
  ]);

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    const data: DashboardResponse = {
      financial: { totalRevenue: 2845000, totalExpenses: 1892000, netProfit: 953000, grossMargin: 0.335, outstandingInvoices: 342000, cashPosition: 1580000 },
      inventory: { totalProducts: 1248, totalStockValue: 4780000, lowStockCount: 23, turnoverRate: 4.2 },
      sales: { totalOrders: 456, totalSalesRevenue: 2845000, conversionRate: 0.28, avgOrderValue: 6238, openOrders: 89 },
      hr: { totalEmployees: 312, totalPayrollCost: 1420000 },
    };
    this.dashboardData.set(data);
    this.loading.set(false);
  }

  formatKpiValue(kpi: KpiData): string {
    if (kpi.label === 'Active Employees' || kpi.label === 'Low Stock Alerts' || kpi.label === 'Open Orders' || kpi.label === 'Outstanding Invoices') {
      return kpi.value.toLocaleString();
    }
    return '$' + kpi.value.toLocaleString();
  }
}
