package com.howie.moviebookingbackend.controller;

import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.service.BookingService;
import com.howie.moviebookingbackend.dto.BookingRequestDTO;
import com.howie.moviebookingbackend.dto.BookingResponseDTO;
import com.howie.moviebookingbackend.dto.BookingListItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.howie.moviebookingbackend.dto.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.howie.moviebookingbackend.entity.Seat;
import com.howie.moviebookingbackend.repository.UserRepository;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @Autowired
    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
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
    public ResponseEntity<List<BookingListItemDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(toListItemDTOs(bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingListItemDTO> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        if (booking != null) {
            return ResponseEntity.ok(toListItemDTO(booking));
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
    public ResponseEntity<List<BookingListItemDTO>> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(toListItemDTOs(bookings));
    }

    @GetMapping("/screening/{screeningId}")
    public ResponseEntity<List<BookingListItemDTO>> getBookingsByScreeningId(@PathVariable Long screeningId) {
        List<Booking> bookings = bookingService.getBookingsByScreeningId(screeningId);
        return ResponseEntity.ok(toListItemDTOs(bookings));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingListItemDTO>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(toListItemDTOs(bookings));
    }

    private BookingListItemDTO toListItemDTO(Booking booking) {
        BookingListItemDTO dto = new BookingListItemDTO();
        dto.setBookingId(booking.getId());
        dto.setMovieTitle(booking.getScreening().getMovie().getTitle());
        dto.setScreeningTime(booking.getScreening().getScreeningTime());
        dto.setSeatNumbers(booking.getSeats().stream().map(Seat::getSeatNumber).toList());
        dto.setBookingTime(booking.getBookingTime());
        dto.setStatus(booking.getStatus().name());
        dto.setUserId(booking.getUser().getId());
        dto.setUserEmail(booking.getUser().getEmail());
        return dto;
    }

    private List<BookingListItemDTO> toListItemDTOs(List<Booking> list) {
        return list.stream().map(this::toListItemDTO).toList();
    }
}