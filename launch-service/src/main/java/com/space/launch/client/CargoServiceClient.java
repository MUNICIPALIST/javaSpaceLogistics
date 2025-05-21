package com.space.launch.client;

import com.space.common.dto.CargoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "cargo-service")
public interface CargoServiceClient {
    @GetMapping("/cargo/{id}")
    CargoDTO getCargo(@PathVariable("id") UUID id);

    @PutMapping("/cargo/{id}/status")
    CargoDTO updateCargoStatus(
        @PathVariable("id") UUID id,
        @RequestParam("status") String status
    );
} 