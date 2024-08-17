package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Screening;
import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningService {
    List<Screening> getAllScreenings();
    Screening getScreeningById(Long id);
    List<Screening> getScreeningsByMovieId(Long movieId);
    List<Screening> getScreeningsByDateRange(LocalDateTime start, LocalDateTime end);
    Screening saveScreening(Screening screening);
    void deleteScreening(Long id);
}