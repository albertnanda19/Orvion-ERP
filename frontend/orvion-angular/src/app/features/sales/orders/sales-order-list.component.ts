import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { OrvionStatusBadgeComponent } from '../../../shared/components/status-badge/orvion-status-badge.component';

@Component({
  selector: 'app-sales-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, MatDatepickerModule, OrvionStatusBadgeComponent],
  template: `
    <div class="space-y-6 p-6">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-semibold text-gray-900">Sales Orders</h1>
        <button mat-raised-button color="primary"><mat-icon class="mr-1">add</mat-icon> New Order</button>
      </div>
      <div class="flex gap-4 flex-wrap">
        <mat-form-field class="flex-1 min-w-[200px]" appearance="outline">
          <mat-icon matPrefix class="text-gray-400 mr-2">search</mat-icon>
          <input matInput [(ngModel)]="searchQuery" placeholder="Search orders..." />
        </mat-form-field>
        <mat-form-field class="w-[180px]" appearance="outline">
          <mat-label>Status</mat-label>
          <mat-select [(ngModel)]="statusFilter">
            <mat-option value="">All</mat-option>
            <mat-option value="DRAFT">Draft</mat-option>
            <mat-option value="CONFIRMED">Confirmed</mat-option>
            <mat-option value="PROCESSING">Processing</mat-option>
            <mat-option value="SHIPPED">Shipped</mat-option>
            <mat-option value="DELIVERED">Delivered</mat-option>
            <mat-option value="CANCELLED">Cancelled</mat-option>
          </mat-select>
        </mat-form-field>
      </div>
      <mat-card class="rounded-xl overflow-hidden">
        <table mat-table [dataSource]="filteredOrders()" class="w-full">
          <ng-container matColumnDef="orderNo">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Order #</th>
            <td mat-cell *matCellDef="let row" class="!text-sm !font-medium">{{ row.orderNo }}</td>
          </ng-container>
          <ng-container matColumnDef="customer">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Customer</th>
            <td mat-cell *matCellDef="let row" class="!text-sm">{{ row.customerName }}</td>
          </ng-container>
          <ng-container matColumnDef="date">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Date</th>
            <td mat-cell *matCellDef="let row" class="!text-sm">{{ row.orderDate }}</td>
          </ng-container>
          <ng-container matColumnDef="total">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Total</th>
            <td mat-cell *matCellDef="let row" class="!text-sm">{{ row.totalAmount | currency }}</td>
          </ng-container>
          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Status</th>
            <td mat-cell *matCellDef="let row"><orvion-status-badge [status]="row.status"></orvion-status-badge></td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Actions</th>
            <td mat-cell *matCellDef="let row"><button mat-icon-button color="primary"><mat-icon>visibility</mat-icon></button></td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns;" class="cursor-pointer hover:bg-gray-50"></tr>
        </table>
        <mat-paginator [length]="totalElements" [pageSize]="10" class="!border-t !border-gray-200"></mat-paginator>
      </mat-card>
    </div>
  `
})
export class SalesOrderListComponent {
  searchQuery = '';
  statusFilter = '';
  totalElements = 100;
  columns = ['orderNo', 'customer', 'date', 'total', 'status', 'actions'];
  orders = signal([
    { orderNo: 'SO-2026-0001', customerName: 'Acme Corp', orderDate: '2026-05-01', totalAmount: 15000, status: 'CONFIRMED' },
    { orderNo: 'SO-2026-0002', customerName: 'Globex Inc', orderDate: '2026-05-03', totalAmount: 28000, status: 'PROCESSING' },
    { orderNo: 'SO-2026-0003', customerName: 'Initech', orderDate: '2026-05-05', totalAmount: 7500, status: 'DELIVERED' },
    { orderNo: 'SO-2026-0004', customerName: 'Umbrella Corp', orderDate: '2026-05-07', totalAmount: 42000, status: 'SHIPPED' },
    { orderNo: 'SO-2026-0005', customerName: 'Stark Industries', orderDate: '2026-05-10', totalAmount: 95000, status: 'DRAFT' },
  ]);

  filteredOrders = computed(() => {
    let filtered = this.orders();
    if (this.searchQuery) filtered = filtered.filter(o => o.orderNo.toLowerCase().includes(this.searchQuery.toLowerCase()) || o.customerName.toLowerCase().includes(this.searchQuery.toLowerCase()));
    if (this.statusFilter) filtered = filtered.filter(o => o.status === this.statusFilter);
    return filtered;
  });
}
