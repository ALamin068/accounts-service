package com.JDMGod.accounts_service.constants;

public final class ApplicationConstants {
    
    private ApplicationConstants() {
        // Utility class
    }
    
    public static final class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String DELETED = "DELETED";
        public static final String INACTIVE = "INACTIVE";
        
        private Status() {}
    }
    
    public static final class OrderStatus {
        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String SHIPPED = "SHIPPED";
        public static final String DELIVERED = "DELIVERED";
        public static final String CANCELLED = "CANCELLED";
        
        private OrderStatus() {}
    }
    
    public static final class PaymentStatus {
        public static final String PENDING = "PENDING";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";
        public static final String REFUNDED = "REFUNDED";
        
        private PaymentStatus() {}
    }
    
    public static final class Roles {
        public static final String ADMIN = "ADMIN";
        public static final String CUSTOMER = "CUSTOMER";
        public static final String SELLER = "SELLER";
        
        private Roles() {}
    }
    
    public static final class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int DEFAULT_PAGE_NUMBER = 0;
        public static final int MAX_PAGE_SIZE = 100;
        
        private Pagination() {}
    }
    
    public static final class Security {
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final int MIN_JWT_SECRET_LENGTH = 32;
        
        private Security() {}
    }
}