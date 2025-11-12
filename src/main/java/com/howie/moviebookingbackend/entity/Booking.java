package com.howie.moviebookingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"user", "screening", "seats"})
@EqualsAndHashCode(exclude = {"user", "screening", "seats"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // 移除 @JsonIgnore，讓後台可看到使用者名稱/Email；User 本身沒有反向關聯不會遞迴
    private User user;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "booking-seats")
    private List<Seat> seats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    public enum BookingStatus {
        CONFIRMED, CANCELLED, PENDING
    }

    // 添加一個方法來管理雙向關係
    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setBooking(this);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.setBooking(null);
    }
}