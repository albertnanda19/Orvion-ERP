package com.orvion.finance.presentation;

import com.orvion.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BaseController {

    protected String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId == null || tenantId.isBlank()) {
            throw new UnauthorizedException("Missing X-Tenant-Id header");
        }
        return tenantId;
    }

    protected String extractUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId != null ? userId : "SYSTEM";
    }
}
