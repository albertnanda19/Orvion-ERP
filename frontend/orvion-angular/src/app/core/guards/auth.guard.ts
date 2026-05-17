import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

export const authGuard: CanActivateFn = async () => {
  const keycloak = inject(KeycloakService);
  const router = inject(Router);
  try {
    const loggedIn = await keycloak.isLoggedIn();
    if (!loggedIn) {
      await keycloak.login();
      return false;
    }
    return true;
  } catch {
    router.navigate(['/unauthorized']);
    return false;
  }
};
