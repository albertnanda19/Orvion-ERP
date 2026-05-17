package com.orvion.gateway.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtClaimsExtractor {

    private static final String TENANT_CLAIM = "tenantId";
    private static final String EMAIL_CLAIM = "email";

    public Mono<ServerWebExchange> extractAndEnhance(ServerWebExchange exchange) {
        return exchange.getPrincipal()
            .filter(principal -> principal instanceof JwtAuthenticationToken)
            .cast(JwtAuthenticationToken.class)
            .map(token -> {
                var claims = token.getToken().getClaims();
                var headers = exchange.getRequest().mutate();

                String tenantId = claims.containsKey(TENANT_CLAIM)
                    ? claims.get(TENANT_CLAIM).toString() : "default";
                headers.header("X-Tenant-Id", tenantId);

                String userId = claims.containsKey("sub")
                    ? claims.get("sub").toString() : "";
                headers.header("X-User-Id", userId);

                String email = claims.containsKey(EMAIL_CLAIM)
                    ? claims.get(EMAIL_CLAIM).toString() : "";
                headers.header("X-User-Email", email);

                @SuppressWarnings("unchecked")
                String roles = claims.containsKey("realm_access")
                    ? ((java.util.Map<String, Collection<String>>) claims.get("realm_access"))
                        .getOrDefault("roles", java.util.Collections.emptyList())
                        .stream().collect(Collectors.joining(","))
                    : "";
                headers.header("X-User-Roles", roles);

                return exchange.mutate().request(headers.build()).build();
            })
            .defaultIfEmpty(exchange);
    }
}
