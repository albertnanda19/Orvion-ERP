import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { LeaveRequest } from '../../../core/models';

interface LeaveRequestExtended extends LeaveRequest {
  employeeName: string;
  duration: number;
}

@Component({
  selector: 'app-leave-request-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule, MatButtonToggleModule],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Leave Requests</h1>
        <div class="flex items-center gap-3">
          <mat-button-toggle-group [(value)]="viewMode" (change)="onViewModeChange($event.value)">
            <mat-button-toggle value="table">
              <mat-icon>table_rows</mat-icon>
              Table
            </mat-button-toggle>
            <mat-button-toggle value="calendar">
              <mat-icon>calendar_month</mat-icon>
              Calendar
            </mat-button-toggle>
          </mat-button-toggle-group>
          <button mat-flat-button color="primary">
            <mat-icon>add</mat-icon>
            New Request
          </button>
        </div>
      </div>

      <div class="flex gap-4 mb-4">
        <mat-form-field appearance="outline" class="w-44">
          <mat-label>Leave Type</mat-label>
          <mat-select (selectionChange)="onTypeChange($event.value)">
            <mat-option value="">All Types</mat-option>
            <mat-option value="ANNUAL">Annual Leave</mat-option>
            <mat-option value="SICK">Sick Leave</mat-option>
            <mat-option value="PERSONAL">Personal Leave</mat-option>
            <mat-option value="MATERNITY">Maternity Leave</mat-option>
            <mat-option value="PATERNITY">Paternity Leave</mat-option>
            <mat-option value="UNPAID">Unpaid Leave</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="w-36">
          <mat-label>Status</mat-label>
          <mat-select (selectionChange)="onStatusChange($event.value)">
            <mat-option value="">All</mat-option>
            <mat-option value="PENDING">Pending</mat-option>
            <mat-option value="APPROVED">Approved</mat-option>
            <mat-option value="REJECTED">Rejected</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      @if (viewMode() === 'table') {
        <div class="bg-white rounded-lg shadow overflow-hidden">
          <table mat-table [dataSource]="leaveRequests()" class="w-full">
            <ng-container matColumnDef="employeeName">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Employee</th>
              <td mat-cell *matCellDef="let l">{{ l.employeeName }}</td>
            </ng-container>

            <ng-container matColumnDef="type">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Leave Type</th>
              <td mat-cell *matCellDef="let l">
                <span class="px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700">{{ l.type | titlecase }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="startDate">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Start Date</th>
              <td mat-cell *matCellDef="let l">{{ l.startDate | date:'mediumDate' }}</td>
            </ng-container>

            <ng-container matColumnDef="endDate">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">End Date</th>
              <td mat-cell *matCellDef="let l">{{ l.endDate | date:'mediumDate' }}</td>
            </ng-container>

            <ng-container matColumnDef="duration">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Duration</th>
              <td mat-cell *matCellDef="let l">{{ l.duration }} day{{ l.duration !== 1 ? 's' : '' }}</td>
            </ng-container>

            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600">Status</th>
              <td mat-cell *matCellDef="let l">
                <span class="px-2 py-0.5 rounded-full text-xs font-semibold"
                      [class.bg-yellow-100]="l.status === 'PENDING'" [class.text-yellow-800]="l.status === 'PENDING'"
                      [class.bg-green-100]="l.status === 'APPROVED'" [class.text-green-800]="l.status === 'APPROVED'"
                      [class.bg-red-100]="l.status === 'REJECTED'" [class.text-red-800]="l.status === 'REJECTED'">
                  {{ l.status | titlecase }}
                </span>
              </td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="font-semibold text-gray-600 w-20">Actions</th>
              <td mat-cell *matCellDef="let l">
                <button mat-icon-button color="primary" aria-label="View leave request">
                  <mat-icon>visibility</mat-icon>
                </button>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50"></tr>
          </table>

          @if (leaveRequests().length === 0) {
            <div class="text-center py-12 text-gray-500">
              <mat-icon class="text-4xl mb-2">event_busy</mat-icon>
              <p>No leave requests found</p>
            </div>
          }
        </div>
      } @else {
        <div class="bg-white rounded-lg shadow p-12">
          <div class="text-center text-gray-400">
            <mat-icon class="text-6xl mb-4">calendar_month</mat-icon>
            <h3 class="text-lg font-semibold text-gray-500 mb-2">Calendar View</h3>
            <p>Interactive calendar view coming soon</p>
          </div>
        </div>
      }
    </div>
  `
})
export class LeaveRequestListComponent {
  viewMode = signal<'table' | 'calendar'>('table');
  leaveRequests = signal<LeaveRequestExtended[]>([]);
  displayedColumns = ['employeeName', 'type', 'startDate', 'endDate', 'duration', 'status', 'actions'];

  onViewModeChange(mode: string): void {
    console.log('View mode changed:', mode);
  }

  onTypeChange(type: string): void {
    console.log('Filter by type:', type);
  }

  onStatusChange(status: string): void {
    console.log('Filter by status:', status);
  }
}
