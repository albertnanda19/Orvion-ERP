export const environment = {
  production: true,
  apiBaseUrl: 'https://api.orvion.com',
  keycloak: {
    url: 'https://sso.orvion.com',
    realm: 'orvion',
    clientId: 'orvion-frontend'
  },
  websocket: {
    url: 'wss://notifications.orvion.com/ws'
  }
};
