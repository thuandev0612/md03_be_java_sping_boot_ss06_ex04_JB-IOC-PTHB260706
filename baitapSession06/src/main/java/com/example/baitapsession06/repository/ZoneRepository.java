package com.example.baitapsession06.repository;

import com.example.baitapsession06.model.Zone;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT z FROM Zone z WHERE z.id = :id")
    Optional<Zone> findByIdForUpdate(@Param("id") Long id);
}
