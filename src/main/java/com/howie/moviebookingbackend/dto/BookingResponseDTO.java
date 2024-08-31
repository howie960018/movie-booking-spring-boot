package com.howie.moviebookingbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long bookingId;
    private String movieTitle;
    private LocalDateTime screeningTime;
    private String seatNumber;
    private LocalDateTime bookingTime;
    private String userEmail;
}