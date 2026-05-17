package com.orvion.hcm.infrastructure.aspect;

import com.orvion.hcm.infrastructure.messaging.AuditEventPublisher;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class AuditLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingAspect.class);
    private static final String SERVICE_NAME = "orvion-hcm-service";

    private final AuditEventPublisher auditEventPublisher;

    public AuditLoggingAspect(AuditEventPublisher auditEventPublisher) {
        this.auditEventPublisher = auditEventPublisher;
    }

    @Around("execution(* com.orvion.hcm.presentation.controller.*.*(..))")
    public Object auditControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String tenantId = extractTenantId();
        String userId = extractUserId();
        String params = sanitizeParams(joinPoint.getArgs());

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            String details = String.format("Method: %s | Params: %s | Duration: %dms | Status: SUCCESS",
                methodName, params, duration);
            auditEventPublisher.publishAuditEvent(tenantId, userId, methodName, details, SERVICE_NAME);
            log.debug("Audit: {} completed in {}ms", methodName, duration);
            return result;
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            String details = String.format("Method: %s | Params: %s | Duration: %dms | Status: ERROR | Error: %s",
                methodName, params, duration, t.getMessage());
            auditEventPublisher.publishAuditEvent(tenantId, userId, methodName, details, SERVICE_NAME);
            log.warn("Audit: {} failed in {}ms: {}", methodName, duration, t.getMessage());
            throw t;
        }
    }

    private String extractTenantId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String tenantId = request.getHeader("X-Tenant-Id");
            if (tenantId != null) return tenantId;
        }
        return "";
    }

    private String extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }

    private String sanitizeParams(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        return Arrays.stream(args)
            .map(arg -> {
                if (arg == null) return "null";
                String str = arg.toString();
                if (str.length() > 100) str = str.substring(0, 100) + "...";
                return str.replaceAll("(?i)(password|secret|token|authorization)=\\S+", "$1=***");
            })
            .collect(Collectors.joining(", ", "[", "]"));
    }
}
