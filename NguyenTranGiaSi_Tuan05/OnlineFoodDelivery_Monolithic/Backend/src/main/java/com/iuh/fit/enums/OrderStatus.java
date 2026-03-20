package com.iuh.fit.enums;

public enum OrderStatus {
    PENDING, CONFIRMED, PREPARING, DELIVERING, COMPLETED, CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == PREPARING || next == CANCELLED;
            case PREPARING -> next == DELIVERING;
            case DELIVERING -> next == COMPLETED;
            default -> false;
        };
    }
}
