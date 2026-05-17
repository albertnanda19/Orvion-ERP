import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { OrvionStatusBadgeComponent } from '../../../shared/components/status-badge/orvion-status-badge.component';

interface WorkOrder {
  id: string;
  orderNo: string;
  productName: string;
  quantity: number;
  status: string;
  dueDate: string;
  progress: number;
}

@Component({
  selector: 'app-work-order-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatButtonToggleModule,
    OrvionStatusBadgeComponent,
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h1 class="text-2xl font-semibold text-gray-900">Work Orders</h1>
        <mat-button-toggle-group
          [value]="viewMode()"
          (change)="viewMode.set($event.value)"
          class="!border !border-gray-200 !rounded-lg"
          hideSingleSelectionIndicator
        >
          <mat-button-toggle value="kanban" class="!text-sm">
            <mat-icon class="!text-lg !mr-1">dashboard</mat-icon>
            Kanban
          </mat-button-toggle>
          <mat-button-toggle value="table" class="!text-sm">
            <mat-icon class="!text-lg !mr-1">table_rows</mat-icon>
            Table
          </mat-button-toggle>
        </mat-button-toggle-group>
      </div>

      @if (viewMode() === 'kanban') {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          @for (col of kanbanColumns; track col.status) {
            <div class="bg-gray-50 rounded-xl p-4">
              <div class="flex items-center justify-between mb-4">
                <h3 class="font-semibold text-gray-700 text-sm uppercase tracking-wider">{{ col.label }}</h3>
                <span class="text-xs font-medium text-gray-400 bg-white px-2 py-0.5 rounded-full">{{ cardsByStatus(col.status).length }}</span>
              </div>
              <div class="space-y-3 min-h-[200px]">
                @for (card of cardsByStatus(col.status); track card.id) {
                  <div class="bg-white rounded-lg border border-gray-200 p-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
                    <p class="font-medium text-gray-900 text-sm">{{ card.orderNo }}</p>
                    <p class="text-xs text-gray-500 mt-1">{{ card.productName }}</p>
                    <div class="flex items-center justify-between mt-3 text-xs text-gray-400">
                      <span>Qty: {{ card.quantity }}</span>
                      <span>{{ card.dueDate | date:'MMM d' }}</span>
                    </div>
                    <div class="mt-2 w-full bg-gray-200 rounded-full h-1.5">
                      <div
                        class="h-1.5 rounded-full bg-primary-500"
                        [style.width.%]="card.progress"
                      ></div>
                    </div>
                  </div>
                }
              </div>
            </div>
          }
        </div>
      } @else {
        <mat-card class="rounded-xl overflow-hidden">
          <div class="overflow-x-auto">
            <table mat-table [dataSource]="workOrders()" class="w-full">

              <ng-container matColumnDef="orderNo">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Order#</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !font-medium !text-gray-900">{{ row.orderNo }}</td>
              </ng-container>

              <ng-container matColumnDef="productName">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Product</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.productName }}</td>
              </ng-container>

              <ng-container matColumnDef="quantity">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Quantity</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.quantity }}</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Status</th>
                <td mat-cell *matCellDef="let row">
                  <orvion-status-badge [status]="row.status" />
                </td>
              </ng-container>

              <ng-container matColumnDef="dueDate">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Due Date</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.dueDate | date:'mediumDate' }}</td>
              </ng-container>

              <ng-container matColumnDef="progress">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Progress</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">
                  <div class="flex items-center gap-2">
                    <div class="flex-1 bg-gray-200 rounded-full h-2">
                      <div
                        class="h-2 rounded-full"
                        [class.bg-green-500]="row.progress === 100"
                        [class.bg-yellow-500]="row.progress > 0 && row.progress < 100"
                        [class.bg-gray-300]="row.progress === 0"
                        [style.width.%]="row.progress"
                      ></div>
                    </div>
                    <span class="text-xs text-gray-500 w-8">{{ row.progress }}%</span>
                  </div>
                </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Actions</th>
                <td mat-cell *matCellDef="let row">
                  <button mat-icon-button color="primary" (click)="viewOrder(row)" matTooltip="View">
                    <mat-icon class="text-sm">visibility</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="tableColumns" class="!border-b !border-gray-200"></tr>
              <tr mat-row *matRowDef="let row; columns: tableColumns;" class="!border-b !border-gray-100 hover:!bg-gray-50 transition-colors"></tr>
            </table>
          </div>
        </mat-card>
      }
    </div>
  `,
})
export class WorkOrderListComponent {
  viewMode = signal<'kanban' | 'table'>('kanban');

  tableColumns: string[] = ['orderNo', 'productName', 'quantity', 'status', 'dueDate', 'progress', 'actions'];

  kanbanColumns = [
    { status: 'PLANNED', label: 'Planned' },
    { status: 'IN_PROGRESS', label: 'In Progress' },
    { status: 'COMPLETED', label: 'Completed' },
  ];

  workOrders = signal<WorkOrder[]>([
    { id: '1', orderNo: 'WO-001', productName: 'Widget Alpha', quantity: 100, status: 'IN_PROGRESS', dueDate: '2026-06-15', progress: 45 },
    { id: '2', orderNo: 'WO-002', productName: 'Gadget Beta', quantity: 50, status: 'PLANNED', dueDate: '2026-06-20', progress: 0 },
    { id: '3', orderNo: 'WO-003', productName: 'Component X', quantity: 200, status: 'COMPLETED', dueDate: '2026-05-30', progress: 100 },
    { id: '4', orderNo: 'WO-004', productName: 'Assembly Gamma', quantity: 25, status: 'IN_PROGRESS', dueDate: '2026-06-25', progress: 70 },
    { id: '5', orderNo: 'WO-005', productName: 'Part Delta', quantity: 500, status: 'PLANNED', dueDate: '2026-07-01', progress: 0 },
    { id: '6', orderNo: 'WO-006', productName: 'Widget Alpha', quantity: 150, status: 'COMPLETED', dueDate: '2026-05-25', progress: 100 },
    { id: '7', orderNo: 'WO-007', productName: 'Gadget Beta', quantity: 75, status: 'IN_PROGRESS', dueDate: '2026-06-18', progress: 30 },
  ]);

  cardsByStatus(status: string): WorkOrder[] {
    return this.workOrders().filter(wo => wo.status === status);
  }

  viewOrder(order: WorkOrder): void {
    console.log('View order:', order.id);
  }
}
