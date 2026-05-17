import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { OrvionStatusBadgeComponent } from '../../../shared/components/status-badge/orvion-status-badge.component';

interface Lead {
  id: string;
  companyName: string;
  contactName: string;
  email: string;
  phone: string;
  status: string;
  priority: string;
  createdDate: string;
}

@Component({
  selector: 'app-lead-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatButtonToggleModule,
    MatChipsModule,
    OrvionStatusBadgeComponent,
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h1 class="text-2xl font-semibold text-gray-900">Leads</h1>
        <mat-button-toggle-group
          [value]="viewMode()"
          (change)="viewMode.set($event.value)"
          class="!border !border-gray-200 !rounded-lg"
          hideSingleSelectionIndicator
        >
          <mat-button-toggle value="kanban" class="!text-sm">
            <mat-icon class="!text-lg !mr-1">dashboard</mat-icon>
            Kanban
          </mat-button-toggle>
          <mat-button-toggle value="table" class="!text-sm">
            <mat-icon class="!text-lg !mr-1">table_rows</mat-icon>
            Table
          </mat-button-toggle>
        </mat-button-toggle-group>
      </div>

      @if (viewMode() === 'kanban') {
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 xl:grid-cols-7 gap-4">
          @for (col of kanbanColumns; track col.status) {
            <div class="bg-gray-50 rounded-xl p-3">
              <div class="flex items-center justify-between mb-3">
                <h3 class="font-semibold text-gray-700 text-xs uppercase tracking-wider">{{ col.label }}</h3>
                <span class="text-xs font-medium text-gray-400 bg-white px-2 py-0.5 rounded-full">{{ leadsByStatus(col.status).length }}</span>
              </div>
              <div class="space-y-2 min-h-[150px]">
                @for (lead of leadsByStatus(col.status); track lead.id) {
                  <div class="bg-white rounded-lg border border-gray-200 p-3 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
                    <p class="font-medium text-gray-900 text-sm">{{ lead.companyName }}</p>
                    <p class="text-xs text-gray-500 mt-0.5">{{ lead.contactName }}</p>
                    <div class="flex items-center justify-between mt-2">
                      <orvion-status-badge [status]="lead.status" />
                      <span
                        class="text-xs font-medium px-1.5 py-0.5 rounded"
                        [class.text-red-700]="lead.priority === 'HIGH'"
                        [class.text-yellow-700]="lead.priority === 'MEDIUM'"
                        [class.text-green-700]="lead.priority === 'LOW'"
                      >
                        {{ lead.priority }}
                      </span>
                    </div>
                  </div>
                }
              </div>
            </div>
          }
        </div>
      } @else {
        <mat-card class="rounded-xl overflow-hidden">
          <div class="overflow-x-auto">
            <table mat-table [dataSource]="leads()" class="w-full">

              <ng-container matColumnDef="companyName">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Company</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !font-medium !text-gray-900">{{ row.companyName }}</td>
              </ng-container>

              <ng-container matColumnDef="contactName">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Contact</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.contactName }}</td>
              </ng-container>

              <ng-container matColumnDef="email">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Email</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.email }}</td>
              </ng-container>

              <ng-container matColumnDef="phone">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Phone</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.phone }}</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Status</th>
                <td mat-cell *matCellDef="let row">
                  <orvion-status-badge [status]="row.status" />
                </td>
              </ng-container>

              <ng-container matColumnDef="priority">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Priority</th>
                <td mat-cell *matCellDef="let row">
                  <span
                    class="text-xs font-medium px-2 py-0.5 rounded"
                    [class.bg-red-100]="row.priority === 'HIGH'"
                    [class.text-red-700]="row.priority === 'HIGH'"
                    [class.bg-yellow-100]="row.priority === 'MEDIUM'"
                    [class.text-yellow-700]="row.priority === 'MEDIUM'"
                    [class.bg-green-100]="row.priority === 'LOW'"
                    [class.text-green-700]="row.priority === 'LOW'"
                  >
                    {{ row.priority }}
                  </span>
                </td>
              </ng-container>

              <ng-container matColumnDef="createdDate">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Created Date</th>
                <td mat-cell *matCellDef="let row" class="!text-sm !text-gray-600">{{ row.createdDate | date:'mediumDate' }}</td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="!font-semibold !text-gray-700 !text-xs !uppercase !tracking-wider !bg-gray-50">Actions</th>
                <td mat-cell *matCellDef="let row">
                  <button mat-icon-button color="primary" (click)="viewLead(row)" matTooltip="View">
                    <mat-icon class="text-sm">visibility</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="tableColumns" class="!border-b !border-gray-200"></tr>
              <tr mat-row *matRowDef="let row; columns: tableColumns;" class="!border-b !border-gray-100 hover:!bg-gray-50 transition-colors"></tr>
            </table>
          </div>
        </mat-card>
      }
    </div>
  `,
})
export class LeadListComponent {
  viewMode = signal<'kanban' | 'table'>('kanban');

  tableColumns: string[] = ['companyName', 'contactName', 'email', 'phone', 'status', 'priority', 'createdDate', 'actions'];

  kanbanColumns = [
    { status: 'NEW', label: 'New' },
    { status: 'CONTACTED', label: 'Contacted' },
    { status: 'QUALIFIED', label: 'Qualified' },
    { status: 'PROPOSAL', label: 'Proposal' },
    { status: 'NEGOTIATION', label: 'Negotiation' },
    { status: 'CLOSED_WON', label: 'Won' },
    { status: 'CLOSED_LOST', label: 'Lost' },
  ];

  leads = signal<Lead[]>([
    { id: '1', companyName: 'Acme Corp', contactName: 'John Smith', email: 'john@acme.com', phone: '+1-555-0101', status: 'NEW', priority: 'HIGH', createdDate: '2026-05-10' },
    { id: '2', companyName: 'Globex Inc', contactName: 'Jane Doe', email: 'jane@globex.com', phone: '+1-555-0102', status: 'CONTACTED', priority: 'MEDIUM', createdDate: '2026-05-12' },
    { id: '3', companyName: 'Initech', contactName: 'Bob Johnson', email: 'bob@initech.com', phone: '+1-555-0103', status: 'QUALIFIED', priority: 'HIGH', createdDate: '2026-05-08' },
    { id: '4', companyName: 'Hooli', contactName: 'Alice Brown', email: 'alice@hooli.com', phone: '+1-555-0104', status: 'PROPOSAL', priority: 'MEDIUM', createdDate: '2026-05-05' },
    { id: '5', companyName: 'Wayne Enterprises', contactName: 'Bruce Wayne', email: 'bruce@wayne.com', phone: '+1-555-0105', status: 'NEGOTIATION', priority: 'HIGH', createdDate: '2026-04-28' },
    { id: '6', companyName: 'Stark Industries', contactName: 'Tony Stark', email: 'tony@stark.com', phone: '+1-555-0106', status: 'CLOSED_WON', priority: 'HIGH', createdDate: '2026-04-15' },
    { id: '7', companyName: 'Umbrella Corp', contactName: 'Jill Valentine', email: 'jill@umbrella.com', phone: '+1-555-0107', status: 'CLOSED_LOST', priority: 'LOW', createdDate: '2026-04-20' },
    { id: '8', companyName: 'Cyberdyne Systems', contactName: 'Sarah Connor', email: 'sarah@cyberdyne.com', phone: '+1-555-0108', status: 'CONTACTED', priority: 'MEDIUM', createdDate: '2026-05-14' },
  ]);

  leadsByStatus(status: string): Lead[] {
    return this.leads().filter(l => l.status === status);
  }

  viewLead(lead: Lead): void {
    console.log('View lead:', lead.id);
  }
}
