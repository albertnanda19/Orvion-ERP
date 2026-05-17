import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, PurchaseOrder, Page } from '../models';

@Injectable({ providedIn: 'root' })
export class InventoryApiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/api/v1/inventory`;

  getProducts(page = 0, size = 10, search?: string, category?: string): Observable<Page<Product>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    if (category) params = params.set('category', category);
    return this.http.get<Page<Product>>(`${this.base}/products`, { params });
  }

  getProduct(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.base}/products/${id}`);
  }

  createProduct(request: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(`${this.base}/products`, request);
  }

  getWarehouses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/warehouses`);
  }

  getStockMovements(page = 0, size = 10, type?: string, dateFrom?: string, dateTo?: string): Observable<Page<any>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (type) params = params.set('type', type);
    if (dateFrom) params = params.set('dateFrom', dateFrom);
    if (dateTo) params = params.set('dateTo', dateTo);
    return this.http.get<Page<any>>(`${this.base}/stock-movements`, { params });
  }

  getPurchaseOrders(page = 0, size = 10, status?: string): Observable<Page<PurchaseOrder>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<PurchaseOrder>>(`${this.base}/purchase-orders`, { params });
  }

  createPurchaseOrder(request: Partial<PurchaseOrder>): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(`${this.base}/purchase-orders`, request);
  }
}
