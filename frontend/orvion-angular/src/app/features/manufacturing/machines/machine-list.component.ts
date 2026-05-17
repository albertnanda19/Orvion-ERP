import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';

interface Machine {
  id: string;
  code: string;
  name: string;
  status: 'RUNNING' | 'IDLE' | 'MAINTENANCE' | 'DOWN';
  utilization: number;
}

@Component({
  selector: 'app-machine-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
  ],
  template: `
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-semibold text-gray-900">Machines</h1>
        <button mat-raised-button color="primary" (click)="addMachine()">
          <mat-icon class="mr-1">add</mat-icon>
          Add Machine
        </button>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        @for (machine of machines(); track machine.id) {
          <mat-card class="rounded-xl hover:shadow-md transition-shadow p-0 overflow-hidden">
            <div class="p-5">
              <div class="flex items-start justify-between">
                <div>
                  <p class="text-xs text-gray-400 font-mono font-medium">{{ machine.code }}</p>
                  <h3 class="text-lg font-semibold text-gray-900 mt-0.5">{{ machine.name }}</h3>
                </div>
                <div class="flex items-center gap-1.5">
                  <span
                    class="w-2.5 h-2.5 rounded-full"
                    [class.bg-green-500]="machine.status === 'RUNNING'"
                    [class.bg-yellow-500]="machine.status === 'IDLE'"
                    [class.bg-red-500]="machine.status === 'MAINTENANCE'"
                    [class.bg-gray-400]="machine.status === 'DOWN'"
                  ></span>
                  <span
                    class="text-xs font-medium"
                    [class.text-green-600]="machine.status === 'RUNNING'"
                    [class.text-yellow-600]="machine.status === 'IDLE'"
                    [class.text-red-600]="machine.status === 'MAINTENANCE'"
                    [class.text-gray-500]="machine.status === 'DOWN'"
                  >
                    {{ machine.status.replace('_', ' ') }}
                  </span>
                </div>
              </div>

              <div class="mt-4">
                <div class="flex items-center justify-between text-sm mb-1.5">
                  <span class="text-gray-500">Utilization</span>
                  <span class="font-semibold text-gray-700">{{ machine.utilization }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2.5">
                  <div
                    class="h-2.5 rounded-full transition-all duration-500"
                    [class.bg-green-500]="machine.utilization >= 75"
                    [class.bg-yellow-500]="machine.utilization >= 50 && machine.utilization < 75"
                    [class.bg-orange-500]="machine.utilization >= 25 && machine.utilization < 50"
                    [class.bg-red-500]="machine.utilization < 25"
                    [style.width.%]="machine.utilization"
                  ></div>
                </div>
              </div>
            </div>

            <div class="border-t border-gray-100 px-5 py-3 flex items-center justify-end gap-1">
              <button mat-icon-button color="primary" (click)="viewMachine(machine)" matTooltip="View">
                <mat-icon class="text-sm">visibility</mat-icon>
              </button>
              <button mat-icon-button color="accent" (click)="editMachine(machine)" matTooltip="Edit">
                <mat-icon class="text-sm">edit</mat-icon>
              </button>
            </div>
          </mat-card>
        }
      </div>
    </div>
  `,
})
export class MachineListComponent {
  machines = signal<Machine[]>([
    { id: '1', code: 'CNC-001', name: 'CNC Milling Machine A', status: 'RUNNING', utilization: 87 },
    { id: '2', code: 'CNC-002', name: 'CNC Milling Machine B', status: 'IDLE', utilization: 12 },
    { id: '3', code: 'LTH-001', name: 'Lathe Machine 1', status: 'RUNNING', utilization: 92 },
    { id: '4', code: 'LTH-002', name: 'Lathe Machine 2', status: 'MAINTENANCE', utilization: 0 },
    { id: '5', code: 'PRS-001', name: 'Hydraulic Press', status: 'RUNNING', utilization: 65 },
    { id: '6', code: 'DRL-001', name: 'Drill Station', status: 'DOWN', utilization: 0 },
    { id: '7', code: 'WLD-001', name: 'Welding Robot', status: 'RUNNING', utilization: 78 },
    { id: '8', code: 'CNV-001', name: 'Conveyor System', status: 'IDLE', utilization: 5 },
  ]);

  viewMachine(machine: Machine): void {
    console.log('View machine:', machine.id);
  }

  editMachine(machine: Machine): void {
    console.log('Edit machine:', machine.id);
  }

  addMachine(): void {
    console.log('Add machine');
  }
}
