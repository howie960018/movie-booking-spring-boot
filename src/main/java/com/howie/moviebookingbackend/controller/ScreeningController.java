package com.howie.moviebookingbackend.controller;

import com.howie.moviebookingbackend.entity.Movie;
import com.howie.moviebookingbackend.entity.Screening;
import com.howie.moviebookingbackend.entity.Seat;
import com.howie.moviebookingbackend.service.ScreeningService;
import com.howie.moviebookingbackend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    @Autowired
    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @Autowired
    private MovieRepository movieRepository;


    @GetMapping
    public ResponseEntity<List<Screening>> getAllScreenings() {
        return ResponseEntity.ok(screeningService.getAllScreenings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Screening> getScreeningById(@PathVariable Long id) {
        Screening screening = screeningService.getScreeningById(id);
        if (screening != null) {
            return ResponseEntity.ok(screening);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Screening>> getScreeningsByMovieId(@PathVariable Long movieId) {
        return ResponseEntity.ok(screeningService.getScreeningsByMovieId(movieId));
    }

    @GetMapping("/daterange")
    public ResponseEntity<List<Screening>> getScreeningsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(screeningService.getScreeningsByDateRange(start, end));
    }

    @PostMapping
    public ResponseEntity<?> createScreening(@RequestBody Map<String, Object> payload) {
        try {
            Long movieId = Long.valueOf(payload.get("movieId").toString());
            LocalDateTime screeningTime = LocalDateTime.parse(payload.get("screeningTime").toString());

            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            Screening screening = new Screening();
            screening.setMovie(movie);
            screening.setScreeningTime(screeningTime);

            Screening createdScreening = screeningService.saveScreening(screening);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdScreening);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating screening: " + e.getMessage());
        }
    }


    @PostMapping("/with-seats")
    public ResponseEntity<?> createScreeningWithSeats(@RequestBody Map<String, Object> payload, @RequestParam int numberOfSeats) {
        try {
            Long movieId = Long.valueOf(payload.get("movieId").toString());
            LocalDateTime screeningTime = LocalDateTime.parse(payload.get("screeningTime").toString());

            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            Screening screening = new Screening();
            screening.setMovie(movie);
            screening.setScreeningTime(screeningTime);

            Screening createdScreening = screeningService.createScreeningWithSeats(screening, numberOfSeats);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdScreening);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating screening with seats: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/available-seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long id) {
        List<Seat> availableSeats = screeningService.getAvailableSeats(id);
        return ResponseEntity.ok(availableSeats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreening(@PathVariable Long id) {
        try {
            screeningService.deleteScreening(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}