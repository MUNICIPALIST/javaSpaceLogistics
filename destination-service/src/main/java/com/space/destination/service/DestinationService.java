package com.space.destination.service;

import com.space.destination.dto.DestinationDTO;
import com.space.destination.model.Destination;
import com.space.destination.model.DestinationType;
import com.space.destination.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public DestinationDTO createDestination(DestinationDTO destinationDTO) {
        Destination destination = new Destination();
        destination.setName(destinationDTO.getName());
        destination.setType(destinationDTO.getType());
        destination.setCoordinates(destinationDTO.getCoordinates());
        destination.setCapacity(destinationDTO.getCapacity());
        destination.setActive(true);

        destination = destinationRepository.save(destination);
        kafkaTemplate.send("destination-events", "New destination created: " + destination.getId());
        
        return convertToDTO(destination);
    }

    public DestinationDTO getDestination(UUID id) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Destination not found"));
        return convertToDTO(destination);
    }

    public List<DestinationDTO> getAllDestinations() {
        return destinationRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public DestinationDTO updateDestination(UUID id, DestinationDTO destinationDTO) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Destination not found"));

        destination.setName(destinationDTO.getName());
        destination.setType(destinationDTO.getType());
        destination.setCoordinates(destinationDTO.getCoordinates());
        destination.setCapacity(destinationDTO.getCapacity());
        destination.setActive(destinationDTO.isActive());

        destination = destinationRepository.save(destination);
        kafkaTemplate.send("destination-events", "Destination updated: " + id);
        
        return convertToDTO(destination);
    }

    @Transactional
    public void deleteDestination(UUID id) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Destination not found"));
        
        destination.setActive(false);
        destinationRepository.save(destination);
        kafkaTemplate.send("destination-events", "Destination deactivated: " + id);
    }

    public List<DestinationDTO> getDestinationsByType(DestinationType type) {
        return destinationRepository.findByType(type).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<DestinationDTO> getActiveDestinations() {
        return destinationRepository.findByActive(true).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<DestinationDTO> getActiveDestinationsByType(DestinationType type) {
        return destinationRepository.findByTypeAndActive(type, true).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private DestinationDTO convertToDTO(Destination destination) {
        DestinationDTO dto = new DestinationDTO();
        dto.setId(destination.getId());
        dto.setName(destination.getName());
        dto.setType(destination.getType());
        dto.setCoordinates(destination.getCoordinates());
        dto.setCapacity(destination.getCapacity());
        dto.setActive(destination.isActive());
        dto.setCreatedAt(destination.getCreatedAt());
        dto.setLastUpdated(destination.getLastUpdated());
        return dto;
    }
} 