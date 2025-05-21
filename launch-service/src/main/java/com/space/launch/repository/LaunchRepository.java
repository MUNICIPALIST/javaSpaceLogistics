package com.space.launch.repository;

import com.space.launch.model.Launch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LaunchRepository extends JpaRepository<Launch, UUID> {
    List<Launch> findByCargoId(UUID cargoId);
    List<Launch> findByDestinationId(UUID destinationId);
} 