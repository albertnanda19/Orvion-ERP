import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { CreateInvoiceRequest } from '../../../core/models';

@Component({
  selector: 'app-invoice-create',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatStepperModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatDatepickerModule, RouterLink,
  ],
  styles: [`
    :host { display: block; padding: 1.5rem; max-width: 960px; }
    .line-item-row { display: flex; gap: 0.75rem; align-items: flex-start; margin-bottom: 0.75rem; }
    .line-item-row mat-form-field { flex: 1; }
    .line-item-row .qty-field { max-width: 120px; }
    .line-item-row .price-field { max-width: 140px; }
    .line-item-row .total-field { max-width: 140px; }
    .review-section { margin-bottom: 1.5rem; }
    .review-section h3 { font-size: 1rem; font-weight: 600; color: #374151; margin-bottom: 0.5rem; border-bottom: 1px solid #e5e7eb; padding-bottom: 0.375rem; }
    .review-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    .review-item label { font-size: 0.75rem; color: #6b7280; text-transform: uppercase; letter-spacing: 0.05em; }
    .review-item p { font-weight: 500; color: #111827; margin: 0; }
    .review-table { width: 100%; border-collapse: collapse; font-size: 0.875rem; }
    .review-table th { text-align: left; padding: 0.5rem 0.75rem; background: #f9fafb; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb; }
    .review-table td { padding: 0.5rem 0.75rem; border-bottom: 1px solid #f3f4f6; }
    .review-table .grand-total td { font-weight: 700; border-top: 2px solid #d1d5db; }
  `],
  template: `
    <div class="flex items-center gap-4 mb-6">
      <a routerLink="/finance/invoices" class="text-gray-500 hover:text-gray-700">
        <mat-icon>arrow_back</mat-icon>
      </a>
      <h1 class="text-2xl font-bold text-gray-900 m-0">New Invoice</h1>
    </div>

    <mat-stepper linear #stepper>
      <mat-step [stepControl]="headerForm" label="Invoice Header">
        <form [formGroup]="headerForm" class="grid grid-cols-2 gap-4 mt-4">
          <mat-form-field appearance="outline">
            <mat-label>Invoice Type</mat-label>
            <mat-select formControlName="type">
              <mat-option value="RECEIVABLE">Receivable</mat-option>
              <mat-option value="PAYABLE">Payable</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Counterparty</mat-label>
            <input matInput formControlName="counterpartyName" placeholder="Customer or supplier name">
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Currency</mat-label>
            <mat-select formControlName="currency">
              <mat-option value="IDR">IDR</mat-option>
              <mat-option value="USD">USD</mat-option>
              <mat-option value="EUR">EUR</mat-option>
              <mat-option value="SGD">SGD</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Due Date</mat-label>
            <input matInput [matDatepicker]="duePicker" formControlName="dueDate">
            <mat-datepicker-toggle matSuffix [for]="duePicker"></mat-datepicker-toggle>
            <mat-datepicker #duePicker></mat-datepicker>
          </mat-form-field>
          <div class="col-span-2 flex justify-end mt-2">
            <button mat-button matStepperNext [disabled]="headerForm.invalid">Next</button>
          </div>
        </form>
      </mat-step>

      <mat-step [stepControl]="lineItemsForm" label="Line Items">
        <div class="mt-4">
          <div formArrayName="lineItems">
            @for (item of lineItems.controls; track idx; let idx = $index) {
              <div [formGroupName]="idx" class="line-item-row">
                <mat-form-field appearance="outline" class="flex-1">
                  <mat-label>Description</mat-label>
                  <input matInput formControlName="description" placeholder="Item description">
                </mat-form-field>
                <mat-form-field appearance="outline" class="qty-field">
                  <mat-label>Qty</mat-label>
                  <input matInput type="number" formControlName="quantity" (input)="recalcLine(idx)">
                </mat-form-field>
                <mat-form-field appearance="outline" class="price-field">
                  <mat-label>Unit Price</mat-label>
                  <input matInput type="number" formControlName="unitPrice" (input)="recalcLine(idx)">
                </mat-form-field>
                <mat-form-field appearance="outline" class="total-field">
                  <mat-label>Total</mat-label>
                  <input matInput [value]="lineTotals()[idx] | number:'1.2-2'" readonly>
                </mat-form-field>
                @if (lineItems.controls.length > 1) {
                  <button mat-icon-button color="warn" (click)="removeLine(idx)" class="mt-2">
                    <mat-icon>remove_circle</mat-icon>
                  </button>
                }
              </div>
            }
          </div>
          <button mat-stroked-button (click)="addLine()" class="mb-4">
            <mat-icon>add</mat-icon> Add Line Item
          </button>
          <div class="text-right text-lg font-bold text-gray-800 mb-4">
            Grand Total: {{ grandTotal() | number:'1.2-2' }}
          </div>
          <div class="flex justify-between">
            <button mat-button matStepperPrevious>Back</button>
            <button mat-button matStepperNext [disabled]="lineItemsForm.invalid || lineItems.length === 0">Next</button>
          </div>
        </div>
      </mat-step>

      <mat-step label="Review">
        <div class="mt-4">
          <div class="review-section">
            <h3>Invoice Details</h3>
            <div class="review-grid">
              <div class="review-item">
                <label>Type</label>
                <p>{{ headerForm.value.type }}</p>
              </div>
              <div class="review-item">
                <label>Counterparty</label>
                <p>{{ headerForm.value.counterpartyName }}</p>
              </div>
              <div class="review-item">
                <label>Currency</label>
                <p>{{ headerForm.value.currency }}</p>
              </div>
              <div class="review-item">
                <label>Due Date</label>
                <p>{{ headerForm.value.dueDate | date:'mediumDate' }}</p>
              </div>
            </div>
          </div>
          <div class="review-section">
            <h3>Line Items</h3>
            <table class="review-table">
              <thead>
                <tr><th>#</th><th>Description</th><th>Qty</th><th>Unit Price</th><th>Total</th></tr>
              </thead>
              <tbody>
                @for (item of lineItems.controls; track $index) {
                  <tr>
                    <td>{{ $index + 1 }}</td>
                    <td>{{ item.value.description }}</td>
                    <td>{{ item.value.quantity }}</td>
                    <td>{{ item.value.unitPrice | number:'1.2-2' }}</td>
                    <td>{{ lineTotals()[$index] | number:'1.2-2' }}</td>
                  </tr>
                }
                <tr class="grand-total">
                  <td colspan="4" class="text-right">Grand Total</td>
                  <td>{{ grandTotal() | number:'1.2-2' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="flex justify-between mt-6">
            <button mat-button matStepperPrevious>Back</button>
            <button mat-flat-button color="primary" (click)="submit()">Submit Invoice</button>
          </div>
        </div>
      </mat-step>
    </mat-stepper>
  `
})
export class InvoiceCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  protected readonly headerForm: FormGroup = this.fb.group({
    type: ['RECEIVABLE', Validators.required],
    counterpartyName: ['', Validators.required],
    currency: ['IDR', Validators.required],
    dueDate: [null, Validators.required],
  });

  protected readonly lineItemsForm: FormGroup = this.fb.group({
    lineItems: this.fb.array([this.createLineItem()]),
  });

  protected readonly lineTotals = signal<number[]>([0]);

  protected get lineItems(): FormArray {
    return this.lineItemsForm.get('lineItems') as FormArray;
  }

  protected readonly grandTotal = computed(() =>
    this.lineTotals().reduce((sum, t) => sum + t, 0)
  );

  private createLineItem(): FormGroup {
    return this.fb.group({
      description: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: [0, [Validators.required, Validators.min(0)]],
    });
  }

  protected addLine(): void {
    this.lineItems.push(this.createLineItem());
    this.lineTotals.update(t => [...t, 0]);
  }

  protected removeLine(idx: number): void {
    this.lineItems.removeAt(idx);
    this.lineTotals.update(t => t.filter((_, i) => i !== idx));
  }

  protected recalcLine(idx: number): void {
    const group = this.lineItems.at(idx);
    const qty = group.get('quantity')?.value ?? 0;
    const price = group.get('unitPrice')?.value ?? 0;
    this.lineTotals.update(t => {
      const updated = [...t];
      updated[idx] = Number(qty) * Number(price);
      return updated;
    });
  }

  protected submit(): void {
    if (this.headerForm.invalid || this.lineItemsForm.invalid) return;

    const payload: CreateInvoiceRequest = {
      counterpartyId: this.headerForm.value.counterpartyName,
      invoiceType: this.headerForm.value.type,
      currency: this.headerForm.value.currency,
      lineItems: this.lineItems.controls.map((ctrl, i) => ({
        description: ctrl.value.description,
        quantity: Number(ctrl.value.quantity),
        unitPrice: Number(ctrl.value.unitPrice),
        total: this.lineTotals()[i],
      })),
    };
    // financeApi.createInvoice(payload) will be provided by FinanceApiService
    this.router.navigate(['/finance/invoices']);
  }
}
