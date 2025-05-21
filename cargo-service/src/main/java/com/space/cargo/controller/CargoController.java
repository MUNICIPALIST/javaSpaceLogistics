package com.space.cargo.controller;

import com.space.cargo.dto.CargoDTO;
import com.space.cargo.model.CargoStatus;
import com.space.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cargo")
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;

    @PostMapping
    public ResponseEntity<CargoDTO> createCargo(@RequestBody CargoDTO cargoDTO) {
        return ResponseEntity.ok(cargoService.createCargo(cargoDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDTO> getCargo(@PathVariable UUID id) {
        return ResponseEntity.ok(cargoService.getCargo(id));
    }

    @GetMapping
    public ResponseEntity<List<CargoDTO>> getAllCargo() {
        return ResponseEntity.ok(cargoService.getAllCargo());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CargoDTO> updateCargoStatus(
            @PathVariable UUID id,
            @RequestParam CargoStatus status) {
        return ResponseEntity.ok(cargoService.updateCargoStatus(id, status));
    }

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<List<CargoDTO>> getCargoByDestination(@PathVariable UUID destinationId) {
        return ResponseEntity.ok(cargoService.getCargoByDestination(destinationId));
    }
} 