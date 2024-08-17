package com.howie.moviebookingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "screenings")
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "screening_time", nullable = false)
    private LocalDateTime screeningTime;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();


    @Column(name = "available_seats", nullable = false)
    private int availableSeats = 100;  // 添加 availableSeats 字段


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public LocalDateTime getScreeningTime() {
        return screeningTime;
    }

    public void setScreeningTime(LocalDateTime screeningTime) {
        this.screeningTime = screeningTime;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setScreening(this);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.setScreening(null);
    }

    public int getAvailableSeats() {  // 添加 getter 方法
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {  // 添加 setter 方法
        this.availableSeats = availableSeats;
    }
}