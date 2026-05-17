import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Lead, Opportunity, Customer, SalesOrder, Page } from '../models';

@Injectable({ providedIn: 'root' })
export class SalesApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/api/v1/sales`;

  getLeads(page = 0, size = 10, status?: string): Observable<Page<Lead>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<Lead>>(`${this.base}/leads`, { params });
  }

  updateLeadStatus(id: string, status: string): Observable<Lead> {
    return this.http.put<Lead>(`${this.base}/leads/${id}/status`, { status });
  }

  getOpportunities(): Observable<Opportunity[]> {
    return this.http.get<Opportunity[]>(`${this.base}/opportunities`);
  }

  getCustomers(page = 0, size = 10, search?: string): Observable<Page<Customer>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<Page<Customer>>(`${this.base}/customers`, { params });
  }

  getSalesOrders(page = 0, size = 10, status?: string): Observable<Page<SalesOrder>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<SalesOrder>>(`${this.base}/sales-orders`, { params });
  }
}
