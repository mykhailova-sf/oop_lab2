package com.laba.products.security.config;

import com.laba.products.security.entity.Role;
import com.laba.products.security.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Value("${spring.security.domain}")
    private String domain;

    @SneakyThrows
    @Bean
    public SecurityFilterChain getFilterChain(HttpSecurity http) {
//        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
//        csrfTokenRepository.setCookieCustomizer(cookie -> cookie.secure(true).sameSite("None"));
//
//        http.csrf(csrf->csrf.
//                csrfTokenRepository(csrfTokenRepository).
//                csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));

        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(e ->
                e.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));

        http.formLogin(AbstractHttpConfigurer::disable);

        http.logout(e -> e.logoutUrl("/logout").
                deleteCookies("accessToken", "JSESSIONID").
                logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.toString())).
                logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Logout successful");
                }));

        http.userDetailsService(userService);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(request -> request.
                requestMatchers("/api/auth/login", "/api/auth/register", "/css/**", "/js/**", "/images/**", "/csrf", "/chat/**", "/ws/info", "/stream-flux", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**").permitAll().
                requestMatchers("/api/products/**").permitAll().
                requestMatchers("/api/orders/**", "/api/auth/profile").authenticated().
                requestMatchers("/api/users/**").hasAuthority(Role.ADMIN.getAuthority()).
                anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                    throw new IllegalArgumentException("Invalid URL format: " + domain);
                }
                String pattern = domain.replaceFirst("://", "://*.");

                registry.addMapping("/**").
                        allowedOrigins(domain, "https://localhost", "http://localhost").
                        allowedOriginPatterns(pattern).
                        allowedOriginPatterns("*").
                        exposedHeaders("*", "Set-Cookie","X-XSRF-TOKEN").
                        allowedHeaders("*").
                        allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE","OPTIONS").
                        allowCredentials(true);
            }
        };
    }
}
