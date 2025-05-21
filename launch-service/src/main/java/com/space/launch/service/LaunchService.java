package com.space.launch.service;

import com.space.common.dto.CargoDTO;
import com.space.common.dto.CargoStatus;
import com.space.launch.client.CargoServiceClient;
import com.space.launch.dto.LaunchDTO;
import com.space.launch.model.Launch;
import com.space.launch.model.LaunchStatus;
import com.space.launch.repository.LaunchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaunchService {
    private final LaunchRepository launchRepository;
    private final CargoServiceClient cargoServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public LaunchDTO createLaunch(LaunchDTO launchDTO) {
        // Verify cargo exists
        CargoDTO cargo = cargoServiceClient.getCargo(launchDTO.getCargoId());
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo not found");
        }

        Launch launch = new Launch();
        launch.setRocketName(launchDTO.getRocketName());
        launch.setCargoId(launchDTO.getCargoId());
        launch.setDestinationId(launchDTO.getDestinationId());
        launch.setStatus(LaunchStatus.SCHEDULED);

        launch = launchRepository.save(launch);
        return convertToDTO(launch);
    }

    public LaunchDTO getLaunch(UUID id) {
        Launch launch = launchRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Launch not found"));
        return convertToDTO(launch);
    }

    public List<LaunchDTO> getAllLaunches() {
        return launchRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public LaunchDTO updateLaunchStatus(UUID id, LaunchStatus status) {
        Launch launch = launchRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Launch not found"));

        launch.setStatus(status);
        launch = launchRepository.save(launch);

        // If launch is completed, update cargo status
        if (status == LaunchStatus.COMPLETED) {
            cargoServiceClient.updateCargoStatus(launch.getCargoId(), "IN_TRANSIT");
            kafkaTemplate.send("launch-events", "Launch completed: " + id);
        } else if (status == LaunchStatus.FAILED) {
            kafkaTemplate.send("launch-events", "Launch failed: " + id);
        }

        return convertToDTO(launch);
    }

    public List<LaunchDTO> getLaunchesByCargo(UUID cargoId) {
        return launchRepository.findByCargoId(cargoId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<LaunchDTO> getLaunchesByDestination(UUID destinationId) {
        return launchRepository.findByDestinationId(destinationId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private LaunchDTO convertToDTO(Launch launch) {
        LaunchDTO dto = new LaunchDTO();
        dto.setId(launch.getId());
        dto.setRocketName(launch.getRocketName());
        dto.setCargoId(launch.getCargoId());
        dto.setDestinationId(launch.getDestinationId());
        dto.setStatus(launch.getStatus());
        dto.setCreatedAt(launch.getCreatedAt());
        return dto;
    }
} 