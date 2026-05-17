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
class FallbackControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void financeFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/finance")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Circuit-Breaker", "open")
            .expectHeader().valueEquals("X-Fallback-Service", "finance-service")
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("SERVICE_UNAVAILABLE")
            .jsonPath("$.message").isEqualTo("Service temporarily unavailable. Please try again later.");
    }

    @Test
    void inventoryFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/inventory")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Circuit-Breaker", "open")
            .expectHeader().valueEquals("X-Fallback-Service", "inventory-service")
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("SERVICE_UNAVAILABLE")
            .jsonPath("$.message").isNotEmpty();
    }

    @Test
    void hcmFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/hcm")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Fallback-Service", "hcm-service");
    }

    @Test
    void manufacturingFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/manufacturing")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Fallback-Service", "manufacturing-service");
    }

    @Test
    void salesFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/sales")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Fallback-Service", "sales-crm-service");
    }

    @Test
    void notificationFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/notification")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Fallback-Service", "notification-service");
    }

    @Test
    void reportingFallbackReturns503() {
        webTestClient.get()
            .uri("/fallback/reporting")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().valueEquals("X-Fallback-Service", "reporting-service");
    }
}
