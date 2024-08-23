package com.howie.moviebookingbackend;

import com.howie.moviebookingbackend.entity.User;
import com.howie.moviebookingbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testSaveAndGetUser() {
        User user = new User();
        user.setEmail("test00@example.com");
        user.setPassword("password");
        user.setFirstName("Test00");
        user.setLastName("User00");

        User savedUser = userService.saveUser(user);
        assertNotNull(savedUser.getId());

        Optional<User> retrievedUser = userService.getUserByEmail("test00@example.com");
        assertTrue(retrievedUser.isPresent());
        assertEquals("Test00", retrievedUser.get().getFirstName());
    }

    // 添加更多整合測試...
}