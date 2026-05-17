import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Notification, Page } from '../models';
import { map, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}/api/v1/notifications`;

  unreadCount = signal(0);
  notifications = signal<Notification[]>([]);

  fetchNotifications(page = 0, size = 20): Observable<Page<Notification>> {
    return this.http.get<Page<Notification>>(this.apiUrl, { params: { page, size } }).pipe(
      map(res => { this.notifications.set(res.content); return res; })
    );
  }

  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`).pipe(
      map(c => { this.unreadCount.set(c); return c; })
    );
  }

  markAsRead(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {}).pipe(
      map(() => this.unreadCount.update(c => Math.max(0, c - 1)))
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read-all`, {}).pipe(
      map(() => this.unreadCount.set(0))
    );
  }

  deleteNotification(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
