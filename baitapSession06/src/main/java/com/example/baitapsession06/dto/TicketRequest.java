package com.example.baitapsession06.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketRequest {
    @NotNull
    @Positive
    private Long vehicleId;

    @NotNull
    @Positive
    private Long zoneId;
}
