import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { OrvionStatusBadgeComponent } from '../../../shared/components/status-badge/orvion-status-badge.component';

interface Opportunity {
  id: string;
  name: string;
  customerName: string;
  stage: string;
  amount: number;
  probability: number;
  expectedClose: string;
}

@Component({
  selector: 'app-opportunity-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    OrvionStatusBadgeComponent,
  ],
  template: `
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-semibold text-gray-900">Opportunities</h1>
        <button mat-raised-button color="primary" (click)="createOpportunity()">
          <mat-icon class="mr-1">add</mat-icon>
          New Opportunity
        </button>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <mat-card class="lg:col-span-1 p-6 rounded-xl">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Sales Funnel</h3>
          <div class="space-y-3">
            @for (stage of funnelStages; track stage.key) {
              <div>
                <div class="flex items-center justify-between text-sm mb-1">
                  <span class="text-gray-600">{{ stage.label }}</span>
                  <span class="font-medium text-gray-900">{{ stage.count }}</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                  <div
                    class="h-3 rounded-full transition-all"
                    [style.width.%]="stage.percentage"
                    [class.bg-blue-500]="stage.key === 'QUALIFICATION'"
                    [class.bg-yellow-500]="stage.key === 'PROPOSAL'"
                    [class.bg-orange-500]="stage.key === 'NEGOTIATION'"
                    [class.bg-green-500]="stage.key === 'CLOSED_WON'"
                    [class.bg-red-500]="stage.key === 'CLOSED_LOST'"
                  ></div>
                </div>
              </div>
            }
          </div>
          <div class="mt-6 pt-4 border-t border-gray-100">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-500">Total Pipeline Value</span>
              <span class="font-semibold text-gray-900">{{ totalPipelineValue() | currency }}</span>
            </div>
            <div class="flex items-center justify-between text-sm mt-2">
              <span class="text-gray-500">Weighted Forecast</span>
              <span class="font-semibold text-gray-900">{{ weightedForecast() | currency }}</span>
            </div>
          </div>
        </mat-card>

        <mat-card class="lg:col-span-2 rounded-xl overflow-hidden">
          <div class="overflow-x-auto">
            <table mat-table [dataSource]="opportunities()" class="w-full">

              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Name</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !font-medium !text-gray-900">{{ row.name }}</td>
              </ng-container>

              <ng-container matColumnDef="customerName">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Customer</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.customerName }}</td>
              </ng-container>

              <ng-container matColumnDef="stage">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Stage</th>
                <td mat-cell *matCellDef="let row">
                  <orvion-status-badge [status]="row.stage" />
                </td>
              </ng-container>

              <ng-container matColumnDef="amount">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Amount</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.amount | currency }}</td>
              </ng-container>

              <ng-container matColumnDef="probability">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Probability</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">
                  <div class="flex items-center gap-2">
                    <div class="flex-1 bg-gray-200 rounded-full h-2">
                      <div
                        class="h-2 rounded-full"
                        [class.bg-green-500]="row.probability >= 75"
                        [class.bg-yellow-500]="row.probability >= 25 && row.probability < 75"
                        [class.bg-red-500]="row.probability < 25"
                        [style.width.%]="row.probability"
                      ></div>
                    </div>
                    <span class="text-xs text-gray-500 w-8">{{ row.probability }}%</span>
                  </div>
                </td>
              </ng-container>

              <ng-container matColumnDef="expectedClose">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Expected Close</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.expectedClose | date:'mediumDate' }}</td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Actions</th>
                <td mat-cell *matCellDef="let row">
                  <button mat-icon-button color="primary" (click)="viewOpportunity(row)" matTooltip="View">
                    <mat-icon class="text-sm">visibility</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="tableColumns" class="!border-b !border-gray-200"></tr>
              <tr mat-row *matRowDef="let row; columns: tableColumns;" class="!border-b !border-gray-100 hover:!bg-gray-50 transition-colors"></tr>
            </table>
          </div>
        </mat-card>
      </div>
    </div>
  `,
})
export class OpportunityListComponent {
  tableColumns: string[] = ['name', 'customerName', 'stage', 'amount', 'probability', 'expectedClose', 'actions'];

  funnelStages = [
    { key: 'QUALIFICATION', label: 'Qualification', count: 8, percentage: 100 },
    { key: 'PROPOSAL', label: 'Proposal', count: 5, percentage: 62 },
    { key: 'NEGOTIATION', label: 'Negotiation', count: 3, percentage: 38 },
    { key: 'CLOSED_WON', label: 'Closed Won', count: 2, percentage: 25 },
    { key: 'CLOSED_LOST', label: 'Closed Lost', count: 1, percentage: 12 },
  ];

  opportunities = signal<Opportunity[]>([
    { id: '1', name: 'ERP Implementation', customerName: 'Acme Corp', stage: 'QUALIFICATION', amount: 150000, probability: 20, expectedClose: '2026-08-15' },
    { id: '2', name: 'Cloud Migration', customerName: 'Globex Inc', stage: 'PROPOSAL', amount: 85000, probability: 45, expectedClose: '2026-07-30' },
    { id: '3', name: 'Security Audit', customerName: 'Initech', stage: 'NEGOTIATION', amount: 45000, probability: 70, expectedClose: '2026-06-20' },
    { id: '4', name: 'Data Analytics Platform', customerName: 'Hooli', stage: 'QUALIFICATION', amount: 200000, probability: 15, expectedClose: '2026-09-01' },
    { id: '5', name: 'Infrastructure Upgrade', customerName: 'Wayne Enterprises', stage: 'NEGOTIATION', amount: 320000, probability: 80, expectedClose: '2026-06-30' },
    { id: '6', name: 'Managed Services', customerName: 'Stark Industries', stage: 'CLOSED_WON', amount: 120000, probability: 100, expectedClose: '2026-05-15' },
    { id: '7', name: 'CRM Implementation', customerName: 'Cyberdyne Systems', stage: 'PROPOSAL', amount: 95000, probability: 50, expectedClose: '2026-07-15' },
  ]);

  totalPipelineValue = () => this.opportunities().reduce((sum, o) => sum + o.amount, 0);

  weightedForecast = () => this.opportunities().reduce((sum, o) => sum + (o.amount * o.probability / 100), 0);

  viewOpportunity(opp: Opportunity): void {
    console.log('View opportunity:', opp.id);
  }

  createOpportunity(): void {
    console.log('Create opportunity');
  }
}
