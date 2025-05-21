package com.space.cargo.dto;

import com.space.cargo.model.CargoStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CargoDTO {
    private UUID id;
    private String name;
    private double weight;
    private UUID destinationId;
    private CargoStatus status;
    private LocalDateTime createdAt;
} 