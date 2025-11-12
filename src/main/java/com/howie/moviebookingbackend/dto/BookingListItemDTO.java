package com.howie.moviebookingbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingListItemDTO {
    private Long bookingId;
    private String movieTitle;
    private LocalDateTime screeningTime;
    private List<String> seatNumbers;
    private LocalDateTime bookingTime;
    private String status;
    private Long userId; // 僅供後台顯示或篩選
    private String userEmail; // 管理後台使用
}

