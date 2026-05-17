import { Injectable, signal, computed } from '@angular/core';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { OrvionUser } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private keycloak = inject(KeycloakService);
  isAuthenticated = signal(false);
  currentUser = signal<OrvionUser | null>(null);
  userRoles = signal<string[]>([]);
  hasRole = computed(() => (role: string) => this.userRoles().includes(role));

  async initUserFromToken(): Promise<void> {
    try {
      const authenticated = await this.keycloak.isLoggedIn();
      this.isAuthenticated.set(authenticated);
      if (authenticated) {
        const tokenParsed = this.keycloak.getKeycloakInstance().tokenParsed as any;
        const roles = tokenParsed?.realm_access?.roles || [];
        this.userRoles.set(roles);
        this.currentUser.set({
          id: tokenParsed?.sub || '',
          username: tokenParsed?.preferred_username || '',
          email: tokenParsed?.email || '',
          firstName: tokenParsed?.given_name || '',
          lastName: tokenParsed?.family_name || '',
          roles,
          tenantId: tokenParsed?.tenant_id || 'tenant1'
        });
      }
    } catch { this.isAuthenticated.set(false); }
  }

  login(): void { this.keycloak.login(); }
  logout(): void { this.keycloak.logout(); }
}
