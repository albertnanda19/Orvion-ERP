import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account, Invoice, CreateInvoiceRequest, JournalEntry, Page } from '../models';

@Injectable({ providedIn: 'root' })
export class FinanceApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/api/v1/finance`;

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.base}/accounts`);
  }

  getInvoices(page = 0, size = 10, status?: string, search?: string): Observable<Page<Invoice>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    if (search) params = params.set('search', search);
    return this.http.get<Page<Invoice>>(`${this.base}/invoices`, { params });
  }

  getInvoice(id: string): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.base}/invoices/${id}`);
  }

  createInvoice(request: CreateInvoiceRequest): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.base}/invoices`, request);
  }

  approveInvoice(id: string): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.base}/invoices/${id}/approve`, {});
  }

  processPayment(id: string, request: any): Observable<any> {
    return this.http.post(`${this.base}/invoices/${id}/payments`, request);
  }

  getJournalEntries(page = 0, size = 10, period?: string): Observable<Page<JournalEntry>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (period) params = params.set('period', period);
    return this.http.get<Page<JournalEntry>>(`${this.base}/journal-entries`, { params });
  }
}
