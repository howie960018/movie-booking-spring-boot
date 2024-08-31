package com.howie.moviebookingbackend.controller;

import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.service.BookingService;
import com.howie.moviebookingbackend.dto.BookingRequestDTO;
import com.howie.moviebookingbackend.dto.BookingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.howie.moviebookingbackend.dto.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        // 驗證座位號格式
        if (!isValidSeatNumbers(bookingRequestDTO.getSeatNumbers())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid seat number format. Expected format: 'A01', 'B05', etc."));
        }

        try {
            BookingResponseDTO bookingResponse = bookingService.createBooking(bookingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    private boolean isValidSeatNumbers(List<String> seatNumbers) {
        return seatNumbers.stream().allMatch(seatNumber -> seatNumber.matches("[A-Z]\\d{2}"));
    }
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


//    @PostMapping
//    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequest) {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String userEmail = authentication.getName();
//            BookingResponseDTO createdBooking = bookingService.createBooking(userEmail, bookingRequest);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/screening/{screeningId}")
    public ResponseEntity<List<Booking>> getBookingsByScreeningId(@PathVariable Long screeningId) {
        List<Booking> bookings = bookingService.getBookingsByScreeningId(screeningId);
        return ResponseEntity.ok(bookings);
    }
}