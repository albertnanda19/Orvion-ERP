import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

interface StockMovement {
  id: string;
  date: string;
  product: string;
  sku: string;
  type: 'IN' | 'OUT' | 'TRANSFER';
  quantity: number;
  reference: string;
  user: string;
}

@Component({
  selector: 'app-stock-movement-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatFormFieldModule, MatSelectModule, MatDatepickerModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Stock Movements</h1>
        <button mat-stroked-button color="primary">
          <mat-icon>download</mat-icon>
          Export
        </button>
      </div>

      <div class="flex gap-4 mb-4 items-start">
        <mat-form-field appearance="outline" class="w-44">
          <mat-label>Date Range</mat-label>
          <mat-date-range-input [rangePicker]="picker">
            <input matStartDate placeholder="Start" />
            <input matEndDate placeholder="End" />
          </mat-date-range-input>
          <mat-datepicker-toggle matSuffix [for]="picker" />
          <mat-date-range-picker #picker />
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-40">
          <mat-label>Movement Type</mat-label>
          <mat-select (selectionChange)="onTypeChange($event.value)">
            <mat-option value="">All Types</mat-option>
            <mat-option value="IN">Inbound</mat-option>
            <mat-option value="OUT">Outbound</mat-option>
            <mat-option value="TRANSFER">Transfer</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="flex-1">
          <mat-label>Search product</mat-label>
          <input matInput (input)="onSearch($event)" placeholder="Search by product or SKU" />
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table mat-table [dataSource]="movements()" class="w-full">
          <ng-container matColumnDef="date">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Date</th>
            <td mat-cell *matCellDef="let m">{{ m.date | date:'mediumDate' }}</td>
          </ng-container>

          <ng-container matColumnDef="product">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Product</th>
            <td mat-cell *matCellDef="let m">
              <div class="font-medium">{{ m.product }}</div>
              <div class="text-xs text-gray-500 font-mono">{{ m.sku }}</div>
            </td>
          </ng-container>

          <ng-container matColumnDef="type">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Type</th>
            <td mat-cell *matCellDef="let m">
              <span class="px-2 py-0.5 rounded-full text-xs font-semibold"
                    [class.bg-green-100]="m.type === 'IN'" [class.text-green-800]="m.type === 'IN'"
                    [class.bg-red-100]="m.type === 'OUT'" [class.text-red-800]="m.type === 'OUT'"
                    [class.bg-blue-100]="m.type === 'TRANSFER'" [class.text-blue-800]="m.type === 'TRANSFER'">
                {{ m.type }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="quantity">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Quantity</th>
            <td mat-cell *matCellDef="let m" class="font-semibold">{{ m.quantity }}</td>
          </ng-container>

          <ng-container matColumnDef="reference">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Reference</th>
            <td mat-cell *matCellDef="let m" class="text-sm text-gray-600">{{ m.reference }}</td>
          </ng-container>

          <ng-container matColumnDef="user">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">User</th>
            <td mat-cell *matCellDef="let m">{{ m.user }}</td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
        </table>

        @if (movements().length === 0) {
          <div class="text-center py-12 text-gray-500">
            <mat-icon class="text-4xl mb-2">swap_vert</mat-icon>
            <p>No stock movements recorded</p>
          </div>
        }
      </div>
    </div>
  `
})
export class StockMovementListComponent {
  movements = signal<StockMovement[]>([]);
  displayedColumns = ['date', 'product', 'type', 'quantity', 'reference', 'user'];

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    console.log('Search movements:', value);
  }

  onTypeChange(type: string): void {
    console.log('Filter by type:', type);
  }
}
