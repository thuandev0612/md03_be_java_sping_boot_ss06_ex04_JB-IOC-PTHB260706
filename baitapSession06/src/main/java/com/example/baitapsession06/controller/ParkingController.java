package com.example.baitapsession06.controller;

import com.example.baitapsession06.dto.TicketRequest;
import com.example.baitapsession06.dto.TicketResponse;
import com.example.baitapsession06.dto.TicketSummaryResponse;
import com.example.baitapsession06.response.ApiResponse;
import com.example.baitapsession06.service.ParkingService;
import com.example.baitapsession06.service.TicketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class ParkingController {
    private final ParkingService parkingService;
    private final TicketService ticketService;

    @Autowired
    public ParkingController(ParkingService parkingService, TicketService ticketService) {
        this.parkingService = parkingService;
        this.ticketService = ticketService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<TicketSummaryResponse>>> getTodaySummary() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Danh sách vé xe trong ngày",
                ticketService.getTodaySummary()));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<TicketResponse>> checkIn(@Valid @RequestBody TicketRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Gửi xe thành công", parkingService.checkIn(req)));
    }

    @PutMapping("/check-out/{vehicleId}")
    public ResponseEntity<ApiResponse<TicketResponse>> checkOut(@PathVariable @Positive Long vehicleId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Trả xe thành công", parkingService.checkOut(vehicleId)));
    }
}
