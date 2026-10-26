package com.example.baitapsession06.service;

import com.example.baitapsession06.dto.TicketRequest;
import com.example.baitapsession06.dto.TicketResponse;
import com.example.baitapsession06.model.ParkingTicket;
import com.example.baitapsession06.model.Vehicle;
import com.example.baitapsession06.model.Zone;
import com.example.baitapsession06.repository.ParkingTicketRepository;
import com.example.baitapsession06.repository.VehicleRepository;
import com.example.baitapsession06.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class ParkingService {
    private final VehicleRepository vehicleRepository;
    private final ZoneRepository zoneRepository;
    private final ParkingTicketRepository ticketRepository;

    @Autowired
    public ParkingService(VehicleRepository vehicleRepository, ZoneRepository zoneRepository,
                          ParkingTicketRepository ticketRepository) {
        this.vehicleRepository = vehicleRepository;
        this.zoneRepository = zoneRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public TicketResponse checkIn(TicketRequest req) {
        Vehicle vehicle = vehicleRepository.findByIdForUpdate(req.getVehicleId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy xe"));
        Zone zone = zoneRepository.findByIdForUpdate(req.getZoneId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khu vực đỗ xe"));
        if (ticketRepository.existsByVehicleIdAndCheckOutTimeIsNull(vehicle.getId())) {
            throw new IllegalStateException("Xe đang ở trong bãi");
        }
        if (zone.getOccupiedSpots() >= zone.getCapacity()) {
            throw new IllegalStateException("Khu vực đã hết chỗ trống");
        }

        ParkingTicket ticket = new ParkingTicket();
        ticket.setVehicle(vehicle);
        ticket.setZone(zone);
        ticket.setCheckInTime(LocalDateTime.now());
        zone.setOccupiedSpots(zone.getOccupiedSpots() + 1);
        zoneRepository.save(zone);
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse checkOut(Long vehicleId) {
        vehicleRepository.findByIdForUpdate(vehicleId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy xe"));
        ParkingTicket ticket = ticketRepository
                .findFirstByVehicleIdAndCheckOutTimeIsNullOrderByCheckInTimeDescIdDesc(vehicleId)
                .orElseThrow(() -> new NoSuchElementException("Xe không có vé đang gửi"));
        Zone zone = zoneRepository.findByIdForUpdate(ticket.getZone().getId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khu vực đỗ xe"));
        if (zone.getOccupiedSpots() <= 0) {
            throw new IllegalStateException("Số chỗ đã dùng của khu vực không hợp lệ");
        }

        ticket.setCheckOutTime(LocalDateTime.now());
        zone.setOccupiedSpots(zone.getOccupiedSpots() - 1);
        zoneRepository.save(zone);
        return toResponse(ticketRepository.save(ticket));
    }

    private TicketResponse toResponse(ParkingTicket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getVehicle().getLicensePlate(),
                ticket.getZone().getName(), ticket.getCheckInTime(), ticket.getCheckOutTime());
    }
}
