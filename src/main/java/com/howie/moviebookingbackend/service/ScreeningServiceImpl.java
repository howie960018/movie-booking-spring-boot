package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Movie;
import com.howie.moviebookingbackend.entity.Screening;
import com.howie.moviebookingbackend.entity.Seat;
import com.howie.moviebookingbackend.repository.MovieRepository;
import com.howie.moviebookingbackend.repository.ScreeningRepositor;
import com.howie.moviebookingbackend.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepositor screeningRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public ScreeningServiceImpl(ScreeningRepositor screeningRepository, MovieRepository movieRepository, SeatRepository seatRepository) {
        this.screeningRepository = screeningRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
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

    @Override
    @Transactional
    public Screening createScreeningWithSeats(Screening screening, int numberOfSeats) {
        Movie movie = movieRepository.findById(screening.getMovie().getId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        screening.setMovie(movie);
        screening.setAvailableSeats(numberOfSeats);

        Screening savedScreening = screeningRepository.save(screening);

        List<Seat> seats = new ArrayList<>();
        int seatsPerRow = 10;
        char rowLetter = 'A';

        for (int i = 0; i < numberOfSeats; i++) {
            int seatInRow = (i % seatsPerRow) + 1; // 從 1 開始
            if (seatInRow == 1 && i > 0) {
                rowLetter++;
            }
            String seatNumber = rowLetter + String.format("%02d", seatInRow); // 使用兩位數字

            Seat seat = new Seat();
            seat.setSeatNumber(seatNumber);
            seat.setBooked(false);
            seat.setScreening(savedScreening);
            seats.add(seat);
        }

        seatRepository.saveAll(seats);

        // 重新從資料庫獲取 Screening，確保 seats 被正確加載
        return screeningRepository.findById(savedScreening.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved screening"));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Seat> getAvailableSeats(Long screeningId) {
        return seatRepository.findByScreeningIdAndIsBooked(screeningId, false);
    }
}