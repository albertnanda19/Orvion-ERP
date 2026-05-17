import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

interface ReportCard {
  icon: string;
  title: string;
  description: string;
  route: string;
  color: string;
}

@Component({
  selector: 'app-finance-reports',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink,
  ],
  styles: [`
    :host { display: block; padding: 1.5rem; }
    .report-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.25rem; margin-top: 1.5rem; }
    .report-card { padding: 1.5rem; cursor: pointer; transition: box-shadow 0.2s, transform 0.2s; }
    .report-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); transform: translateY(-2px); }
    .card-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin-bottom: 1rem; }
    .card-icon mat-icon { font-size: 24px; width: 24px; height: 24px; color: white; }
  `],
  template: `
    <div class="mb-2">
      <h1 class="text-2xl font-bold text-gray-900 m-0">Financial Reports</h1>
      <p class="text-gray-500 mt-1 mb-0">View and analyze your financial data</p>
    </div>

    <div class="report-grid">
      @for (card of reportCards; track card.title) {
        <mat-card class="report-card" appearance="outlined" [routerLink]="card.route">
          <div class="card-icon" [style.background-color]="card.color">
            <mat-icon>{{ card.icon }}</mat-icon>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-1">{{ card.title }}</h3>
          <p class="text-sm text-gray-500 mb-4">{{ card.description }}</p>
          <button mat-stroked-button color="primary" (click)="$event.stopPropagation()" [routerLink]="card.route">
            View Report
          </button>
        </mat-card>
      }
    </div>

    <mat-card appearance="outlined" class="mt-8 p-6 text-center text-gray-400">
      <mat-icon class="text-4xl mb-2" style="width:40px;height:40px;font-size:40px;">construction</mat-icon>
      <p class="text-sm m-0">Report generation and export features are under development.</p>
    </mat-card>
  `
})
export class FinanceReportsComponent {
  protected readonly reportCards: ReportCard[] = [
    {
      icon: 'balance',
      title: 'Trial Balance',
      description: 'View the trial balance summary for any accounting period with debit and credit totals.',
      route: '/finance/reports/trial-balance',
      color: '#3b82f6',
    },
    {
      icon: 'trending_up',
      title: 'Profit & Loss',
      description: 'Analyze revenue, cost of goods sold, and expenses to see your net profit or loss.',
      route: '/finance/reports/profit-loss',
      color: '#10b981',
    },
    {
      icon: 'account_balance',
      title: 'Balance Sheet',
      description: 'Review your company\'s assets, liabilities, and equity at a glance.',
      route: '/finance/reports/balance-sheet',
      color: '#8b5cf6',
    },
    {
      icon: 'dashboard',
      title: 'Executive Dashboard',
      description: 'Key financial KPIs including revenue, expenses, profit margins, and cash position.',
      route: '/finance/reports/executive-dashboard',
      color: '#f59e0b',
    },
  ];
}
