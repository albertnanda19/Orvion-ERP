#!/bin/bash
# Keycloak Setup Script for Orvion ERP
# Creates realm, clients, roles, and test users

set -euo pipefail

KC_HOME="$HOME/keycloak-24.0.4"
KC_URL="http://localhost:8180"
KCADM="$KC_HOME/bin/kcadm.sh"

echo "=== Creating Orvion Realm ==="
$KCADM create realms -s realm=orvion -s enabled=true -s displayName="Orvion ERP"

echo "=== Creating Microservice Clients ==="
SERVICES=("finance-service" "inventory-service" "hcm-service" "manufacturing-service" "sales-service" "notification-service" "reporting-service" "api-gateway")

for svc in "${SERVICES[@]}"; do
  $KCADM create clients -r orvion \
    -s clientId="$svc" \
    -s enabled=true \
    -s publicClient=false \
    -s secret="${svc}-secret" \
    -s serviceAccountsEnabled=true \
    -s authorizationServicesEnabled=true \
    -s "redirectUris=[\"http://localhost:*/callback\"]" \
    -o --fields id,clientId 2>&1 | grep -E '"id"|"clientId"'
done

echo "=== Creating Realm Roles ==="
ROLES=("admin" "manager" "user" "viewer" "auditor")
for role in "${ROLES[@]}"; do
  $KCADM create roles -r orvion -s name="$role" -s description="Orvion $role role"
done

echo "=== Creating Composite Admin Role ==="
for comp_role in manager user viewer auditor; do
  $KCADM add-roles -r orvion --rname admin --rolename "$comp_role" 2>/dev/null || true
done

echo "=== Creating Test Users ==="
while IFS=':' read -r username password role email first last; do
  user_id=$($KCADM create users -r orvion \
    -s "username=$username" \
    -s "email=$email" \
    -s "firstName=$first" \
    -s "lastName=$last" \
    -s enabled=true \
    -s emailVerified=true \
    -o --fields id 2>&1 | grep -o '"id" : "[^"]*"' | cut -d'"' -f4)

  if [ -n "$user_id" ]; then
    $KCADM set-password -r orvion --userid "$user_id" -p "$password" 2>/dev/null
    $KCADM add-roles -r orvion --uusername "$username" --rolename "$role"
    echo "  Created user: $username role=$role"
  fi
done <<< "admin:OrvionAdmin@2024:admin:admin@orvion.com:Admin:User
john.doe:Password123!:manager:john.doe@orvion.com:John:Doe
jane.smith:Password123!:user:jane.smith@orvion.com:Jane:Smith
bob.wilson:Password123!:viewer:bob.wilson@orvion.com:Bob:Wilson
auditor.lee:Password123!:auditor:auditor.lee@orvion.com:Auditor:Lee"

echo "=== Verification ==="
echo "--- Realm ---"
$KCADM get realms/orvion --fields realm,enabled 2>&1
echo "--- Clients ---"
$KCADM get clients -r orvion --fields clientId 2>&1 | grep clientId
echo "--- Roles ---"
$KCADM get roles -r orvion --fields name 2>&1 | grep name
echo "--- Users ---"
$KCADM get users -r orvion --fields username,email 2>&1 | grep -E '"username"|"email"'

echo ""
echo "=== Keycloak Setup Complete ==="
echo "Admin Console: http://localhost:8180/admin/"
echo "Orvion Realm: http://localhost:8180/admin/master/console/#/orvion"
