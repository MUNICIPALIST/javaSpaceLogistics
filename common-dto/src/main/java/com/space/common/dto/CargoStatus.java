package com.space.common.dto;

/**
 * Enum representing possible states of a cargo during its lifecycle.
 */
public enum CargoStatus {
    /**
     * Cargo has been created but not yet assigned for delivery
     */
    CREATED,

    /**
     * Cargo is currently in transit to its destination
     */
    IN_TRANSIT,

    /**
     * Cargo has been successfully delivered to its destination
     */
    DELIVERED
} 