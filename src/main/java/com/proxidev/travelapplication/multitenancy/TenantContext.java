package com.proxidev.travelapplication.multitenancy;

import com.proxidev.travelapplication.model.TenantContextHolder;

public final class TenantContext {
    private static ThreadLocal<TenantContextHolder> contextHolder = new InheritableThreadLocal<>();

    public static TenantContextHolder getTenantContextHolder() {
        return contextHolder.get();
    }

    public static void setTenantContextHolder(TenantContextHolder tenantContextHolder) {
        contextHolder.set(tenantContextHolder);
    }

    public static void clearTenantContextHolder() {
        contextHolder.remove();
    }
}
