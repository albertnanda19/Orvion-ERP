import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatTreeModule } from '@angular/material/tree';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { OrvionStatusBadgeComponent } from '../../../shared/components/status-badge/orvion-status-badge.component';

interface BomRow {
  id: string;
  bomId: string;
  productName: string;
  componentsCount: number;
  totalCost: number;
  version: string;
  status: string;
  children?: BomRow[];
}

@Component({
  selector: 'app-bom-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatTreeModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    OrvionStatusBadgeComponent,
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h1 class="text-2xl font-semibold text-gray-900">Bill of Materials</h1>
        <button mat-raised-button color="primary" (click)="createNew()">
          <mat-icon class="mr-1">add</mat-icon>
          Create New BOM
        </button>
      </div>

      <mat-card class="rounded-xl overflow-hidden">
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="boms()" class="w-full">

            <ng-container matColumnDef="bomId">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">BOM ID</th>
              <td mat-cell *matCellDef="let row" class="!text-sm !font-medium !text-gray-900">{{ row.bomId }}</td>
            </ng-container>

            <ng-container matColumnDef="productName">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Product Name</th>
              <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.productName }}</td>
            </ng-container>

            <ng-container matColumnDef="componentsCount">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Components</th>
              <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.componentsCount }}</td>
            </ng-container>

            <ng-container matColumnDef="totalCost">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Total Cost</th>
              <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.totalCost | currency }}</td>
            </ng-container>

            <ng-container matColumnDef="version">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Version</th>
              <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.version }}</td>
            </ng-container>

            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Status</th>
              <td mat-cell *matCellDef="let row">
                <orvion-status-badge [status]="row.status" />
              </td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Actions</th>
              <td mat-cell *matCellDef="let row">
                <div class="flex items-center gap-1">
                  <button mat-icon-button color="primary" (click)="viewBom(row)" matTooltip="View">
                    <mat-icon class="text-sm">visibility</mat-icon>
                  </button>
                  <button mat-icon-button color="accent" (click)="editBom(row)" matTooltip="Edit">
                    <mat-icon class="text-sm">edit</mat-icon>
                  </button>
                </div>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns" class="!border-b !border-gray-200"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="!border-b !border-gray-100 hover:!bg-gray-50 transition-colors"></tr>
          </table>
        </div>
      </mat-card>
    </div>
  `,
})
export class BomListComponent {
  displayedColumns: string[] = ['bomId', 'productName', 'componentsCount', 'totalCost', 'version', 'status', 'actions'];

  boms = signal<BomRow[]>([
    { id: '1', bomId: 'BOM-001', productName: 'Widget Alpha', componentsCount: 12, totalCost: 45.50, version: '1.0', status: 'APPROVED' },
    { id: '2', bomId: 'BOM-002', productName: 'Gadget Beta', componentsCount: 8, totalCost: 32.00, version: '2.1', status: 'ACTIVE' },
    { id: '3', bomId: 'BOM-003', productName: 'Component X', componentsCount: 5, totalCost: 18.75, version: '1.2', status: 'DRAFT' },
    { id: '4', bomId: 'BOM-004', productName: 'Assembly Gamma', componentsCount: 24, totalCost: 128.00, version: '3.0', status: 'APPROVED' },
    { id: '5', bomId: 'BOM-005', productName: 'Part Delta', componentsCount: 3, totalCost: 9.99, version: '1.0', status: 'ACTIVE' },
  ]);

  viewBom(row: BomRow): void {
    console.log('View BOM:', row.id);
  }

  editBom(row: BomRow): void {
    console.log('Edit BOM:', row.id);
  }

  createNew(): void {
    console.log('Create new BOM');
  }
}
