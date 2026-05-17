import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { PayrollRecord } from '../../../core/models';

interface PayrollRecordExtended extends PayrollRecord {
  employeeName: string;
}

@Component({
  selector: 'app-payroll',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Payroll Processing</h1>
        <div class="flex items-center gap-3">
          <mat-form-field appearance="outline" class="w-32">
            <mat-label>Month</mat-label>
            <mat-select [(value)]="selectedMonth">
              <mat-option value="1">January</mat-option>
              <mat-option value="2">February</mat-option>
              <mat-option value="3">March</mat-option>
              <mat-option value="4">April</mat-option>
              <mat-option value="5">May</mat-option>
              <mat-option value="6">June</mat-option>
              <mat-option value="7">July</mat-option>
              <mat-option value="8">August</mat-option>
              <mat-option value="9">September</mat-option>
              <mat-option value="10">October</mat-option>
              <mat-option value="11">November</mat-option>
              <mat-option value="12">December</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field appearance="outline" class="w-28">
            <mat-label>Year</mat-label>
            <mat-select [(value)]="selectedYear">
              <mat-option [value]="2024">2024</mat-option>
              <mat-option [value]="2025">2025</mat-option>
              <mat-option [value]="2026">2026</mat-option>
            </mat-select>
          </mat-form-field>
          <button mat-flat-button color="primary" (click)="generatePayroll()">
            <mat-icon>play_arrow</mat-icon>
            Generate Payroll
          </button>
          <button mat-stroked-button color="accent" (click)="bulkApprove()">
            <mat-icon>checklist</mat-icon>
            Bulk Approve
          </button>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table mat-table [dataSource]="payrollRecords()" class="w-full">
          <ng-container matColumnDef="employeeName">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Employee</th>
            <td mat-cell *matCellDef="let r">{{ r.employeeName }}</td>
          </ng-container>

          <ng-container matColumnDef="basicSalary">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Basic Salary</th>
            <td mat-cell *matCellDef="let r">{{ r.basicSalary | currency }}</td>
          </ng-container>

          <ng-container matColumnDef="allowances">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Allowances</th>
            <td mat-cell *matCellDef="let r" class="text-green-600">+{{ r.allowances | currency }}</td>
          </ng-container>

          <ng-container matColumnDef="deductions">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Deductions</th>
            <td mat-cell *matCellDef="let r" class="text-red-600">-{{ r.deductions | currency }}</td>
          </ng-container>

          <ng-container matColumnDef="netPay">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Net Pay</th>
            <td mat-cell *matCellDef="let r" class="font-bold">{{ r.netPay | currency }}</td>
          </ng-container>

          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
            <td mat-cell *matCellDef="let r">
              <span class="px-2 py-0.5 rounded-full text-xs font-semibold"
                    [class.bg-gray-100]="r.status === 'DRAFT'" [class.text-gray-700]="r.status === 'DRAFT'"
                    [class.bg-green-100]="r.status === 'APPROVED'" [class.text-green-800]="r.status === 'APPROVED'"
                    [class.bg-blue-100]="r.status === 'PAID'" [class.text-blue-800]="r.status === 'PAID'">
                {{ r.status | titlecase }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600 w-20">Actions</th>
            <td mat-cell *matCellDef="let r">
              <button mat-icon-button color="primary" aria-label="View payroll record">
                <mat-icon>visibility</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
        </table>

        @if (payrollRecords().length === 0) {
          <div class="text-center py-12 text-gray-500">
            <mat-icon class="text-4xl mb-2">payments</mat-icon>
            <p>No payroll records for this period</p>
            <p class="text-sm mt-1">Click "Generate Payroll" to create records</p>
          </div>
        }

        <mat-paginator [length]="totalElements()" [pageSize]="20" [pageSizeOptions]="[10, 20, 50]" (page)="onPageChange($event)" class="border-t" />
      </div>
    </div>
  `
})
export class PayrollComponent {
  selectedMonth = signal((new Date().getMonth() + 1).toString());
  selectedYear = signal(new Date().getFullYear().toString());
  payrollRecords = signal<PayrollRecordExtended[]>([]);
  loading = signal(false);
  totalElements = signal(0);
  displayedColumns = ['employeeName', 'basicSalary', 'allowances', 'deductions', 'netPay', 'status', 'actions'];

  generatePayroll(): void {
    console.log('Generate payroll for', this.selectedMonth(), '/', this.selectedYear());
  }

  bulkApprove(): void {
    console.log('Bulk approve payroll records');
  }

  onPageChange(event: any): void {
    console.log('Page change:', event);
  }
}
