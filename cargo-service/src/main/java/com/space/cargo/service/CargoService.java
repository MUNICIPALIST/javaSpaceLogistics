package com.space.cargo.service;

import com.space.cargo.dto.CargoDTO;
import com.space.cargo.model.Cargo;
import com.space.cargo.model.CargoStatus;
import com.space.cargo.repository.CargoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public CargoDTO createCargo(CargoDTO cargoDTO) {
        Cargo cargo = new Cargo();
        cargo.setName(cargoDTO.getName());
        cargo.setWeight(cargoDTO.getWeight());
        cargo.setDestinationId(cargoDTO.getDestinationId());
        cargo.setStatus(CargoStatus.CREATED);
        
        Cargo savedCargo = cargoRepository.save(cargo);
        return convertToDTO(savedCargo);
    }

    public CargoDTO getCargo(UUID id) {
        return cargoRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Cargo not found"));
    }

    public List<CargoDTO> getAllCargo() {
        return cargoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CargoDTO updateCargoStatus(UUID id, CargoStatus newStatus) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found"));
        
        cargo.setStatus(newStatus);
        Cargo updatedCargo = cargoRepository.save(cargo);
        
        // Send event to Kafka
        kafkaTemplate.send("cargo-status-updates", 
            String.format("Cargo %s status updated to %s", id, newStatus));
        
        return convertToDTO(updatedCargo);
    }

    public List<CargoDTO> getCargoByDestination(UUID destinationId) {
        return cargoRepository.findByDestinationId(destinationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CargoDTO convertToDTO(Cargo cargo) {
        CargoDTO dto = new CargoDTO();
        dto.setId(cargo.getId());
        dto.setName(cargo.getName());
        dto.setWeight(cargo.getWeight());
        dto.setDestinationId(cargo.getDestinationId());
        dto.setStatus(cargo.getStatus());
        dto.setCreatedAt(cargo.getCreatedAt());
        return dto;
    }
} 