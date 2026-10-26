package com.example.baitapsession06.repository;

import com.example.baitapsession06.dto.TicketSummaryResponse;
import com.example.baitapsession06.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
    boolean existsByVehicleIdAndCheckOutTimeIsNull(Long vehicleId);

    Optional<ParkingTicket> findFirstByVehicleIdAndCheckOutTimeIsNullOrderByCheckInTimeDescIdDesc(Long vehicleId);

    @Query("""
            SELECT new com.example.baitapsession06.dto.TicketSummaryResponse(
                t.id, v.licensePlate, z.name, t.checkInTime, t.checkOutTime)
            FROM ParkingTicket t
            JOIN t.vehicle v
            JOIN t.zone z
            WHERE t.checkInTime >= :startOfDay AND t.checkInTime < :nextDay
            ORDER BY t.checkInTime DESC, t.id DESC
            """)
    List<TicketSummaryResponse> findDailySummary(@Param("startOfDay") LocalDateTime startOfDay,
                                              @Param("nextDay") LocalDateTime nextDay);
}
