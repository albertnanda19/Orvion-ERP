import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Employee, PayrollRecord, LeaveRequest, Page } from '../models';

@Injectable({ providedIn: 'root' })
export class HcmApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/api/v1/hcm`;

  getEmployees(page = 0, size = 10, search?: string, department?: string, status?: string): Observable<Page<Employee>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    if (department) params = params.set('department', department);
    if (status) params = params.set('status', status);
    return this.http.get<Page<Employee>>(`${this.base}/employees`, { params });
  }

  getEmployee(id: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.base}/employees/${id}`);
  }

  getPayroll(period: string, page = 0, size = 10): Observable<Page<PayrollRecord>> {
    const params = new HttpParams().set('period', period).set('page', page).set('size', size);
    return this.http.get<Page<PayrollRecord>>(`${this.base}/payroll`, { params });
  }

  approvePayroll(id: string): Observable<PayrollRecord> {
    return this.http.put<PayrollRecord>(`${this.base}/payroll/${id}/approve`, {});
  }

  getLeaveRequests(page = 0, size = 10, status?: string, type?: string): Observable<Page<LeaveRequest>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    if (type) params = params.set('type', type);
    return this.http.get<Page<LeaveRequest>>(`${this.base}/leave-requests`, { params });
  }

  approveLeave(id: string): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.base}/leave-requests/${id}/approve`, {});
  }

  rejectLeave(id: string): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.base}/leave-requests/${id}/reject`, {});
  }

  getAttendance(date?: string, department?: string): Observable<any[]> {
    let params = new HttpParams();
    if (date) params = params.set('date', date);
    if (department) params = params.set('department', department);
    return this.http.get<any[]>(`${this.base}/attendance`, { params });
  }
}
