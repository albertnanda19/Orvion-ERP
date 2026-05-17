import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TableColumn } from '../../../core/models';

@Component({
  selector: 'orvion-table',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatProgressBarModule,
  ],
  template: `
    @if (loading()) {
      <mat-progress-bar mode="indeterminate" class="!fixed !top-0 !left-0 !z-50" />
    }

    <div class="mat-elevation-z0 overflow-hidden rounded-lg border border-gray-200 bg-white">
      <div class="overflow-x-auto">
        <table mat-table [dataSource]="data()" matSort (matSortChange)="onSortChange($event)" class="w-full">

          @for (col of columns(); track col.key) {
            <ng-container [matColumnDef]="col.key">
              <th mat-header-cell *matHeaderCellDef mat-sort-header [style.width]="col.width || 'auto'" [disabled]="!col.sortable" class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">
                {{ col.label }}
              </th>
              <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">
                @switch (col.type) {
                  @case ('currency') {
                    {{ row[col.key] | currency }}
                  }
                  @case ('date') {
                    {{ row[col.key] | date:'mediumDate' }}
                  }
                  @case ('number') {
                    {{ row[col.key] | number }}
                  }
                  @default {
                    {{ row[col.key] }}
                  }
                }
              </td>
            </ng-container>
          }

          <tr mat-header-row *matHeaderRowDef="displayedColumns()" class="!border-b !border-gray-200"></tr>
          <tr
            mat-row
            *matRowDef="let row; columns: displayedColumns();"
            class="!border-b !border-gray-100 hover:!bg-gray-50 cursor-pointer transition-colors"
            (click)="onRowClick(row)"
          ></tr>
        </table>
      </div>

      @if (!loading() && data().length === 0) {
        <div class="flex flex-col items-center justify-center py-12 text-gray-400">
          <span class="text-4xl mb-2">inbox</span>
          <p class="text-sm">No data available</p>
        </div>
      }

      <mat-paginator
        [length]="totalElements()"
        [pageSize]="pageSize()"
        [pageSizeOptions]="[5, 10, 25, 50]"
        (page)="onPageChange($event)"
        class="!border-t !border-gray-200"
        showFirstLastButtons
      />
    </div>
  `,
})
export class OrvionTableComponent {
  columns = input.required<TableColumn[]>();
  data = input.required<any[]>();
  loading = input(false);
  totalElements = input(0);
  pageSize = input(10);

  pageChange = output<PageEvent>();
  sortChange = output<Sort>();
  rowClick = output<any>();

  displayedColumns = () => this.columns().map(c => c.key);

  onPageChange(event: PageEvent): void {
    this.pageChange.emit(event);
  }

  onSortChange(sort: Sort): void {
    this.sortChange.emit(sort);
  }

  onRowClick(row: any): void {
    this.rowClick.emit(row);
  }
}
