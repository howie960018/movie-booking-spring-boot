package com.howie.moviebookingbackend.config;

import com.howie.moviebookingbackend.entity.User;
import com.howie.moviebookingbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 如果已經有 ADMIN 角色就略過
        if (userRepository.countByRole(User.UserRole.ADMIN) > 0) {
            log.info("Admin seed skipped (admin already exists)");
            return;
        }

        // 建立預設 Admin 帳號
        String email = "admin@example.com";
        String rawPassword = "Admin@12345"; // 建議啟動後立即修改

        if (userRepository.existsByEmail(email)) {
            // 若這個 email 已存在，直接提升為 ADMIN
            userRepository.findByEmail(email).ifPresent(u -> {
                u.setRole(User.UserRole.ADMIN);
                // 若不確定密碼是否可用，也可選擇重設密碼
                u.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(u);
                log.info("User {} promoted to ADMIN.", email);
            });
            return;
        }

        User admin = new User();
        admin.setEmail(email);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPhoneNumber("0912345678");
        admin.setRole(User.UserRole.ADMIN);
        admin.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(admin);
        log.info("Default admin created: {} / {}", email, rawPassword);
    }
}

