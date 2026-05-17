package com.orvion.reporting.presentation.controller;

import com.orvion.common.security.TenantContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    protected String extractTenantId() {
        String tenantId = TenantContextHolder.getTenantId();
        return tenantId != null ? tenantId : "tenant1";
    }

    protected String extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
