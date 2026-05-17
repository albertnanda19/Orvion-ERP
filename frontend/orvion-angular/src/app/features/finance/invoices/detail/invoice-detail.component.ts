import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Invoice, InvoiceLineItem } from '../../../../core/models';

@Component({
  selector: 'app-invoice-detail',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatTableModule,
    MatButtonModule, MatIconModule, MatChipsModule,
  ],
  styles: [`
    :host { display: block; padding: 1.5rem; max-width: 1000px; }
    .header-section { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    .header-left h1 { font-size: 1.5rem; font-weight: 700; color: #111827; margin: 0 0 0.25rem 0; }
    .header-left .subtitle { font-size: 0.875rem; color: #6b7280; }
    .amount-display { text-align: right; }
    .amount-display .amount { font-size: 1.75rem; font-weight: 700; color: #111827; }
    .amount-display .label { font-size: 0.75rem; color: #6b7280; text-transform: uppercase; letter-spacing: 0.05em; }
    .info-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 0.75rem; margin-bottom: 1.5rem; }
    .info-card { padding: 0.75rem 1rem; }
    .info-card label { font-size: 0.75rem; color: #6b7280; text-transform: uppercase; letter-spacing: 0.05em; }
    .info-card p { font-weight: 500; color: #111827; margin: 0.125rem 0 0 0; }
    .badge { display: inline-block; padding: 0.125rem 0.625rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.025em; }
    .badge-draft { background-color: #e5e7eb; color: #374151; }
    .badge-pending_approval { background-color: #fef3c7; color: #92400e; }
    .badge-approved { background-color: #d1fae5; color: #065f46; }
    .badge-paid { background-color: #dbeafe; color: #1e40af; }
    .badge-void { background-color: #fee2e2; color: #991b1b; }
    .badge-overdue { background-color: #ffedd5; color: #9a3412; }
    .section-title { font-size: 1rem; font-weight: 600; color: #374151; margin-bottom: 0.75rem; border-bottom: 1px solid #e5e7eb; padding-bottom: 0.375rem; }
    .action-bar { display: flex; gap: 0.75rem; flex-wrap: wrap; margin-top: 1.5rem; }
    .payment-placeholder { padding: 1rem; background: #f9fafb; border-radius: 0.5rem; }
  `],
  template: `
    <div class="header-section">
      <div class="header-left">
        <h1>{{ invoice().invoiceNo || 'Loading...' }}</h1>
        <div class="subtitle">
          <span class="badge"
            [class.badge-receivable]="invoice().type === 'RECEIVABLE'"
            [class.badge-payable]="invoice().type === 'PAYABLE'"
            class="mr-2">{{ invoice().type }}</span>
          <span class="badge"
            [class.badge-draft]="invoice().status === 'DRAFT'"
            [class.badge-pending_approval]="invoice().status === 'PENDING_APPROVAL'"
            [class.badge-approved]="invoice().status === 'APPROVED'"
            [class.badge-paid]="invoice().status === 'PAID'"
            [class.badge-void]="invoice().status === 'VOID'"
            [class.badge-overdue]="invoice().status === 'OVERDUE'">{{ invoice().status.replace('_', ' ') }}</span>
        </div>
      </div>
      <div class="amount-display">
        <div class="label">Total Amount</div>
        <div class="amount">{{ invoice().amount | number:'1.2-2' }} {{ invoice().currency }}</div>
      </div>
    </div>

    <div class="info-grid">
      <mat-card class="info-card" appearance="outlined">
        <label>Counterparty</label>
        <p>{{ invoice().counterpartyName || '—' }}</p>
      </mat-card>
      <mat-card class="info-card" appearance="outlined">
        <label>Issue Date</label>
        <p>{{ invoice().createdAt | date:'mediumDate' || '—' }}</p>
      </mat-card>
      <mat-card class="info-card" appearance="outlined">
        <label>Due Date</label>
        <p>{{ invoice().dueDate | date:'mediumDate' || '—' }}</p>
      </mat-card>
      <mat-card class="info-card" appearance="outlined">
        <label>Currency</label>
        <p>{{ invoice().currency || '—' }}</p>
      </mat-card>
    </div>

    <div class="section-title">Line Items</div>
    <table mat-table [dataSource]="lineItems()" class="w-full mb-6">
      <ng-container matColumnDef="description">
        <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Description</th>
        <td mat-cell *matCellDef="let item">{{ item.description }}</td>
      </ng-container>
      <ng-container matColumnDef="quantity">
        <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Qty</th>
        <td mat-cell *matCellDef="let item">{{ item.quantity }}</td>
      </ng-container>
      <ng-container matColumnDef="unitPrice">
        <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Unit Price</th>
        <td mat-cell *matCellDef="let item">{{ item.unitPrice | number:'1.2-2' }}</td>
      </ng-container>
      <ng-container matColumnDef="total">
        <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Total</th>
        <td mat-cell *matCellDef="let item" class="font-medium">{{ item.total | number:'1.2-2' }}</td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="lineItemColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: lineItemColumns;"></tr>
    </table>

    <div class="section-title">Payment History</div>
    <div class="payment-placeholder text-gray-500 text-sm mb-6">
      <p class="m-0">No payments recorded yet.</p>
    </div>

    <div class="action-bar">
      @if (invoice().status === 'PENDING_APPROVAL') {
        <button mat-flat-button color="primary" (click)="approve()">
          <mat-icon>check_circle</mat-icon> Approve
        </button>
      }
      @if (invoice().status === 'APPROVED') {
        <button mat-flat-button color="accent" (click)="recordPayment()">
          <mat-icon>payments</mat-icon> Record Payment
        </button>
      }
      @if (invoice().status !== 'PAID' && invoice().status !== 'VOID') {
        <button mat-stroked-button color="warn" (click)="voidInvoice()">
          <mat-icon>block</mat-icon> Void
        </button>
      }
      <button mat-stroked-button (click)="goBack()">
        <mat-icon>arrow_back</mat-icon> Back
      </button>
    </div>
  `
})
export class InvoiceDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly invoice = signal<Invoice>({} as Invoice);
  protected readonly lineItems = signal<InvoiceLineItem[]>([]);
  protected readonly lineItemColumns = ['description', 'quantity', 'unitPrice', 'total'];

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadInvoice(id);
    }
  }

  private loadInvoice(id: string): void {
    // financeApi.getInvoice(id) will be provided by FinanceApiService
    this.invoice.set({
      id,
      invoiceNo: 'INV-001',
      type: 'RECEIVABLE',
      counterpartyName: 'Sample Counterparty',
      amount: 1500000,
      currency: 'IDR',
      status: 'PENDING_APPROVAL',
      dueDate: new Date().toISOString(),
      createdAt: new Date().toISOString(),
    });
    this.lineItems.set([
      { description: 'Consulting Services', quantity: 10, unitPrice: 150000, total: 1500000 },
    ]);
  }

  protected approve(): void {
    // financeApi.approveInvoice(id)
  }

  protected recordPayment(): void {
    // financeApi.recordPayment(id)
  }

  protected voidInvoice(): void {
    // financeApi.voidInvoice(id)
  }

  protected goBack(): void {
    this.router.navigate(['/finance/invoices']);
  }
}
