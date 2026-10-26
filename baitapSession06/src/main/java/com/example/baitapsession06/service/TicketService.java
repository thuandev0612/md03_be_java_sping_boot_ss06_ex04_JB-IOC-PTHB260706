package com.example.baitapsession06.service;

import com.example.baitapsession06.dto.TicketSummaryResponse;
import com.example.baitapsession06.repository.ParkingTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TicketService {
    private final ParkingTicketRepository ticketRepository;

    @Autowired
    public TicketService(ParkingTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getTodaySummary() {
        LocalDate today = LocalDate.now();
        return ticketRepository.findDailySummary(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }
}
