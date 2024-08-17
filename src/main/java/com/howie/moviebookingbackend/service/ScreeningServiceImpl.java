package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Movie;
import com.howie.moviebookingbackend.entity.Screening;
import com.howie.moviebookingbackend.repository.MovieRepository;
import com.howie.moviebookingbackend.repository.ScreeningRepositor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepositor screeningRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ScreeningServiceImpl(ScreeningRepositor screeningRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Screening> getAllScreenings() {
        return screeningRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Screening getScreeningById(Long id) {
        return screeningRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Screening> getScreeningsByMovieId(Long movieId) {
        return screeningRepository.findByMovieId(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Screening> getScreeningsByDateRange(LocalDateTime start, LocalDateTime end) {
        return screeningRepository.findByScreeningTimeBetween(start, end);
    }

    @Override
    @Transactional
    public Screening saveScreening(Screening screening) {
        return movieRepository.findById(screening.getMovie().getId())
                .map(movie -> {
                    screening.setMovie(movie);
                    return screeningRepository.save(screening);
                })
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    @Override
    @Transactional
    public void deleteScreening(Long id) {
        screeningRepository.deleteById(id);
    }
}