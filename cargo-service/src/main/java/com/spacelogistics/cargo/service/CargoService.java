package com.spacelogistics.cargo.service;

import com.spacelogistics.cargo.dto.CargoDto;

import java.util.List;

public interface CargoService {
    List<CargoDto> getAllCargos();
    CargoDto getCargoById(Long id);
    CargoDto createCargo(CargoDto cargoDto);
    CargoDto updateCargo(Long id, CargoDto cargoDto);
    void deleteCargo(Long id);
} 