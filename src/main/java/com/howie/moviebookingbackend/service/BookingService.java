package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Booking;
import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    List<Booking> getBookingsByCustomerEmail(String email);
    List<Booking> getBookingsByScreeningId(Long screeningId);
    Booking saveBooking(Booking booking);
    void deleteBooking(Long id);
}