package com.space.destination.controller;

import com.space.destination.dto.DestinationDTO;
import com.space.destination.model.DestinationType;
import com.space.destination.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {
    private final DestinationService destinationService;

    @PostMapping
    public ResponseEntity<DestinationDTO> createDestination(@RequestBody DestinationDTO destinationDTO) {
        return ResponseEntity.ok(destinationService.createDestination(destinationDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinationDTO> getDestination(@PathVariable UUID id) {
        return ResponseEntity.ok(destinationService.getDestination(id));
    }

    @GetMapping
    public ResponseEntity<List<DestinationDTO>> getAllDestinations() {
        return ResponseEntity.ok(destinationService.getAllDestinations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinationDTO> updateDestination(
        @PathVariable UUID id,
        @RequestBody DestinationDTO destinationDTO
    ) {
        return ResponseEntity.ok(destinationService.updateDestination(id, destinationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable UUID id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<DestinationDTO>> getDestinationsByType(@PathVariable DestinationType type) {
        return ResponseEntity.ok(destinationService.getDestinationsByType(type));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DestinationDTO>> getActiveDestinations() {
        return ResponseEntity.ok(destinationService.getActiveDestinations());
    }

    @GetMapping("/active/type/{type}")
    public ResponseEntity<List<DestinationDTO>> getActiveDestinationsByType(@PathVariable DestinationType type) {
        return ResponseEntity.ok(destinationService.getActiveDestinationsByType(type));
    }
} 