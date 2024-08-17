package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Booking;
import com.howie.moviebookingbackend.entity.Screening;
import com.howie.moviebookingbackend.repository.BookingRepository;
import com.howie.moviebookingbackend.repository.ScreeningRepositor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScreeningRepositor screeningRepositor;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ScreeningRepositor screeningRepositor) {
        this.bookingRepository = bookingRepository;
        this.screeningRepositor = screeningRepositor;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByScreeningId(Long screeningId) {
        return bookingRepository.findByScreeningId(screeningId);
    }

    @Override
    @Transactional
    public Booking saveBooking(Booking booking) {
        // 原有的邏輯保持不變
        booking.setBookingTime(LocalDateTime.now());

        Screening screening = screeningRepositor.findById(booking.getScreening().getId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        if (screening.getAvailableSeats() < booking.getNumberOfSeats()) {
            throw new RuntimeException("Not enough seats available");
        }

        screening.setAvailableSeats(screening.getAvailableSeats() - booking.getNumberOfSeats());
        screeningRepositor.save(screening);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        // 獲取相關的放映場次
        Screening screening = booking.getScreening();

        // 增加放映場次的可用座位數
        screening.setAvailableSeats(screening.getAvailableSeats() + booking.getNumberOfSeats());
        screeningRepositor.save(screening);

        // 刪除預訂
        bookingRepository.deleteById(id);
    }

}