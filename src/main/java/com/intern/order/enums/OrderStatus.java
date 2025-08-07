package com.intern.order.enums;

/**
 * Represents the status of an order.
 */
public enum OrderStatus {
    /**
     * Order has been placed but not yet confirmed.
     */
    PENDING,

    /**
     * Order has been confirmed by the system or staff.
     */
    CONFIRMED,

    /**
     * Order has been shipped to the customer.
     */
    SHIPPED,

    /**
     * Order has been successfully delivered to the customer.
     */
    DELIVERED,

    /**
     * Order has been cancelled.
     */
    CANCELLED
}
