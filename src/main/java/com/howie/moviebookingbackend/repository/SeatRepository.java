package com.howie.moviebookingbackend.repository;

import com.howie.moviebookingbackend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreeningId(Long screeningId);
    List<Seat> findByScreeningIdAndIsBooked(Long screeningId, boolean isBooked);
}