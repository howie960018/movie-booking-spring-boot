package com.howie.moviebookingbackend.repository;

import com.howie.moviebookingbackend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByScreeningId(Long screeningId);

    List<Booking> findByUserEmail(String email);

    List<Booking> findByUserId(Long userId);
}