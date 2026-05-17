package com.orvion.common.security;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = ThreadLocal.withInitial(TenantContext::new);

    private TenantContextHolder() {
    }

    public static void setTenantId(String tenantId) {
        TenantContext context = CONTEXT.get();
        context.setTenantId(tenantId);
    }

    public static String getTenantId() {
        return CONTEXT.get().getTenantId();
    }

    public static void setUserId(String userId) {
        TenantContext context = CONTEXT.get();
        context.setUserId(userId);
    }

    public static String getUserId() {
        return CONTEXT.get().getUserId();
    }

    public static void setContext(TenantContext context) {
        CONTEXT.set(context);
    }

    public static TenantContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
