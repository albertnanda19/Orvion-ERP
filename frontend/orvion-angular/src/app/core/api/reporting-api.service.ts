import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page, DashboardResponse, SalesReport, InventoryReport, HrReport, ReportDefinition, ReportExecution } from '../models';

@Injectable({ providedIn: 'root' })
export class ReportingApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/api/v1/reports`;

  getExecutiveDashboard(period: string): Observable<DashboardResponse> {
    const params = new HttpParams().set('period', period);
    return this.http.get<DashboardResponse>(`${this.base}/executive-dashboard`, { params });
  }

  getSalesReport(startDate: string, endDate: string, groupBy: string): Observable<SalesReport[]> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate).set('groupBy', groupBy);
    return this.http.get<SalesReport[]>(`${this.base}/sales`, { params });
  }

  getInventoryReport(): Observable<InventoryReport> {
    return this.http.get<InventoryReport>(`${this.base}/inventory`);
  }

  getHrReport(period: string): Observable<HrReport> {
    const params = new HttpParams().set('period', period);
    return this.http.get<HrReport>(`${this.base}/hr`, { params });
  }

  getReportDefinitions(): Observable<ReportDefinition[]> {
    return this.http.get<ReportDefinition[]>(`${this.base}/definitions`);
  }

  createReportDefinition(request: Partial<ReportDefinition>): Observable<ReportDefinition> {
    return this.http.post<ReportDefinition>(`${this.base}/definitions`, request);
  }

  executeReport(id: string): Observable<ReportExecution> {
    return this.http.post<ReportExecution>(`${this.base}/definitions/${id}/execute`, {});
  }

  getReportExecutionStatus(id: string): Observable<ReportExecution> {
    return this.http.get<ReportExecution>(`${this.base}/executions/${id}`);
  }

  searchAuditLogs(query?: string, dateFrom?: string, dateTo?: string, page = 0, size = 10): Observable<Page<any>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (query) params = params.set('query', query);
    if (dateFrom) params = params.set('dateFrom', dateFrom);
    if (dateTo) params = params.set('dateTo', dateTo);
    return this.http.get<Page<any>>(`${this.base}/audit-logs`, { params });
  }
}
