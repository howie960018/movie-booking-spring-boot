package com.howie.moviebookingbackend.controller;

import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final BookingRepository bookingRepository;

    public PaymentController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // 假付款：將訂單狀態由 PENDING -> CONFIRMED
    @PostMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmPayment(@PathVariable Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            return ResponseEntity.ok("Already confirmed");
        }
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        return ResponseEntity.ok("Payment confirmed");
    }
}

