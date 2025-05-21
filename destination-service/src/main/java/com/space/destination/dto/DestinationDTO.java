package com.space.destination.dto;

import com.space.destination.model.DestinationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DestinationDTO {
    private UUID id;
    private String name;
    private DestinationType type;
    private String coordinates;
    private double capacity;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
} 