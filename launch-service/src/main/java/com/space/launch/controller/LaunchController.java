package com.space.launch.controller;

import com.space.launch.dto.LaunchDTO;
import com.space.launch.model.LaunchStatus;
import com.space.launch.service.LaunchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/launches")
@RequiredArgsConstructor
public class LaunchController {
    private final LaunchService launchService;

    @PostMapping
    public ResponseEntity<LaunchDTO> createLaunch(@RequestBody LaunchDTO launchDTO) {
        return ResponseEntity.ok(launchService.createLaunch(launchDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaunchDTO> getLaunch(@PathVariable UUID id) {
        return ResponseEntity.ok(launchService.getLaunch(id));
    }

    @GetMapping
    public ResponseEntity<List<LaunchDTO>> getAllLaunches() {
        return ResponseEntity.ok(launchService.getAllLaunches());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LaunchDTO> updateLaunchStatus(
        @PathVariable UUID id,
        @RequestParam LaunchStatus status
    ) {
        return ResponseEntity.ok(launchService.updateLaunchStatus(id, status));
    }

    @GetMapping("/cargo/{cargoId}")
    public ResponseEntity<List<LaunchDTO>> getLaunchesByCargo(@PathVariable UUID cargoId) {
        return ResponseEntity.ok(launchService.getLaunchesByCargo(cargoId));
    }

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<List<LaunchDTO>> getLaunchesByDestination(@PathVariable UUID destinationId) {
        return ResponseEntity.ok(launchService.getLaunchesByDestination(destinationId));
    }
} 