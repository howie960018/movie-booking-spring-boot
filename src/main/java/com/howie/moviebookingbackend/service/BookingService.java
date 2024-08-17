package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Booking;
import java.util.List;
import com.howie.moviebookingbackend.dto.BookingRequestDTO;

public interface BookingService {
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    List<Booking> getBookingsByUserId(Long userId);
    List<Booking> getBookingsByScreeningId(Long screeningId);
    Booking createBooking(BookingRequestDTO bookingRequest);
    void cancelBooking(Long id);
}