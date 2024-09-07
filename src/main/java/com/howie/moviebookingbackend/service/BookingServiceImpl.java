package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.dto.BookingResponseDTO;
import com.howie.moviebookingbackend.dto.BookingRequestDTO;
import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.entity.Screening;
import com.howie.moviebookingbackend.entity.Seat;
import com.howie.moviebookingbackend.entity.User;
import com.howie.moviebookingbackend.repository.BookingRepository;
import com.howie.moviebookingbackend.repository.ScreeningRepositor;
import com.howie.moviebookingbackend.repository.SeatRepository;
import com.howie.moviebookingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Screening screening = screeningRepository.findById(bookingRequestDTO.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        List<Seat> seats = seatRepository.findAllByScreeningAndSeatNumberIn(
                screening, bookingRequestDTO.getSeatNumbers());

        if (seats.size() != bookingRequestDTO.getSeatNumbers().size()) {
            throw new RuntimeException("One or more seats not found");
        }

        // 檢查座位是否已被預訂
        List<Seat> bookedSeats = seats.stream().filter(Seat::isBooked).toList();
        if (!bookedSeats.isEmpty()) {
            throw new RuntimeException("One or more seats are already booked");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScreening(screening);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.PENDING);

        // 先保存 Booking 以獲得 ID
        booking = bookingRepository.save(booking);

        int requestedSeats = bookingRequestDTO.getSeatNumbers().size();
        if (screening.getAvailableSeats() < requestedSeats) {
            throw new RuntimeException("Not enough available seats");
        }

        // 然後設置 Seat 的 Booking 引用
        Booking finalBooking = booking;
        seats.forEach(seat -> {
            seat.setBooked(true);
            seat.setBooking(finalBooking);
        });
        // 減少可用座位數量
        screening.setAvailableSeats(screening.getAvailableSeats() - requestedSeats);

        seatRepository.saveAll(seats);

        // 設置 Booking 的 seats
        booking.setSeats(seats);

        return createBookingResponseDTO(booking);
    }
    private BookingResponseDTO createBookingResponseDTO(Booking booking) {
        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(booking.getId());
        responseDTO.setMovieTitle(booking.getScreening().getMovie().getTitle());
        responseDTO.setScreeningTime(booking.getScreening().getScreeningTime());
        responseDTO.setSeatNumbers(booking.getSeats().stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList()));
        responseDTO.setBookingTime(booking.getBookingTime());
        responseDTO.setUserEmail(booking.getUser().getEmail());
        responseDTO.setTotalSeats(booking.getSeats().size());
        responseDTO.setStatus(booking.getStatus().toString());
        return responseDTO;
    }
    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 释放与该预订关联的所有座位
        List<Seat> seats = booking.getSeats();
        seats.forEach(seat -> {
            seat.setBooked(false);
            seat.setBooking(null);
        });
        seatRepository.saveAll(seats);

        // 更新放映的可用座位數
        Screening screening = booking.getScreening();
        screening.setAvailableSeats(screening.getAvailableSeats() + seats.size());
        screeningRepository.save(screening);

        bookingRepository.delete(booking);
    }
}