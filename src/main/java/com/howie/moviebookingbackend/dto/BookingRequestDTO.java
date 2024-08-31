package com.howie.moviebookingbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Long screeningId;
    private List<String> seatNumbers; // 新增的屬性，用來記錄用戶選擇的座位編號
}