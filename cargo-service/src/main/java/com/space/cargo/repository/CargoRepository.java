package com.space.cargo.repository;

import com.space.cargo.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CargoRepository extends JpaRepository<Cargo, UUID> {
    List<Cargo> findByDestinationId(UUID destinationId);
} 