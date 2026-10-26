package com.example.baitapsession06.repository;

import com.example.baitapsession06.dto.VehicleResponse;
import com.example.baitapsession06.model.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id")
    Optional<Vehicle> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT new com.example.baitapsession06.dto.VehicleResponse(v.id, v.licensePlate, v.color, v.type)
            FROM Vehicle v
            WHERE (:keyword IS NULL OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
            """)
    Page<VehicleResponse> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
