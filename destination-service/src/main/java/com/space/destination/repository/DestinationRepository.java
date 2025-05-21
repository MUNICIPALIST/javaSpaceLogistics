package com.space.destination.repository;

import com.space.destination.model.Destination;
import com.space.destination.model.DestinationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    List<Destination> findByType(DestinationType type);
    List<Destination> findByActive(boolean active);
    List<Destination> findByTypeAndActive(DestinationType type, boolean active);
} 