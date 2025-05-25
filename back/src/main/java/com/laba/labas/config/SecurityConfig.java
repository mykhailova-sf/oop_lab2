package com.laba.labas.config;

import com.laba.labas.model.entity.User;
import com.laba.labas.service.UserService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Value("${spring.security.domain:http://localhost:8080}")
    private String domain;

    @SneakyThrows
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(e ->
                e.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));

        http.formLogin(AbstractHttpConfigurer::disable);

        http.logout(e -> e.logoutUrl("/logout").
                deleteCookies("accessToken", "JSESSIONID").
//                logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.toString())).
                logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Logout successful");
                }));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(request -> request.
                requestMatchers( "/registration", "/login", "/validate-code").permitAll().
                requestMatchers("/generate-code").hasAuthority(User.Role.DOCTOR.name()).
//                requestMatchers("/users/**").hasAnyAuthority(User.Role.DOCTOR.name(), User.Role.ADMIN.name()).
                requestMatchers("/users/**").permitAll().
                requestMatchers("/consultations/**").authenticated().
                requestMatchers("/appointments/**").authenticated().
                requestMatchers("/current-user/**").authenticated().
                anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOrigins(domain, "https://localhost", "http://localhost","http://localhost:5173").
                        allowedHeaders("*").
                        allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").
                        allowCredentials(true);
            }
        };
    }
}
