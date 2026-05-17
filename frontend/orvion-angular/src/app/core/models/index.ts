export interface OrvionUser { id: string; username: string; email: string; firstName: string; lastName: string; roles: string[]; tenantId: string; }

export interface Page<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number; first: boolean; last: boolean; }

export interface TableColumn { key: string; label: string; sortable?: boolean; width?: string; type?: 'text' | 'number' | 'date' | 'currency' | 'status' | 'action'; }

export interface Notification { id: string; type: string; title: string; message: string; read: boolean; createdAt: string; }

export interface KpiData { label: string; value: number; previousValue?: number; change?: number; trend?: 'up' | 'down' | 'neutral'; icon: string; color: string; }

export interface Invoice { id: string; invoiceNo: string; type: 'RECEIVABLE' | 'PAYABLE'; counterpartyName: string; amount: number; currency: string; status: string; dueDate: string; createdAt: string; }
export interface InvoiceLineItem { description: string; quantity: number; unitPrice: number; total: number; }
export interface CreateInvoiceRequest { counterpartyId: string; invoiceType: string; currency: string; lineItems: InvoiceLineItem[]; }

export interface Account { id: string; code: string; name: string; type: string; balance: number; }

export interface JournalEntry { id: string; entryNumber: string; description: string; entryDate: string; status: string; totalDebit: number; totalCredit: number; }

export interface Product { id: string; sku: string; name: string; category: string; stockLevel: number; unitPrice: number; }
export interface PurchaseOrder { id: string; poNumber: string; supplierName: string; status: string; totalAmount: number; orderDate: string; }

export interface Employee { id: string; employeeNo: string; firstName: string; lastName: string; department: string; position: string; status: string; }
export interface PayrollRecord { id: string; employeeId: string; period: string; basicSalary: number; allowances: number; deductions: number; netPay: number; status: string; }
export interface LeaveRequest { id: string; employeeId: string; type: string; startDate: string; endDate: string; status: string; }

export interface WorkOrder { id: string; orderNo: string; productName: string; quantity: number; status: string; dueDate: string; }
export interface BomItem { id: string; componentName: string; quantity: number; unit: string; }
export interface Machine { id: string; code: string; name: string; status: string; utilization: number; }

export interface Lead { id: string; companyName: string; contactName: string; email: string; phone: string; status: string; priority: string; }
export interface Opportunity { id: string; name: string; customerName: string; stage: string; amount: number; probability: number; }
export interface Customer { id: string; name: string; email: string; creditLimit: number; outstandingAmount: number; }
export interface SalesOrder { id: string; orderNo: string; customerName: string; orderDate: string; status: string; totalAmount: number; }

export interface DashboardResponse { financial: FinancialSummary; inventory: InventorySummary; sales: SalesSummary; hr: HrSummary; }
export interface FinancialSummary { totalRevenue: number; totalExpenses: number; netProfit: number; grossMargin: number; outstandingInvoices: number; cashPosition: number; }
export interface InventorySummary { totalProducts: number; totalStockValue: number; lowStockCount: number; turnoverRate: number; }
export interface SalesSummary { totalOrders: number; totalSalesRevenue: number; conversionRate: number; avgOrderValue: number; openOrders: number; }
export interface HrSummary { totalEmployees: number; totalPayrollCost: number; }

export interface SalesReport { period: string; totalOrders: number; totalRevenue: number; conversionRate: number; avgOrderValue: number; }
export interface InventoryReport { period: string; totalProducts: number; totalStockValue: number; lowStockCount: number; turnoverRate: number; }
export interface HrReport { period: string; totalEmployees: number; totalPayrollCost: number; }

export interface ReportDefinition { id: string; name: string; description: string; reportType: string; scheduleConfig: string; outputFormat: string; active: boolean; }
export interface ReportExecution { id: string; reportDefinitionId: string; status: string; executionDurationMs: number; errorMessage: string; }
