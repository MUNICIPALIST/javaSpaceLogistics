package com.space.common.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Cargo entity.
 * Used for transferring cargo data between services.
 */
@Data
public class CargoDTO {
    /**
     * Unique identifier of the cargo
     */
    private UUID id;

    /**
     * Name of the cargo
     */
    private String name;

    /**
     * Weight of the cargo in kilograms
     */
    private double weight;

    /**
     * ID of the destination where cargo should be delivered
     */
    private UUID destinationId;

    /**
     * Current status of the cargo
     */
    private CargoStatus status;

    /**
     * Timestamp when the cargo was created
     */
    private LocalDateTime createdAt;
} 