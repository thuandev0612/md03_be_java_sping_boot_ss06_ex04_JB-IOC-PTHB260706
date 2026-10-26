package com.example.baitapsession06.dto;

import com.example.baitapsession06.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VehicleResponse {
    private final Long id;
    private final String licensePlate;
    private final String color;
    private final VehicleType vehicleType;
}
