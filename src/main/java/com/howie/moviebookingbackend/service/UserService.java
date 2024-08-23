package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    List<User> getUsersByRole(User.UserRole role);
}