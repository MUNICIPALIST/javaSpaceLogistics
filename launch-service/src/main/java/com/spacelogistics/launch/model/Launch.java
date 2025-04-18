package com.spacelogistics.launch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "launches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Launch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long cargoId;
    
    private Long destinationId;
    
    private LocalDateTime launchDate;
    
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, FAILED
    
    private Double fuelRequired;
    
    private LocalDateTime estimatedArrival;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 