import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

interface StockAtLocation {
  product: string;
  sku: string;
  quantity: number;
  location: string;
  lastCounted: string;
}

interface WarehouseLocation {
  id: string;
  code: string;
  name: string;
  type: string;
}

@Component({
  selector: 'app-warehouse',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Warehouse Management</h1>
        <div class="flex gap-2">
          <button mat-stroked-button color="primary">
            <mat-icon>add_location</mat-icon>
            Add Location
          </button>
          <button mat-flat-button color="primary">
            <mat-icon>add</mat-icon>
            Add Stock
          </button>
        </div>
      </div>

      <div class="grid grid-cols-12 gap-6">
        <div class="col-span-4">
          <mat-card>
            <mat-card-header class="pb-2">
              <mat-card-title class="text-lg font-semibold">Locations</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              @if (locations().length === 0) {
                <div class="text-center py-8 text-gray-400">
                  <mat-icon class="text-3xl mb-2">warehouse</mat-icon>
                  <p class="text-sm">No locations configured</p>
                </div>
              } @else {
                <div class="space-y-2">
                  @for (loc of locations(); track loc.id) {
                    <div class="flex items-center gap-3 p-3 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors" [class.bg-blue-50]="selectedLocation() === loc.id">
                      <mat-icon class="text-gray-400">inventory</mat-icon>
                      <div>
                        <div class="font-medium text-sm">{{ loc.name }}</div>
                        <div class="text-xs text-gray-500">{{ loc.code }} · {{ loc.type }}</div>
                      </div>
                    </div>
                  }
                </div>
              }
            </mat-card-content>
          </mat-card>
        </div>

        <div class="col-span-8">
          <mat-card>
            <mat-card-header class="pb-2">
              <mat-card-title class="text-lg font-semibold">Stock at Location</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <table mat-table [dataSource]="stockAtLocation()" class="w-full">
                <ng-container matColumnDef="product">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Product</th>
                  <td mat-cell *matCellDef="let s">{{ s.product }}</td>
                </ng-container>

                <ng-container matColumnDef="sku">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">SKU</th>
                  <td mat-cell *matCellDef="let s" class="font-mono text-sm">{{ s.sku }}</td>
                </ng-container>

                <ng-container matColumnDef="quantity">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Quantity</th>
                  <td mat-cell *matCellDef="let s">
                    <span class="font-semibold" [class.text-green-600]="s.quantity > 50" [class.text-yellow-600]="s.quantity >= 10 && s.quantity <= 50" [class.text-red-600]="s.quantity < 10">
                      {{ s.quantity }}
                    </span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="location">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Location</th>
                  <td mat-cell *matCellDef="let s">{{ s.location }}</td>
                </ng-container>

                <ng-container matColumnDef="lastCounted">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Last Counted</th>
                  <td mat-cell *matCellDef="let s">{{ s.lastCounted }}</td>
                </ng-container>

                <tr mat-header-row *matHeaderRowDef="stockColumns"></tr>
                <tr mat-row *matRowDef="let row; columns: stockColumns;" class="hover:bg-gray-50"></tr>
              </table>

              @if (stockAtLocation().length === 0) {
                <div class="text-center py-12 text-gray-500">
                  <mat-icon class="text-4xl mb-2">shelves</mat-icon>
                  <p>Select a location to view stock</p>
                </div>
              }
            </mat-card-content>
          </mat-card>
        </div>
      </div>
    </div>
  `
})
export class WarehouseComponent {
  locations = signal<WarehouseLocation[]>([]);
  selectedLocation = signal<string | null>(null);
  stockAtLocation = signal<StockAtLocation[]>([]);
  stockColumns = ['product', 'sku', 'quantity', 'location', 'lastCounted'];
}
