import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return async () => {
    const keycloak = inject(KeycloakService);
    const router = inject(Router);
    try {
      const tokenParsed = keycloak.getKeycloakInstance().tokenParsed as any;
      const roles: string[] = tokenParsed?.realm_access?.roles || [];
      const hasAccess = roles.some(r => allowedRoles.includes(r));
      if (!hasAccess) { router.navigate(['/unauthorized']); return false; }
      return true;
    } catch { router.navigate(['/unauthorized']); return false; }
  };
}
