package com.space.launch.dto;

import com.space.launch.model.LaunchStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LaunchDTO {
    private UUID id;
    private String rocketName;
    private UUID cargoId;
    private UUID destinationId;
    private LaunchStatus status;
    private LocalDateTime createdAt;
} 