package com.orvion.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.config.enabled=false",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/orvion"
    })
class GatewaySecurityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void unauthenticatedRequestToProtectedRouteReturns401() {
        webTestClient.get()
            .uri("/api/v1/finance/test")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("UNAUTHORIZED")
            .jsonPath("$.message").isNotEmpty();
    }

    @Test
    void healthEndpointReturns200WithoutAuth() {
        webTestClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void invalidJwtReturns401WithErrorResponse() {
        webTestClient.get()
            .uri("/api/v1/finance/test")
            .header("Authorization", "Bearer invalid-token")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("UNAUTHORIZED");
    }
}
