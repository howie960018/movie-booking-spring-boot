package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
}