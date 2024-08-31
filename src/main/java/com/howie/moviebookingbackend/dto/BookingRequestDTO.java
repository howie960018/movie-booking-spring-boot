package com.howie.moviebookingbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Long screeningId;
    private Long seatId;
}