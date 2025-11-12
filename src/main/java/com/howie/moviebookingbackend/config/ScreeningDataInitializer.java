package com.howie.moviebookingbackend.config;

import com.howie.moviebookingbackend.entity.Movie;
import com.howie.moviebookingbackend.service.ScreeningService;
import com.howie.moviebookingbackend.repository.MovieRepository;
import com.howie.moviebookingbackend.repository.ScreeningRepositor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Order(15)
public class ScreeningDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ScreeningDataInitializer.class);

    private final MovieRepository movieRepository;
    private final ScreeningService screeningService;
    private final ScreeningRepositor screeningRepository;

    public ScreeningDataInitializer(MovieRepository movieRepository,
                                    ScreeningService screeningService,
                                    ScreeningRepositor screeningRepository) {
        this.movieRepository = movieRepository;
        this.screeningService = screeningService;
        this.screeningRepository = screeningRepository;
    }

    @Override
    public void run(String... args) {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            log.info("No movies found for screening seeding.");
            return;
        }
        int created = 0;
        LocalDateTime now = LocalDateTime.now();
        // 未來三天，每天三個固定時間檔：10:00、14:30、19:30
        LocalTime[] slots = new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(14, 30), LocalTime.of(19, 30)};

        for (Movie movie : movies) {
            for (int dayOffset = 0; dayOffset < 3; dayOffset++) {
                LocalDateTime dayBase = now.plusDays(dayOffset).withHour(0).withMinute(0).withSecond(0).withNano(0);
                for (LocalTime slot : slots) {
                    LocalDateTime screeningTime = dayBase.withHour(slot.getHour()).withMinute(slot.getMinute());
                    if (screeningRepository.existsByMovieIdAndScreeningTime(movie.getId(), screeningTime)) {
                        continue; // skip duplicates
                    }
                    screeningService.createScreeningWithSeats(movie.getId(), screeningTime, 100);
                    created++;
                }
            }
        }
        log.info("Seeded {} screenings.", created);
    }
}

