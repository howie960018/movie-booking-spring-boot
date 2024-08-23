package com.howie.moviebookingbackend.repository;

import com.howie.moviebookingbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // 新增的方法
    List<User> findByRole(User.UserRole role);
    long countByRole(User.UserRole role);
    Optional<User> findByEmailAndRole(String email, User.UserRole role);
}