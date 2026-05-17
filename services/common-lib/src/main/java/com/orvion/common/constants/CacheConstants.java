package com.orvion.common.constants;

import java.time.Duration;

public final class CacheConstants {

    private CacheConstants() {
    }

    public static class Names {
        public static final String USERS = "users";
        public static final String ROLES = "roles";
        public static final String PERMISSIONS = "permissions";
        public static final String PRODUCTS = "products";
        public static final String INVENTORY = "inventory";
        public static final String CUSTOMERS = "customers";
        public static final String ORDERS = "orders";
        public static final String INVOICES = "invoices";
        public static final String TENANTS = "tenants";
        public static final String CONFIGURATION = "configuration";
        public static final String EXCHANGE_RATES = "exchangeRates";
        public static final String PRICING = "pricing";

        private Names() {
        }
    }

    public static class TTL {
        public static final Duration USER_SESSION = Duration.ofMinutes(30);
        public static final Duration SHORT_LIVED = Duration.ofMinutes(5);
        public static final Duration MEDIUM_LIVED = Duration.ofMinutes(30);
        public static final Duration LONG_LIVED = Duration.ofHours(2);
        public static final Duration EXTENDED = Duration.ofHours(24);
        public static final Duration CONFIG = Duration.ofHours(1);
        public static final Duration EXCHANGE_RATE = Duration.ofHours(6);
        public static final Duration PRODUCT_CATALOG = Duration.ofMinutes(15);

        private TTL() {
        }
    }
}
