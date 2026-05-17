import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { Invoice } from '../../../core/models';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, MatTableModule, MatPaginatorModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatDatepickerModule,
  ],
  styles: [`
    :host { display: block; padding: 1.5rem; }
    .badge { display: inline-block; padding: 0.125rem 0.625rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.025em; }
    .badge-draft { background-color: #e5e7eb; color: #374151; }
    .badge-pending_approval { background-color: #fef3c7; color: #92400e; }
    .badge-approved { background-color: #d1fae5; color: #065f46; }
    .badge-paid { background-color: #dbeafe; color: #1e40af; }
    .badge-void { background-color: #fee2e2; color: #991b1b; }
    .badge-overdue { background-color: #ffedd5; color: #9a3412; }
    .mat-mdc-row:hover { background-color: #f9fafb; }
  `],
  template: `
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 m-0">Invoices</h1>
      <button mat-flat-button color="primary" routerLink="/finance/invoices/new">
        <mat-icon>add</mat-icon> New Invoice
      </button>
    </div>

    <div class="flex gap-4 flex-wrap items-baseline mb-4">
      <mat-form-field appearance="outline" class="flex-1 min-w-[240px]">
        <mat-label>Search invoices</mat-label>
        <input matInput (input)="search.set($any($event.target).value)" placeholder="Invoice no, counterparty...">
        <mat-icon matPrefix>search</mat-icon>
      </mat-form-field>
      <mat-form-field appearance="outline" class="min-w-[180px]">
        <mat-label>Status</mat-label>
        <mat-select (selectionChange)="statusFilter.set($any($event).value)">
          <mat-option value="">All</mat-option>
          <mat-option value="DRAFT">Draft</mat-option>
          <mat-option value="PENDING_APPROVAL">Pending Approval</mat-option>
          <mat-option value="APPROVED">Approved</mat-option>
          <mat-option value="PAID">Paid</mat-option>
          <mat-option value="VOID">Void</mat-option>
          <mat-option value="OVERDUE">Overdue</mat-option>
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline" class="min-w-[180px]">
        <mat-label>Date from</mat-label>
        <input matInput [matDatepicker]="fromPicker" (dateChange)="dateFrom.set($any($event).value)">
        <mat-datepicker-toggle matSuffix [for]="fromPicker"></mat-datepicker-toggle>
        <mat-datepicker #fromPicker></mat-datepicker>
      </mat-form-field>
      <mat-form-field appearance="outline" class="min-w-[180px]">
        <mat-label>Date to</mat-label>
        <input matInput [matDatepicker]="toPicker" (dateChange)="dateTo.set($any($event).value)">
        <mat-datepicker-toggle matSuffix [for]="toPicker"></mat-datepicker-toggle>
        <mat-datepicker #toPicker></mat-datepicker>
      </mat-form-field>
    </div>

    @if (loading()) {
      <div class="flex justify-center py-16">
        <div class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    } @else {
      <table mat-table [dataSource]="invoices()" class="w-full bg-transparent">
        <ng-container matColumnDef="invoiceNo">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Invoice No</th>
          <td mat-cell *matCellDef="let inv" class="font-medium text-gray-900">{{ inv.invoiceNo }}</td>
        </ng-container>
        <ng-container matColumnDef="type">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Type</th>
          <td mat-cell *matCellDef="let inv">{{ inv.type }}</td>
        </ng-container>
        <ng-container matColumnDef="counterpartyName">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Counterparty</th>
          <td mat-cell *matCellDef="let inv">{{ inv.counterpartyName }}</td>
        </ng-container>
        <ng-container matColumnDef="amount">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Amount</th>
          <td mat-cell *matCellDef="let inv" class="font-medium">{{ inv.amount | number:'1.2-2' }} {{ inv.currency }}</td>
        </ng-container>
        <ng-container matColumnDef="status">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
          <td mat-cell *matCellDef="let inv">
            <span class="badge"
              [class.badge-draft]="inv.status === 'DRAFT'"
              [class.badge-pending_approval]="inv.status === 'PENDING_APPROVAL'"
              [class.badge-approved]="inv.status === 'APPROVED'"
              [class.badge-paid]="inv.status === 'PAID'"
              [class.badge-void]="inv.status === 'VOID'"
              [class.badge-overdue]="inv.status === 'OVERDUE'">{{ inv.status.replace('_', ' ') }}</span>
          </td>
        </ng-container>
        <ng-container matColumnDef="dueDate">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Due Date</th>
          <td mat-cell *matCellDef="let inv">{{ inv.dueDate | date:'mediumDate' }}</td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Actions</th>
          <td mat-cell *matCellDef="let inv">
            <div class="flex gap-1">
              <a mat-icon-button [routerLink]="['/finance/invoices', inv.id]">
                <mat-icon>visibility</mat-icon>
              </a>
              @if (inv.status === 'PENDING_APPROVAL') {
                <button mat-icon-button color="primary" (click)="approve(inv)">
                  <mat-icon>check_circle</mat-icon>
                </button>
              }
            </div>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
        <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
      </table>

      <mat-paginator [length]="totalElements()" [pageSize]="20" [pageSizeOptions]="[10,20,50]"
                     (page)="onPageChange($event)" showFirstLastButtons class="mt-2">
      </mat-paginator>
    }
  `
})
export class InvoiceListComponent implements OnInit {
  protected readonly invoices = signal<Invoice[]>([]);
  protected readonly loading = signal(false);
  protected readonly totalElements = signal(0);
  protected readonly search = signal('');
  protected readonly statusFilter = signal('');
  protected readonly dateFrom = signal<Date | null>(null);
  protected readonly dateTo = signal<Date | null>(null);
  protected readonly displayedColumns = ['invoiceNo', 'type', 'counterpartyName', 'amount', 'status', 'dueDate', 'actions'];

  private page = 0;
  private size = 20;

  ngOnInit(): void {
    this.loadInvoices();
  }

  private loadInvoices(): void {
    this.loading.set(true);
    setTimeout(() => {
      this.invoices.set([]);
      this.totalElements.set(0);
      this.loading.set(false);
    }, 300);
  }

  protected onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.loadInvoices();
  }

  protected approve(inv: Invoice): void {
  }
}
