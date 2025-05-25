package com.laba.labas.service;

import com.laba.labas.model.dto.UserLoginDto;
import com.laba.labas.model.dto.UserRequestDto;
import com.laba.labas.model.dto.UserResponseDto;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RegistrationCodeService registrationCodeService;

    @InjectMocks
    private UserService userService;

    private UserRequestDto userRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup test data
        userRequestDto = UserRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(User.Role.PATIENT)
                .build();

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(User.Role.PATIENT)
                .build();
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDto result = userService.registerUser(userRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());

        verify(userRepository).existsByEmail(userRequestDto.getEmail());
        verify(passwordEncoder).encode(userRequestDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userRequestDto);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(userRequestDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        UserResponseDto result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());

        verify(userRepository).findById(1L);
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        UserLoginDto loginDto = UserLoginDto.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        UserResponseDto result = userService.authenticateUser(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(passwordEncoder).matches(loginDto.getPassword(), user.getPassword());
    }

    @Test
    void authenticateUser_InvalidPassword() {
        // Arrange
        UserLoginDto loginDto = UserLoginDto.builder()
                .email("john.doe@example.com")
                .password("wrongPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser(loginDto);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(passwordEncoder).matches(loginDto.getPassword(), user.getPassword());
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        User user2 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .password("encodedPassword")
                .role(User.Role.DOCTOR)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        // Act
        List<UserResponseDto> results = userService.getAllUsers();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(user.getId(), results.get(0).getId());
        assertEquals(user2.getId(), results.get(1).getId());

        verify(userRepository).findAll();
    }
}