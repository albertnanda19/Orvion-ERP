import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
}

@Component({
  selector: 'orvion-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <div class="p-6">
      <h2 mat-dialog-title class="!text-lg !font-semibold !text-gray-900 !mb-2 !p-0">
        {{ data.title }}
      </h2>

      <mat-dialog-content class="!p-0 !m-0">
        <p class="text-sm text-gray-600">{{ data.message }}</p>
      </mat-dialog-content>

      <mat-dialog-actions align="end" class="!p-0 !mt-6 !mb-0 !min-h-0">
        <button mat-button (click)="onCancel()" class="!text-gray-600">
          {{ data.cancelLabel || 'Cancel' }}
        </button>
        <button mat-raised-button color="warn" (click)="onConfirm()" class="!ml-2">
          {{ data.confirmLabel || 'Confirm' }}
        </button>
      </mat-dialog-actions>
    </div>
  `,
})
export class OrvionConfirmDialogComponent {
  private dialogRef = inject<MatDialogRef<OrvionConfirmDialogComponent>>(MatDialogRef);
  protected data: ConfirmDialogData = inject(MAT_DIALOG_DATA);

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
