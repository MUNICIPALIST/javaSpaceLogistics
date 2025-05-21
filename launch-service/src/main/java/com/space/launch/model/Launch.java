package com.space.launch.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "launches")
public class Launch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String rocketName;

    @Column(nullable = false)
    private UUID cargoId;

    @Column(nullable = false)
    private UUID destinationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LaunchStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = LaunchStatus.SCHEDULED;
        }
    }
} 