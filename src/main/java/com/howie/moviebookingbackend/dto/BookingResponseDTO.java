package com.howie.moviebookingbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long bookingId;
    private String movieTitle;
    private LocalDateTime screeningTime;
    private List<String> seatNumbers;
    private LocalDateTime bookingTime;
    private String userEmail;
    private int totalSeats;
    private String status;
}