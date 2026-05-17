import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { HttpClient } from '@angular/common/http';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { environment } from '../../../../environments/environment';

interface SearchResult {
  type: string;
  icon: string;
  label: string;
  subtitle: string;
  route: string[];
}

@Component({
  selector: 'app-global-search',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatIconModule, MatListModule],
  template: `
    <div class="p-4 max-w-lg w-[500px]">
      <mat-form-field appearance="outline" class="w-full">
        <mat-label>Search customers, products, invoices...</mat-label>
        <input matInput [(ngModel)]="query" (input)="onSearch()" placeholder="Type to search..." autofocus>
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>

      @if (loading()) {
        <div class="flex items-center justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
        </div>
      }

      @if (!loading() && results().length === 0 && query().length > 0) {
        <div class="text-center py-8 text-gray-500">
          <mat-icon class="text-4xl mb-2">search_off</mat-icon>
          <p>No results found</p>
        </div>
      }

      @if (results().length > 0) {
        <mat-list>
          @for (group of groupedResults(); track group.type) {
            <div class="mb-2">
              <div class="text-xs font-semibold text-gray-500 uppercase px-4 py-1">{{ group.type }}</div>
              @for (item of group.items; track item.label) {
                <mat-list-item (click)="navigate(item)" class="cursor-pointer hover:bg-gray-50 rounded-lg">
                  <div matListItemIcon>
                    <mat-icon class="text-primary-500">{{ item.icon }}</mat-icon>
                  </div>
                  <div matListItemTitle class="text-sm font-medium">{{ item.label }}</div>
                  <div matListItemLine class="text-xs text-gray-500">{{ item.subtitle }}</div>
                </mat-list-item>
              }
            </div>
          }
        </mat-list>
      }

      @if (query().length === 0) {
        <div class="text-center py-8 text-gray-400">
          <mat-icon class="text-4xl mb-2">lightbulb</mat-icon>
          <p class="text-sm">Search across all modules</p>
          <p class="text-xs mt-1">Press <kbd class="px-1 py-0.5 bg-gray-100 rounded text-xs">Esc</kbd> to close</p>
        </div>
      }
    </div>
  `
})
export class GlobalSearchComponent {
  private http = inject(HttpClient);
  private router = inject(Router);
  private dialogRef = inject(MatDialogRef<GlobalSearchComponent>);

  query = signal('');
  results = signal<SearchResult[]>([]);
  loading = signal(false);

  private search$ = new Subject<string>();

  constructor() {
    this.search$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => {
        if (!q.trim()) return of([]);
        this.loading.set(true);
        return this.http.get<SearchResult[]>(`${environment.apiBaseUrl}/api/v1/reports/audit-logs`, {
          params: { query: q }
        });
      })
    ).subscribe({
      next: (res) => {
        this.results.set(res || []);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.results.set([]);
      }
    });
  }

  groupedResults = computed(() => {
    const groups = new Map<string, SearchResult[]>();
    for (const r of this.results()) {
      if (!groups.has(r.type)) groups.set(r.type, []);
      groups.get(r.type)!.push(r);
    }
    return Array.from(groups.entries()).map(([type, items]) => ({ type, items }));
  });

  onSearch(): void { this.search$.next(this.query()); }

  navigate(item: SearchResult): void {
    this.dialogRef.close();
    this.router.navigate(item.route);
  }
}
