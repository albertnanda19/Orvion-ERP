export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080',
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'orvion',
    clientId: 'orvion-frontend'
  },
  websocket: {
    url: 'http://localhost:8086/ws'
  }
};
