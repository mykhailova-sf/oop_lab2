package com.laba.labas.config;

import com.laba.labas.model.entity.User;
import com.laba.labas.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = null;

        Cookie cookie = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookieElement -> "accessToken".toLowerCase(Locale.ROOT).equals(cookieElement.getName().toLowerCase(Locale.ROOT)))
                .findFirst().orElse(null);
        jwtToken = cookie == null ? null : cookie.getValue();

        if (jwtToken == null) {
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                jwtToken = bearerToken.substring(7);
            }
        }

        if (jwtToken == null) {
            log.debug("No accessToken");
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtToken.isBlank() && jwtUtil.validateToken(jwtToken)) {
            String email = jwtUtil.getEmail(jwtToken);
            try {
                var userDto = userService.getUserByEmail(email);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority(userDto.getRole().name())
                    );

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDto.getEmail(),
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            } catch (Exception e) {
                log.debug("Token contains a non-existent user: {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            log.debug("Token value is blank or invalid");
        }

        filterChain.doFilter(request, response);
    }
}
