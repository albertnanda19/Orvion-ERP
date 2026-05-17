import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrvionStatusBadgeComponent } from '../../../shared/components/status-badge/orvion-status-badge.component';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatFormFieldModule, MatInputModule, MatCardModule, MatButtonModule, MatIconModule, OrvionStatusBadgeComponent],
  template: `
    <div class="space-y-6 p-6">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-semibold text-gray-900">Customers</h1>
        <button mat-raised-button color="primary"><mat-icon class="mr-1">add</mat-icon> Add Customer</button>
      </div>
      <mat-form-field class="w-full max-w-md" appearance="outline">
        <mat-icon matPrefix class="text-gray-400 mr-2">search</mat-icon>
        <input matInput (input)="searchQuery.set($any($event).target.value)" placeholder="Search customers..." class="!text-sm" />
      </mat-form-field>
      <mat-card class="rounded-xl overflow-hidden">
        <table mat-table [dataSource]="customers()" class="w-full">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Name</th>
            <td mat-cell *matCellDef="let row" class="!text-sm !font-medium !text-gray-900">{{ row.name }}</td>
          </ng-container>
          <ng-container matColumnDef="email">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Email</th>
            <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.email }}</td>
          </ng-container>
          <ng-container matColumnDef="phone">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Phone</th>
            <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.phone }}</td>
          </ng-container>
          <ng-container matColumnDef="creditLimit">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Credit Limit</th>
            <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.creditLimit | currency }}</td>
          </ng-container>
          <ng-container matColumnDef="outstanding">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Outstanding</th>
            <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.outstandingAmount | currency }}</td>
          </ng-container>
          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-600">Status</th>
            <td mat-cell *matCellDef="let row"><orvion-status-badge [status]="row.status"></orvion-status-badge></td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns;" class="cursor-pointer hover:bg-gray-50"></tr>
        </table>
      </mat-card>
    </div>
  `
})
export class CustomerListComponent {
  searchQuery = signal('');
  columns = ['name', 'email', 'phone', 'creditLimit', 'outstanding', 'status'];
  customers = signal([
    { name: 'Acme Corp', email: 'contact@acme.com', phone: '+1-555-0100', creditLimit: 100000, outstandingAmount: 45000, status: 'ACTIVE' },
    { name: 'Globex Inc', email: 'info@globex.com', phone: '+1-555-0101', creditLimit: 200000, outstandingAmount: 120000, status: 'ACTIVE' },
    { name: 'Initech', email: 'billing@initech.com', phone: '+1-555-0102', creditLimit: 50000, outstandingAmount: 50000, status: 'SUSPENDED' },
  ]);
}
