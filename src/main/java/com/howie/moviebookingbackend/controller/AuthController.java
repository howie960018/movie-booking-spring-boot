package com.howie.moviebookingbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.howie.moviebookingbackend.entity.User;
import com.howie.moviebookingbackend.service.UserService;
import com.howie.moviebookingbackend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            logger.info("User logged in successfully: " + loginRequest.getEmail());

            // Retrieve the user from the database
            User user = userService.getUserByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT Token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            // Return the JWT Token in the response
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (AuthenticationException e) {
            logger.warn("Login failed for user: " + loginRequest.getEmail());
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // 通知客戶端刪除 Token 或將 Token 放入黑名單
        logger.info("User logged out successfully");
        return ResponseEntity.ok("User logged out successfully");
    }

    // Class to receive the login request
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Class to return the JWT Token in the response
    public static class JwtResponse {
        private String token;

        public JwtResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
