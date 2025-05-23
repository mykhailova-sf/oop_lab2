package com.laba.products.security.service;

import com.laba.products.app.entity.User;
import com.laba.products.security.config.JwtUtil;
import com.laba.products.security.dto.UserLoginDto;
import com.laba.products.security.dto.UserRegistrationDto;
import com.laba.products.security.entity.Role;
import com.laba.products.security.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.domain}")
    private String domain;

    public ResponseEntity<String> login(UserLoginDto userLoginDto) {
        String login = userLoginDto.getLogin();
        User user = userService.findByUsername(login);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), userLoginDto.getPassword()));
            setCookie(user);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
        return ResponseEntity.ok("Successfully logged in");
    }

    public ResponseEntity<String> registration(UserRegistrationDto userRegistrationDto) {
        User user;
        if (userRegistrationDto.getUsername().equals("admin")){
            User user1 = userMapper.userRegistrationDtoToUser(userRegistrationDto);
            user1.setRole(Role.ADMIN);
            user = userService.save(user1);
        }else {
             user = userService.save(userMapper.userRegistrationDtoToUser(userRegistrationDto));
        }
        setCookie(user);
        return ResponseEntity.ok("User registered successfully");
    }

    private void setCookie(User user) {
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            throw new IllegalArgumentException("Invalid URL format: " + domain);
        }
        String pattern = domain.replaceFirst("^(https?://)", ".");
        System.out.println(pattern);
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes()))
                .getResponse();
        ResponseCookie responseCookie = ResponseCookie.from("accessToken", jwtUtil.generateToken(user)).
                httpOnly(true).
                maxAge(24_192_000).
                sameSite("None").
                domain("localhost").
                path("/").
                secure(true).
                build();
        Objects.requireNonNull(response).addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
}