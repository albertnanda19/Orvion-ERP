import { Injectable, signal } from '@angular/core';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({ providedIn: 'root' })
export class TenantService {
  private keycloak = inject(KeycloakService);
  currentTenantId = signal('tenant1');

  initFromToken(): void {
    try {
      const token = this.keycloak.getKeycloakInstance().tokenParsed as any;
      if (token?.tenant_id) this.currentTenantId.set(token.tenant_id);
    } catch { this.currentTenantId.set('tenant1'); }
  }

  setTenant(tenantId: string): void { this.currentTenantId.set(tenantId); }
}
