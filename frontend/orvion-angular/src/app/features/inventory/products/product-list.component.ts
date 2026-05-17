import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Product } from '../../../core/models';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Products</h1>
        <button mat-flat-button color="primary" class="flex items-center gap-2">
          <mat-icon>add</mat-icon>
          Add Product
        </button>
      </div>

      <div class="flex gap-4 mb-4">
        <mat-form-field appearance="outline" class="flex-1">
          <mat-label>Search products</mat-label>
          <input matInput (input)="onSearch($event)" placeholder="Search by SKU or name" />
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-48">
          <mat-label>Category</mat-label>
          <mat-select (selectionChange)="onCategoryChange($event.value)">
            <mat-option value="">All Categories</mat-option>
            <mat-option value="Raw Materials">Raw Materials</mat-option>
            <mat-option value="Finished Goods">Finished Goods</mat-option>
            <mat-option value="Packaging">Packaging</mat-option>
            <mat-option value="Supplies">Supplies</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      @if (loading()) {
        <div class="flex justify-center py-12">
          <mat-spinner diameter="40" />
        </div>
      } @else {
        <div class="bg-white rounded-lg shadow overflow-hidden">
          <table mat-table [dataSource]="products()" class="w-full">
            <ng-container matColumnDef="sku">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">SKU</th>
              <td mat-cell *matCellDef="let p" class="font-mono text-sm">{{ p.sku }}</td>
            </ng-container>

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Product Name</th>
              <td mat-cell *matCellDef="let p">{{ p.name }}</td>
            </ng-container>

            <ng-container matColumnDef="category">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Category</th>
              <td mat-cell *matCellDef="let p">
                <span class="px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700">{{ p.category }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="stockLevel">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Stock Level</th>
              <td mat-cell *matCellDef="let p">
                <span class="font-semibold" [class.text-green-600]="p.stockLevel > 50" [class.text-yellow-600]="p.stockLevel >= 10 && p.stockLevel <= 50" [class.text-red-600]="p.stockLevel < 10">
                  {{ p.stockLevel }}
                </span>
              </td>
            </ng-container>

            <ng-container matColumnDef="unitPrice">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Unit Price</th>
              <td mat-cell *matCellDef="let p">{{ p.unitPrice | currency }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600 w-20">Actions</th>
              <td mat-cell *matCellDef="let p">
                <button mat-icon-button color="primary" aria-label="View product">
                  <mat-icon>visibility</mat-icon>
                </button>
                <button mat-icon-button color="warn" aria-label="Delete product">
                  <mat-icon>delete</mat-icon>
                </button>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
          </table>

          @if (products().length === 0) {
            <div class="text-center py-12 text-gray-500">
              <mat-icon class="text-4xl mb-2">inventory_2</mat-icon>
              <p>No products found</p>
            </div>
          }

          <mat-paginator [length]="totalElements()" [pageSize]="20" [pageSizeOptions]="[10, 20, 50]" (page)="onPageChange($event)" class="border-t" />
        </div>
      }
    </div>
  `
})
export class ProductListComponent {
  products = signal<Product[]>([]);
  loading = signal(false);
  totalElements = signal(0);
  displayedColumns = ['sku', 'name', 'category', 'stockLevel', 'unitPrice', 'actions'];

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    console.log('Search products:', value);
  }

  onCategoryChange(category: string): void {
    console.log('Filter by category:', category);
  }

  onPageChange(event: any): void {
    console.log('Page change:', event);
  }
}
