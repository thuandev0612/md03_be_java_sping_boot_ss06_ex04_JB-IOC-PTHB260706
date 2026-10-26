package com.example.baitapsession06.dto;

import com.example.baitapsession06.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleCreateRequest {
    @NotBlank
    @Size(max = 255)
    private String licensePlate;

    @Size(max = 255)
    private String color;

    @NotNull
    private VehicleType vehicleType;
}
