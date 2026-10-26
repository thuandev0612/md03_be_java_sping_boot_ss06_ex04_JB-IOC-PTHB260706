package com.example.baitapsession06.service;

import com.example.baitapsession06.dto.VehicleCreateRequest;
import com.example.baitapsession06.dto.VehicleResponse;
import com.example.baitapsession06.model.Vehicle;
import com.example.baitapsession06.repository.VehicleRepository;
import com.example.baitapsession06.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class VehicleService {
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "id", "licensePlate", "licensePlate", "color", "color",
            "vehicleType", "type", "type", "type");

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<VehicleResponse> getPagedVehicles(
            int page, int size, String sortBy, String direction, String keyword) {
        int safePage = Math.max(page, 0);
        if (size <= 0 || (long) safePage * size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("page/size không hợp lệ; size phải lớn hơn 0");
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank() && direction != null && !direction.isBlank()) {
            String field = SORT_FIELDS.get(sortBy.trim());
            if (field == null) {
                throw new IllegalArgumentException("sortBy hợp lệ: id, licensePlate, color, vehicleType");
            }
            sort = Sort.by(Sort.Direction.fromString(direction.trim()), field);
        }

        String search = keyword == null || keyword.isBlank() ? null : keyword.trim();
        Pageable pageable = PageRequest.of(safePage, size, sort);
        Page<VehicleResponse> vehicles = vehicleRepository.findAllByKeyword(search, pageable);
        return new PageResponse<>(vehicles.getContent(), vehicles.getNumber(), vehicles.getSize(),
                vehicles.getTotalElements(), vehicles.getTotalPages(), vehicles.isLast());
    }

    @Transactional
    public VehicleResponse createVehicle(VehicleCreateRequest req) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(req.getLicensePlate());
        vehicle.setColor(req.getColor());
        vehicle.setType(req.getVehicleType());
        Vehicle saved = vehicleRepository.save(vehicle);
        return new VehicleResponse(saved.getId(), saved.getLicensePlate(), saved.getColor(), saved.getType());
    }
}
