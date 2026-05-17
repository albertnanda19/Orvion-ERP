import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-reporting',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatIconModule, MatButtonModule],
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-semibold text-gray-900">Reports & Analytics</h1>
        <p class="text-gray-500 mt-1">Executive dashboard, scheduled reports, and custom report builder</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <mat-card class="p-6 rounded-xl hover:shadow-md transition-shadow cursor-pointer" routerLink="/dashboard">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-lg bg-primary-100 flex items-center justify-center">
              <mat-icon class="text-primary-600">dashboard</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">Executive Dashboard</h3>
              <p class="text-sm text-gray-500 mt-1">Real-time KPI overview</p>
            </div>
          </div>
        </mat-card>

        <mat-card class="p-6 rounded-xl hover:shadow-md transition-shadow">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-lg bg-amber-100 flex items-center justify-center">
              <mat-icon class="text-amber-600">schedule</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">Scheduled Reports</h3>
              <p class="text-sm text-gray-500 mt-1">No scheduled reports configured</p>
            </div>
          </div>
        </mat-card>

        <mat-card class="p-6 rounded-xl hover:shadow-md transition-shadow">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-lg bg-emerald-100 flex items-center justify-center">
              <mat-icon class="text-emerald-600">build</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">Custom Report Builder</h3>
              <p class="text-sm text-gray-500 mt-1">Create custom reports</p>
            </div>
          </div>
        </mat-card>
      </div>
    </div>
  `,
})
export class ReportingComponent {}
