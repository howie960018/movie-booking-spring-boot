package com.howie.moviebookingbackend.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private Long userId;
    private Long screeningId;
    private Long seatId;
}