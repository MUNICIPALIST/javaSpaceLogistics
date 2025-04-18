package com.spacelogistics.cargo.controller;

import com.spacelogistics.cargo.dto.CargoDto;
import com.spacelogistics.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
@RequiredArgsConstructor
public class CargoController {
    
    private final CargoService cargoService;
    
    @GetMapping
    public ResponseEntity<List<CargoDto>> getAllCargos() {
        return ResponseEntity.ok(cargoService.getAllCargos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CargoDto> getCargoById(@PathVariable Long id) {
        return ResponseEntity.ok(cargoService.getCargoById(id));
    }
    
    @PostMapping
    public ResponseEntity<CargoDto> createCargo(@RequestBody CargoDto cargoDto) {
        return new ResponseEntity<>(cargoService.createCargo(cargoDto), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CargoDto> updateCargo(@PathVariable Long id, @RequestBody CargoDto cargoDto) {
        return ResponseEntity.ok(cargoService.updateCargo(id, cargoDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(@PathVariable Long id) {
        cargoService.deleteCargo(id);
        return ResponseEntity.noContent().build();
    }
} 