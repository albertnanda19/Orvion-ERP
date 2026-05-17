import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

interface AttendanceRecord {
  id: string;
  employeeName: string;
  employeeNo: string;
  date: string;
  checkIn: string;
  checkOut: string;
  hours: number;
  status: 'PRESENT' | 'LATE' | 'ABSENT' | 'HALF_DAY';
}

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatDatepickerModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Attendance Tracking</h1>
        <div class="flex gap-2">
          <button mat-stroked-button color="primary">
            <mat-icon>download</mat-icon>
            Export
          </button>
          <button mat-flat-button color="primary">
            <mat-icon>edit_note</mat-icon>
            Manual Entry
          </button>
        </div>
      </div>

      <div class="flex gap-4 mb-4 items-start">
        <mat-form-field appearance="outline" class="w-40">
          <mat-label>Date</mat-label>
          <input matInput [matDatepicker]="picker" [value]="selectedDate()" (dateChange)="onDateChange($event.value)" />
          <mat-datepicker-toggle matSvgIcon [for]="picker" />
          <mat-datepicker #picker />
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
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table mat-table [dataSource]="attendanceRecords()" class="w-full">
          <ng-container matColumnDef="employeeName">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Employee</th>
            <td mat-cell *matCellDef="let a">
              <div class="font-medium">{{ a.employeeName }}</div>
              <div class="text-xs text-gray-500 font-mono">{{ a.employeeNo }}</div>
            </td>
          </ng-container>

          <ng-container matColumnDef="date">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Date</th>
            <td mat-cell *matCellDef="let a">{{ a.date | date:'mediumDate' }}</td>
          </ng-container>

          <ng-container matColumnDef="checkIn">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Check In</th>
            <td mat-cell *matCellDef="let a" class="font-mono text-sm">{{ a.checkIn }}</td>
          </ng-container>

          <ng-container matColumnDef="checkOut">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Check Out</th>
            <td mat-cell *matCellDef="let a" class="font-mono text-sm">{{ a.checkOut }}</td>
          </ng-container>

          <ng-container matColumnDef="hours">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Hours</th>
            <td mat-cell *matCellDef="let a" class="font-semibold">{{ a.hours.toFixed(1) }}</td>
          </ng-container>

          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
            <td mat-cell *matCellDef="let a">
              <span class="px-2 py-0.5 rounded-full text-xs font-semibold"
                    [class.bg-green-100]="a.status === 'PRESENT'" [class.text-green-800]="a.status === 'PRESENT'"
                    [class.bg-yellow-100]="a.status === 'LATE'" [class.text-yellow-800]="a.status === 'LATE'"
                    [class.bg-red-100]="a.status === 'ABSENT'" [class.text-red-800]="a.status === 'ABSENT'"
                    [class.bg-blue-100]="a.status === 'HALF_DAY'" [class.text-blue-800]="a.status === 'HALF_DAY'">
                {{ a.status }}
              </span>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
        </table>

        @if (attendanceRecords().length === 0) {
          <div class="text-center py-12 text-gray-500">
            <mat-icon class="text-4xl mb-2">fact_check</mat-icon>
            <p>No attendance records for this date</p>
          </div>
        }
      </div>
    </div>
  `
})
export class AttendanceComponent {
  selectedDate = signal(new Date());
  attendanceRecords = signal<AttendanceRecord[]>([]);
  displayedColumns = ['employeeName', 'date', 'checkIn', 'checkOut', 'hours', 'status'];

  onDateChange(date: Date | null): void {
    if (date) {
      this.selectedDate.set(date);
      console.log('Date changed:', date);
    }
  }

  onDepartmentChange(department: string): void {
    console.log('Filter by department:', department);
  }
}
