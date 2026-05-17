import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatCardModule, MatIconModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-2xl font-semibold text-gray-900">Settings</h1>

      <div class="space-y-4">
        <mat-card class="p-6 rounded-xl">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center">
              <mat-icon class="text-blue-600">tune</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">General</h3>
              <p class="text-sm text-gray-500 mt-1">Application preferences, localization, and branding settings</p>
            </div>
          </div>
        </mat-card>

        <mat-card class="p-6 rounded-xl">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-emerald-100 flex items-center justify-center">
              <mat-icon class="text-emerald-600">group</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">Users</h3>
              <p class="text-sm text-gray-500 mt-1">Manage user accounts, invitations, and access</p>
            </div>
          </div>
        </mat-card>

        <mat-card class="p-6 rounded-xl">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-purple-100 flex items-center justify-center">
              <mat-icon class="text-purple-600">security</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">Roles</h3>
              <p class="text-sm text-gray-500 mt-1">Define roles, permissions, and access policies</p>
            </div>
          </div>
        </mat-card>

        <mat-card class="p-6 rounded-xl">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-amber-100 flex items-center justify-center">
              <mat-icon class="text-amber-600">settings_applications</mat-icon>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900">System Configuration</h3>
              <p class="text-sm text-gray-500 mt-1">System-wide configuration, integrations, and audit logs</p>
            </div>
          </div>
        </mat-card>
      </div>
    </div>
  `,
})
export class SettingsComponent {}
