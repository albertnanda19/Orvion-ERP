import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { JournalEntry } from '../../../core/models';

@Component({
  selector: 'app-journal-entry-list',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule,
    MatFormFieldModule, MatSelectModule, MatButtonModule,
    MatIconModule, MatInputModule,
  ],
  styles: [`
    :host { display: block; padding: 1.5rem; }
    .badge { display: inline-block; padding: 0.125rem 0.625rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.025em; }
    .badge-draft { background-color: #e5e7eb; color: #374151; }
    .badge-posted { background-color: #d1fae5; color: #065f46; }
    .filters { display: flex; gap: 1rem; flex-wrap: wrap; margin-bottom: 1rem; align-items: baseline; }
    .mat-mdc-row:hover { background-color: #f9fafb; }
  `],
  template: `
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 m-0">Journal Entries</h1>
      <button mat-flat-button color="primary">
        <mat-icon>add</mat-icon> New Entry
      </button>
    </div>

    <div class="filters">
      <mat-form-field appearance="outline" class="min-w-[160px]">
        <mat-label>Month</mat-label>
        <mat-select (selectionChange)="monthFilter.set($any($event).value)">
          <mat-option value="">All</mat-option>
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
      <mat-form-field appearance="outline" class="min-w-[140px]">
        <mat-label>Year</mat-label>
        <mat-select (selectionChange)="yearFilter.set($any($event).value)">
          <mat-option value="">All</mat-option>
          <mat-option value="2025">2025</mat-option>
          <mat-option value="2026">2026</mat-option>
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline" class="flex-1 min-w-[200px]">
        <mat-label>Search entries</mat-label>
        <input matInput (input)="search.set($any($event.target).value)" placeholder="Entry number, description...">
        <mat-icon matPrefix>search</mat-icon>
      </mat-form-field>
    </div>

    @if (loading()) {
      <div class="flex justify-center py-16">
        <div class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    } @else {
      <table mat-table [dataSource]="entries()" class="w-full bg-transparent">
        <ng-container matColumnDef="entryNumber">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Entry Number</th>
          <td mat-cell *matCellDef="let e" class="font-medium text-gray-900">{{ e.entryNumber }}</td>
        </ng-container>
        <ng-container matColumnDef="description">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Description</th>
          <td mat-cell *matCellDef="let e">{{ e.description }}</td>
        </ng-container>
        <ng-container matColumnDef="entryDate">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Date</th>
          <td mat-cell *matCellDef="let e">{{ e.entryDate | date:'mediumDate' }}</td>
        </ng-container>
        <ng-container matColumnDef="totalDebit">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Total Debit</th>
          <td mat-cell *matCellDef="let e" class="font-medium">{{ e.totalDebit | number:'1.2-2' }}</td>
        </ng-container>
        <ng-container matColumnDef="totalCredit">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Total Credit</th>
          <td mat-cell *matCellDef="let e" class="font-medium">{{ e.totalCredit | number:'1.2-2' }}</td>
        </ng-container>
        <ng-container matColumnDef="status">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
          <td mat-cell *matCellDef="let e">
            <span class="badge"
              [class.badge-draft]="e.status === 'DRAFT'"
              [class.badge-posted]="e.status === 'POSTED'">{{ e.status }}</span>
          </td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Actions</th>
          <td mat-cell *matCellDef="let e">
            <button mat-icon-button>
              <mat-icon>visibility</mat-icon>
            </button>
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
export class JournalEntryListComponent implements OnInit {
  protected readonly entries = signal<JournalEntry[]>([]);
  protected readonly loading = signal(false);
  protected readonly totalElements = signal(0);
  protected readonly search = signal('');
  protected readonly monthFilter = signal('');
  protected readonly yearFilter = signal('');
  protected readonly displayedColumns = ['entryNumber', 'description', 'entryDate', 'totalDebit', 'totalCredit', 'status', 'actions'];

  private page = 0;
  private size = 20;

  ngOnInit(): void {
    this.loadEntries();
  }

  private loadEntries(): void {
    this.loading.set(true);
    setTimeout(() => {
      this.entries.set([]);
      this.totalElements.set(0);
      this.loading.set(false);
    }, 300);
  }

  protected onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.loadEntries();
  }
}
