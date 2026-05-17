import { Component, signal, computed } from '@angular/core';
import { inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgClass } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  requiredRoles: string[];
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    NgClass,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
    MatButtonModule,
  ],
  template: `
    <mat-drawer-container class="h-screen flex" autosize>
      <mat-drawer
        [opened]="sidebarOpen()"
        mode="side"
        class="border-r border-gray-200 bg-white transition-all duration-300"
        [class.w-64]="!sidebarCollapsed()"
        [class.w-16]="sidebarCollapsed()"
      >
        <div class="flex flex-col h-full">
          <div class="h-16 flex items-center px-4 border-b border-gray-100">
            <span class="text-xl font-bold text-primary-600" [class.hidden]="sidebarCollapsed()">Orvion ERP</span>
            <span class="text-xl font-bold text-primary-600" [class.hidden]="!sidebarCollapsed()">OE</span>
          </div>

          <mat-nav-list class="flex-1 overflow-y-auto py-2">
            @for (item of visibleMenuItems(); track item.route) {
              <a
                mat-list-item
                [routerLink]="item.route"
                routerLinkActive="bg-primary-50 text-primary-600"
                [routerLinkActiveOptions]="{ exact: item.route === 'dashboard' }"
                class="mx-2 mb-1 rounded-lg hover:bg-gray-50"
                [class.justify-center]="sidebarCollapsed()"
                [class.px-3]="!sidebarCollapsed()"
              >
                <mat-icon class="text-gray-500" [class.mr-3]="!sidebarCollapsed()">{{ item.icon }}</mat-icon>
                <span [class.hidden]="sidebarCollapsed()">{{ item.label }}</span>
              </a>
            }
          </mat-nav-list>
        </div>
      </mat-drawer>

      <mat-drawer-content class="flex flex-col">
        <mat-toolbar class="bg-white border-b border-gray-200 h-16 flex items-center px-4 gap-4">
          <button mat-icon-button (click)="toggleSidebar()" class="text-gray-600">
            <mat-icon>menu</mat-icon>
          </button>

          <div class="flex-1 flex items-center">
            <div class="relative max-w-md w-full">
              <mat-icon class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-lg">search</mat-icon>
              <input
                type="text"
                placeholder="Search... (Cmd+K)"
                class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          <button mat-icon-button class="text-gray-600 relative">
            <mat-icon>notifications</mat-icon>
            <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
          </button>

          <button mat-icon-button (click)="themeService.toggleTheme()" class="text-gray-600">
            <mat-icon>{{ themeService.isDarkMode() ? 'light_mode' : 'dark_mode' }}</mat-icon>
          </button>

          <div class="flex items-center gap-2 pl-2 border-l border-gray-200">
            <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 text-sm font-semibold">
              {{ userInitials() }}
            </div>
            <span class="text-sm font-medium text-gray-700" [class.hidden]="sidebarCollapsed()">
              {{ authService.currentUser()?.firstName }}
            </span>
          </div>
        </mat-toolbar>

        <main class="flex-1 overflow-auto bg-gray-50 p-6">
          <router-outlet />
        </main>
      </mat-drawer-content>
    </mat-drawer-container>
  `,
})
export class ShellComponent {
  protected authService = inject(AuthService);
  protected themeService = inject(ThemeService);

  sidebarOpen = signal(true);
  sidebarCollapsed = signal(false);

  userInitials = computed(() => {
    const user = this.authService.currentUser();
    if (!user) return '?';
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  });

  private menuItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'dashboard', requiredRoles: [] },
    { label: 'Finance', route: '/finance/accounts', icon: 'account_balance', requiredRoles: ['FINANCE_MANAGER', 'FINANCE_STAFF', 'SUPER_ADMIN'] },
    { label: 'Inventory', route: '/inventory/products', icon: 'inventory_2', requiredRoles: ['INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN'] },
    { label: 'HCM', route: '/hcm/employees', icon: 'people', requiredRoles: ['HR_MANAGER', 'HR_STAFF', 'SUPER_ADMIN'] },
    { label: 'Manufacturing', route: '/manufacturing/work-orders', icon: 'factory', requiredRoles: ['MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN'] },
    { label: 'Sales', route: '/sales/orders', icon: 'trending_up', requiredRoles: ['SALES_MANAGER', 'SALES_STAFF', 'SUPER_ADMIN'] },
    { label: 'Reports', route: '/reports', icon: 'bar_chart', requiredRoles: [] },
    { label: 'Settings', route: '/settings', icon: 'settings', requiredRoles: ['SUPER_ADMIN'] },
  ];

  visibleMenuItems = computed(() => {
    const userRoles = this.authService.userRoles();
    return this.menuItems.filter(item => {
      if (item.requiredRoles.length === 0) return true;
      return item.requiredRoles.some(role => userRoles.includes(role));
    });
  });

  toggleSidebar(): void {
    if (window.innerWidth < 1024) {
      this.sidebarOpen.update(v => !v);
    } else {
      this.sidebarCollapsed.update(v => !v);
    }
  }
}
