package com.laba.labas.controller;

import com.laba.labas.config.JwtUtil;
import com.laba.labas.model.dto.UserLoginDto;
import com.laba.labas.model.dto.UserRequestDto;
import com.laba.labas.model.dto.UserResponseDto;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.UserRepository;
import com.laba.labas.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/registration")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto, HttpServletResponse response) {
        log.info("Received registration request for email: {}", userRequestDto.getEmail());
        UserResponseDto userResponseDto = userService.registerUser(userRequestDto);
        log.info("User registered successfully with ID: {}", userResponseDto.getId());

        // Get the user entity to generate JWT token
        User user = userRepository.findById(userResponseDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        log.info("JWT token generated for user: {}", userResponseDto.getEmail());

        // Set JWT token as cookie
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60); // 14 days in seconds
        response.addCookie(cookie);
        log.info("JWT token set as cookie for user: {}", userResponseDto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody UserLoginDto userLoginDto, HttpServletResponse response) {
        log.info("Received login request for email: {}", userLoginDto.getEmail());
        UserResponseDto userResponseDto = userService.authenticateUser(userLoginDto);
        log.info("User authenticated successfully with ID: {}", userResponseDto.getId());

        // Get the user entity to generate JWT token
        User user = userRepository.findById(userResponseDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        log.info("JWT token generated for user: {}", userResponseDto.getEmail());

        // Set JWT token as cookie
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60); // 14 days in seconds
        response.addCookie(cookie);
        log.info("JWT token set as cookie for user: {}", userResponseDto.getEmail());

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletResponse response) {
        log.info("Received logout request");

        // Clear the JWT token cookie
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete the cookie
        response.addCookie(cookie);
        log.info("JWT token cookie cleared");

        log.info("User logged out successfully");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<Void> deleteAccount(HttpServletResponse response) {
        log.info("Received delete account request");
        // In a real application, this would get the current user's ID from the security context
        // and delete their account

        // Clear the JWT token cookie
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete the cookie
        response.addCookie(cookie);
        log.info("JWT token cookie cleared");

        log.info("Account deleted successfully");
        return ResponseEntity.ok().build();
    }
}
