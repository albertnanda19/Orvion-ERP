import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { WorkOrder, BomItem, Machine, Page } from '../models';

@Injectable({ providedIn: 'root' })
export class ManufacturingApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/api/v1/manufacturing`;

  getBoms(): Observable<BomItem[]> {
    return this.http.get<BomItem[]>(`${this.base}/boms`);
  }

  getBom(id: string): Observable<BomItem> {
    return this.http.get<BomItem>(`${this.base}/boms/${id}`);
  }

  getWorkOrders(page = 0, size = 10, status?: string): Observable<Page<WorkOrder>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<WorkOrder>>(`${this.base}/work-orders`, { params });
  }

  updateWorkOrderStatus(id: string, status: string): Observable<WorkOrder> {
    return this.http.put<WorkOrder>(`${this.base}/work-orders/${id}/status`, { status });
  }

  getMachines(): Observable<Machine[]> {
    return this.http.get<Machine[]>(`${this.base}/machines`);
  }
}
