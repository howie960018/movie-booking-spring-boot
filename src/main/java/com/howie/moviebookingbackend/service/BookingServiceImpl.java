package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.entity.Screening;
import com.howie.moviebookingbackend.entity.Seat;
import com.howie.moviebookingbackend.entity.User;
import com.howie.moviebookingbackend.repository.BookingRepository;
import com.howie.moviebookingbackend.repository.ScreeningRepositor;
import com.howie.moviebookingbackend.repository.SeatRepository;
import com.howie.moviebookingbackend.repository.UserRepository;
import com.howie.moviebookingbackend.dto.BookingRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScreeningRepositor screeningRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ScreeningRepositor screeningRepository,
                              UserRepository userRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.screeningRepository = screeningRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByScreeningId(Long screeningId) {
        return bookingRepository.findByScreeningId(screeningId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Booking createBooking(BookingRequestDTO bookingRequest) {
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Screening screening = screeningRepository.findById(bookingRequest.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        Seat seat = seatRepository.findById(bookingRequest.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (seat.isBooked()) {
            throw new RuntimeException("Seat is already booked");
        }

        seat.setBooked(true);
        seatRepository.save(seat);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScreening(screening);
        booking.setSeat(seat);
        booking.setBookingTime(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Seat seat = booking.getSeat();
        seat.setBooked(false);
        seatRepository.save(seat);

        bookingRepository.delete(booking);
    }
}