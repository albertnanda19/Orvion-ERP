import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { PurchaseOrder } from '../../../core/models';

interface PurchaseOrderExtended extends PurchaseOrder {
  expectedDate: string;
}

@Component({
  selector: 'app-purchase-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, MatDatepickerModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Purchase Orders</h1>
        <button mat-flat-button color="primary">
          <mat-icon>add</mat-icon>
          Create PO
        </button>
      </div>

      <div class="flex gap-4 mb-4 items-start">
        <mat-form-field appearance="outline" class="flex-1">
          <mat-label>Search POs</mat-label>
          <input matInput (input)="onSearch($event)" placeholder="Search by PO# or supplier" />
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-44">
          <mat-label>Status</mat-label>
          <mat-select (selectionChange)="onStatusChange($event.value)">
            <mat-option value="">All Statuses</mat-option>
            <mat-option value="DRAFT">Draft</mat-option>
            <mat-option value="PENDING_APPROVAL">Pending Approval</mat-option>
            <mat-option value="APPROVED">Approved</mat-option>
            <mat-option value="SENT">Sent</mat-option>
            <mat-option value="RECEIVED">Received</mat-option>
            <mat-option value="CANCELLED">Cancelled</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-44">
          <mat-label>Date Range</mat-label>
          <mat-date-range-input [rangePicker]="picker">
            <input matStartDate placeholder="Start" />
            <input matEndDate placeholder="End" />
          </mat-date-range-input>
          <mat-datepicker-toggle matSuffix [for]="picker" />
          <mat-date-range-picker #picker />
        </mat-form-field>
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table mat-table [dataSource]="purchaseOrders()" class="w-full">
          <ng-container matColumnDef="poNumber">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">PO#</th>
            <td mat-cell *matCellDef="let po" class="font-mono text-sm font-medium text-blue-600">{{ po.poNumber }}</td>
          </ng-container>

          <ng-container matColumnDef="supplierName">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Supplier</th>
            <td mat-cell *matCellDef="let po">{{ po.supplierName }}</td>
          </ng-container>

          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
            <td mat-cell *matCellDef="let po">
              <span class="px-2 py-0.5 rounded-full text-xs font-semibold" [class.bg-gray-100]="po.status === 'DRAFT'" [class.text-gray-700]="po.status === 'DRAFT'"
                    [class.bg-yellow-100]="po.status === 'PENDING_APPROVAL'" [class.text-yellow-800]="po.status === 'PENDING_APPROVAL'"
                    [class.bg-blue-100]="po.status === 'APPROVED'" [class.text-blue-800]="po.status === 'APPROVED'"
                    [class.bg-purple-100]="po.status === 'SENT'" [class.text-purple-800]="po.status === 'SENT'"
                    [class.bg-green-100]="po.status === 'RECEIVED'" [class.text-green-800]="po.status === 'RECEIVED'"
                    [class.bg-red-100]="po.status === 'CANCELLED'" [class.text-red-800]="po.status === 'CANCELLED'">
                {{ po.status }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="totalAmount">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Total Amount</th>
            <td mat-cell *matCellDef="let po">{{ po.totalAmount | currency }}</td>
          </ng-container>

          <ng-container matColumnDef="orderDate">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Order Date</th>
            <td mat-cell *matCellDef="let po">{{ po.orderDate | date:'mediumDate' }}</td>
          </ng-container>

          <ng-container matColumnDef="expectedDate">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Expected Date</th>
            <td mat-cell *matCellDef="let po">{{ po.expectedDate | date:'mediumDate' }}</td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600 w-20">Actions</th>
            <td mat-cell *matCellDef="let po">
              <button mat-icon-button color="primary" aria-label="View purchase order" [routerLink]="['/inventory/purchase-orders', po.id]">
                <mat-icon>visibility</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
        </table>

        @if (purchaseOrders().length === 0) {
          <div class="text-center py-12 text-gray-500">
            <mat-icon class="text-4xl mb-2">receipt_long</mat-icon>
            <p>No purchase orders found</p>
          </div>
        }

        <mat-paginator [length]="totalElements()" [pageSize]="20" [pageSizeOptions]="[10, 20, 50]" (page)="onPageChange($event)" class="border-t" />
      </div>
    </div>
  `
})
export class PurchaseOrderListComponent {
  purchaseOrders = signal<PurchaseOrderExtended[]>([]);
  loading = signal(false);
  totalElements = signal(0);
  displayedColumns = ['poNumber', 'supplierName', 'status', 'totalAmount', 'orderDate', 'expectedDate', 'actions'];

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    console.log('Search POs:', value);
  }

  onStatusChange(status: string): void {
    console.log('Filter by status:', status);
  }

  onPageChange(event: any): void {
    console.log('Page change:', event);
  }
}
