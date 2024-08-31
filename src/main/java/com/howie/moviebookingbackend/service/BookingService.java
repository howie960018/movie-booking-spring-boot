package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.dto.BookingRequestDTO;
import com.howie.moviebookingbackend.dto.BookingResponseDTO;
import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    List<Booking> getBookingsByUserId(Long userId);
    List<Booking> getBookingsByScreeningId(Long screeningId);

    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);
    void cancelBooking(Long id);


}