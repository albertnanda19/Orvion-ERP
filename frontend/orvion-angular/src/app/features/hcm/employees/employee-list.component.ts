import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Employee } from '../../../core/models';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Employees</h1>
        <button mat-flat-button color="primary">
          <mat-icon>person_add</mat-icon>
          Add Employee
        </button>
      </div>

      <div class="flex gap-4 mb-4">
        <mat-form-field appearance="outline" class="flex-1">
          <mat-label>Search employees</mat-label>
          <input matInput (input)="onSearch($event)" placeholder="Search by name, employee#, or position" />
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-48">
          <mat-label>Department</mat-label>
          <mat-select (selectionChange)="onDepartmentChange($event.value)">
            <mat-option value="">All Departments</mat-option>
            <mat-option value="Engineering">Engineering</mat-option>
            <mat-option value="Finance">Finance</mat-option>
            <mat-option value="Human Resources">Human Resources</mat-option>
            <mat-option value="Marketing">Marketing</mat-option>
            <mat-option value="Operations">Operations</mat-option>
            <mat-option value="Sales">Sales</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-36">
          <mat-label>Status</mat-label>
          <mat-select (selectionChange)="onStatusChange($event.value)">
            <mat-option value="">All</mat-option>
            <mat-option value="ACTIVE">Active</mat-option>
            <mat-option value="ON_LEAVE">On Leave</mat-option>
            <mat-option value="TERMINATED">Terminated</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table mat-table [dataSource]="employees()" class="w-full">
          <ng-container matColumnDef="employeeNo">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Employee#</th>
            <td mat-cell *matCellDef="let e" class="font-mono text-sm text-blue-600 font-medium">{{ e.employeeNo }}</td>
          </ng-container>

          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Name</th>
            <td mat-cell *matCellDef="let e">{{ e.firstName }} {{ e.lastName }}</td>
          </ng-container>

          <ng-container matColumnDef="department">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Department</th>
            <td mat-cell *matCellDef="let e">{{ e.department }}</td>
          </ng-container>

          <ng-container matColumnDef="position">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Position</th>
            <td mat-cell *matCellDef="let e">{{ e.position }}</td>
          </ng-container>

          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
            <td mat-cell *matCellDef="let e">
              <span class="px-2 py-0.5 rounded-full text-xs font-semibold"
                    [class.bg-green-100]="e.status === 'ACTIVE'" [class.text-green-800]="e.status === 'ACTIVE'"
                    [class.bg-yellow-100]="e.status === 'ON_LEAVE'" [class.text-yellow-800]="e.status === 'ON_LEAVE'"
                    [class.bg-red-100]="e.status === 'TERMINATED'" [class.text-red-800]="e.status === 'TERMINATED'">
                {{ e.status }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600 w-20">Actions</th>
            <td mat-cell *matCellDef="let e">
              <button mat-icon-button color="primary" aria-label="View employee">
                <mat-icon>visibility</mat-icon>
              </button>
              <button mat-icon-button color="accent" aria-label="Edit employee">
                <mat-icon>edit</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
        </table>

        @if (employees().length === 0) {
          <div class="text-center py-12 text-gray-500">
            <mat-icon class="text-4xl mb-2">people</mat-icon>
            <p>No employees found</p>
          </div>
        }

        <mat-paginator [length]="totalElements()" [pageSize]="20" [pageSizeOptions]="[10, 20, 50]" (page)="onPageChange($event)" class="border-t" />
      </div>
    </div>
  `
})
export class EmployeeListComponent {
  employees = signal<Employee[]>([]);
  loading = signal(false);
  totalElements = signal(0);
  displayedColumns = ['employeeNo', 'name', 'department', 'position', 'status', 'actions'];

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    console.log('Search employees:', value);
  }

  onDepartmentChange(department: string): void {
    console.log('Filter by department:', department);
  }

  onStatusChange(status: string): void {
    console.log('Filter by status:', status);
  }

  onPageChange(event: any): void {
    console.log('Page change:', event);
  }
}
