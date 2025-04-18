package com.spacelogistics.cargo.service.impl;

import com.spacelogistics.cargo.dto.CargoDto;
import com.spacelogistics.cargo.model.Cargo;
import com.spacelogistics.cargo.repository.CargoRepository;
import com.spacelogistics.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoServiceImpl implements CargoService {
    
    private final CargoRepository cargoRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Override
    public List<CargoDto> getAllCargos() {
        return cargoRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CargoDto getCargoById(Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));
        return mapToDto(cargo);
    }
    
    @Override
    public CargoDto createCargo(CargoDto cargoDto) {
        Cargo cargo = mapToEntity(cargoDto);
        Cargo savedCargo = cargoRepository.save(cargo);
        
        // Send notification to Kafka
        kafkaTemplate.send("cargo-events", "Created cargo with ID: " + savedCargo.getId());
        
        return mapToDto(savedCargo);
    }
    
    @Override
    public CargoDto updateCargo(Long id, CargoDto cargoDto) {
        Cargo existingCargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));
        
        existingCargo.setName(cargoDto.getName());
        existingCargo.setDescription(cargoDto.getDescription());
        existingCargo.setWeight(cargoDto.getWeight());
        existingCargo.setType(cargoDto.getType());
        
        Cargo updatedCargo = cargoRepository.save(existingCargo);
        
        // Send notification to Kafka
        kafkaTemplate.send("cargo-events", "Updated cargo with ID: " + updatedCargo.getId());
        
        return mapToDto(updatedCargo);
    }
    
    @Override
    public void deleteCargo(Long id) {
        if (!cargoRepository.existsById(id)) {
            throw new RuntimeException("Cargo not found with id: " + id);
        }
        
        cargoRepository.deleteById(id);
        
        // Send notification to Kafka
        kafkaTemplate.send("cargo-events", "Deleted cargo with ID: " + id);
    }
    
    private CargoDto mapToDto(Cargo cargo) {
        return new CargoDto(
                cargo.getId(),
                cargo.getName(),
                cargo.getDescription(),
                cargo.getWeight(),
                cargo.getType()
        );
    }
    
    private Cargo mapToEntity(CargoDto cargoDto) {
        Cargo cargo = new Cargo();
        cargo.setName(cargoDto.getName());
        cargo.setDescription(cargoDto.getDescription());
        cargo.setWeight(cargoDto.getWeight());
        cargo.setType(cargoDto.getType());
        return cargo;
    }
} 