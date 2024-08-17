package com.howie.moviebookingbackend.repository;

import com.howie.moviebookingbackend.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScreeningRepositor extends JpaRepository<Screening, Long> {
    List<Screening> findByMovieId(Long movieId);
    List<Screening> findByScreeningTimeBetween(LocalDateTime start, LocalDateTime end);
}