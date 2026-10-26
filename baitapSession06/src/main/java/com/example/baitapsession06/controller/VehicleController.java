package com.example.baitapsession06.controller;

import com.example.baitapsession06.dto.VehicleCreateRequest;
import com.example.baitapsession06.dto.VehicleResponse;
import com.example.baitapsession06.response.ApiResponse;
import com.example.baitapsession06.response.PageResponse;
import com.example.baitapsession06.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponse>>> getVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String keyword) {
        PageResponse<VehicleResponse> result = vehicleService.getPagedVehicles(page, size, sortBy, direction, keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách xe thành công", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(@Valid @RequestBody VehicleCreateRequest req) {
        VehicleResponse result = vehicleService.createVehicle(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo xe thành công", result));
    }
}
