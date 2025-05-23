package com.laba.labas.service;

import com.laba.labas.model.dto.UserLoginDto;
import com.laba.labas.model.dto.UserRequestDto;
import com.laba.labas.model.dto.UserResponseDto;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationCodeService registrationCodeService;

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        log.info("Registering new user with email: {}", userRequestDto.getEmail());

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            log.error("User with email {} already exists", userRequestDto.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Validate registration code for doctors and nurses
        if (userRequestDto.getRole() == User.Role.DOCTOR || userRequestDto.getRole() == User.Role.NURSE) {
            log.info("Validating registration code for role: {}", userRequestDto.getRole());

            if (userRequestDto.getSpecialCode() == null || userRequestDto.getSpecialCode().isBlank()) {
                log.error("Registration code is required for role: {}", userRequestDto.getRole());
                throw new IllegalArgumentException("Registration code is required for doctors and nurses");
            }

            // Use the code and get the role it was generated for
            try {
                User.Role codeRole = registrationCodeService.useCode(userRequestDto.getSpecialCode());

                // Check if the code was generated for the correct role
                if (codeRole != userRequestDto.getRole()) {
                    log.error("Registration code is for role {} but user is registering as {}", 
                            codeRole, userRequestDto.getRole());
                    throw new IllegalArgumentException("Invalid registration code for the specified role");
                }

                log.info("Registration code validated successfully");
            } catch (Exception e) {
                log.error("Invalid registration code: {}", e.getMessage());
                throw new IllegalArgumentException("Invalid registration code: " + e.getMessage());
            }
        }

        User user = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .email(userRequestDto.getEmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .role(userRequestDto.getRole())
                .doctorSpecialty(userRequestDto.getDoctorSpecialty())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return UserResponseDto.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("Getting user by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", id);
                    return new EntityNotFoundException("User not found");
                });

        return UserResponseDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} not found", email);
                    return new EntityNotFoundException("User not found");
                });

        return UserResponseDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.info("Getting all users");

        return userRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByRole(User.Role role) {
        log.info("Getting users by role: {}", role);

        return userRepository.findByRole(role).stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.error("User with id {} not found", id);
            throw new EntityNotFoundException("User not found");
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully");
    }

    @Transactional(readOnly = true)
    public UserResponseDto authenticateUser(UserLoginDto userLoginDto) {
        log.info("Authenticating user with email: {}", userLoginDto.getEmail());

        User user = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> {
                    log.error("User with email {} not found", userLoginDto.getEmail());
                    return new EntityNotFoundException("Invalid email or password");
                });

        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            log.error("Invalid password for user with email: {}", userLoginDto.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("User authenticated successfully");
        return UserResponseDto.fromEntity(user);
    }
}
