package com.example.baitapsession06.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketSummaryResponse {
    private final Long id;
    private final String licensePlate;
    private final String zoneName;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
}
