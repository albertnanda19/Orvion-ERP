import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'unauthorized', loadComponent: () => import('./features/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent) },
  {
    path: '',
    loadComponent: () => import('./layout/shell/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      {
        path: 'finance',
        canActivate: [roleGuard(['FINANCE_MANAGER', 'FINANCE_STAFF', 'SUPER_ADMIN'])],
        children: [
          { path: 'accounts', loadComponent: () => import('./features/finance/accounts/account-list.component').then(m => m.AccountListComponent) },
          { path: 'journal-entries', loadComponent: () => import('./features/finance/journal-entries/journal-entry-list.component').then(m => m.JournalEntryListComponent) },
          { path: 'invoices', loadComponent: () => import('./features/finance/invoices/invoice-list.component').then(m => m.InvoiceListComponent) },
          { path: 'invoices/new', loadComponent: () => import('./features/finance/invoices/invoice-create.component').then(m => m.InvoiceCreateComponent) },
          { path: 'invoices/:id', loadComponent: () => import('./features/finance/invoices/detail/invoice-detail.component').then(m => m.InvoiceDetailComponent) },
          { path: 'reports', loadComponent: () => import('./features/finance/reports/finance-reports.component').then(m => m.FinanceReportsComponent) },
        ]
      },
      {
        path: 'inventory',
        canActivate: [roleGuard(['INVENTORY_MANAGER', 'INVENTORY_STAFF', 'SUPER_ADMIN'])],
        children: [
          { path: 'products', loadComponent: () => import('./features/inventory/products/product-list.component').then(m => m.ProductListComponent) },
          { path: 'warehouse', loadComponent: () => import('./features/inventory/warehouse/warehouse.component').then(m => m.WarehouseComponent) },
          { path: 'purchase-orders', loadComponent: () => import('./features/inventory/purchase-orders/purchase-order-list.component').then(m => m.PurchaseOrderListComponent) },
          { path: 'stock-movements', loadComponent: () => import('./features/inventory/movements/stock-movement-list.component').then(m => m.StockMovementListComponent) },
        ]
      },
      {
        path: 'hcm',
        canActivate: [roleGuard(['HR_MANAGER', 'HR_STAFF', 'SUPER_ADMIN'])],
        children: [
          { path: 'employees', loadComponent: () => import('./features/hcm/employees/employee-list.component').then(m => m.EmployeeListComponent) },
          { path: 'payroll', loadComponent: () => import('./features/hcm/payroll/payroll.component').then(m => m.PayrollComponent) },
          { path: 'leave', loadComponent: () => import('./features/hcm/leave/leave-request-list.component').then(m => m.LeaveRequestListComponent) },
          { path: 'attendance', loadComponent: () => import('./features/hcm/attendance/attendance.component').then(m => m.AttendanceComponent) },
        ]
      },
      {
        path: 'manufacturing',
        canActivate: [roleGuard(['MANUFACTURING_MANAGER', 'MANUFACTURING_STAFF', 'SUPER_ADMIN'])],
        children: [
          { path: 'bom', loadComponent: () => import('./features/manufacturing/bom/bom-list.component').then(m => m.BomListComponent) },
          { path: 'work-orders', loadComponent: () => import('./features/manufacturing/work-orders/work-order-list.component').then(m => m.WorkOrderListComponent) },
          { path: 'machines', loadComponent: () => import('./features/manufacturing/machines/machine-list.component').then(m => m.MachineListComponent) },
        ]
      },
      {
        path: 'sales',
        canActivate: [roleGuard(['SALES_MANAGER', 'SALES_STAFF', 'SUPER_ADMIN'])],
        children: [
          { path: 'leads', loadComponent: () => import('./features/sales/leads/lead-list.component').then(m => m.LeadListComponent) },
          { path: 'opportunities', loadComponent: () => import('./features/sales/opportunities/opportunity-list.component').then(m => m.OpportunityListComponent) },
          { path: 'customers', loadComponent: () => import('./features/sales/customers/customer-list.component').then(m => m.CustomerListComponent) },
          { path: 'orders', loadComponent: () => import('./features/sales/orders/sales-order-list.component').then(m => m.SalesOrderListComponent) },
        ]
      },
      { path: 'reports', loadComponent: () => import('./features/reporting/reporting.component').then(m => m.ReportingComponent) },
      { path: 'settings', canActivate: [roleGuard(['SUPER_ADMIN'])], loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent) },
    ]
  },
  { path: '**', loadComponent: () => import('./features/not-found/not-found.component').then(m => m.NotFoundComponent) }
];
