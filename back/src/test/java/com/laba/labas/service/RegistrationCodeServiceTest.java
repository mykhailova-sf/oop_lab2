package com.laba.labas.service;

import com.laba.labas.model.dto.CodeGenerationRequestDto;
import com.laba.labas.model.dto.CodeGenerationResponseDto;
import com.laba.labas.model.dto.CodeValidationRequestDto;
import com.laba.labas.model.dto.CodeValidationResponseDto;
import com.laba.labas.model.entity.RegistrationCode;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.RegistrationCodeRepository;
import com.laba.labas.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationCodeServiceTest {

    @Mock
    private RegistrationCodeRepository registrationCodeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationCodeService registrationCodeService;

    private User doctor;
    private RegistrationCode validCode;
    private RegistrationCode usedCode;
    private RegistrationCode expiredCode;
    private CodeGenerationRequestDto codeGenerationRequestDto;
    private CodeValidationRequestDto codeValidationRequestDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        doctor = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .role(User.Role.DOCTOR)
                .doctorSpecialty("Cardiology")
                .build();

        validCode = RegistrationCode.builder()
                .id(1L)
                .code("validCode123")
                .role(User.Role.DOCTOR)
                .generatedBy(doctor)
                .used(false)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        usedCode = RegistrationCode.builder()
                .id(2L)
                .code("usedCode456")
                .role(User.Role.NURSE)
                .generatedBy(doctor)
                .used(true)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        expiredCode = RegistrationCode.builder()
                .id(3L)
                .code("expiredCode789")
                .role(User.Role.DOCTOR)
                .generatedBy(doctor)
                .used(false)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        codeGenerationRequestDto = CodeGenerationRequestDto.builder()
                .value(User.Role.DOCTOR)
                .build();

        codeValidationRequestDto = CodeValidationRequestDto.builder()
                .value("validCode123")
                .build();
    }

    @Test
    void generateCode_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(registrationCodeRepository.existsByCode(anyString())).thenReturn(false);
        when(registrationCodeRepository.save(any(RegistrationCode.class))).thenReturn(validCode);

        // Act
        CodeGenerationResponseDto result = registrationCodeService.generateCode(codeGenerationRequestDto, 1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertFalse(result.getValue().isEmpty());

        verify(userRepository).findById(1L);
        verify(registrationCodeRepository).save(any(RegistrationCode.class));
    }

    @Test
    void generateCode_UserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            registrationCodeService.generateCode(codeGenerationRequestDto, 1L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    void generateCode_UserNotDoctor() {
        // Arrange
        User notDoctor = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(User.Role.PATIENT)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(notDoctor));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationCodeService.generateCode(codeGenerationRequestDto, 1L);
        });

        assertEquals("Only doctors can generate registration codes", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    void generateCode_InvalidRole() {
        // Arrange
        CodeGenerationRequestDto invalidRoleDto = CodeGenerationRequestDto.builder()
                .value(User.Role.PATIENT)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(doctor));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationCodeService.generateCode(invalidRoleDto, 1L);
        });

        assertEquals("Registration codes can only be generated for doctors or nurses", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    void validateCode_Valid() {
        // Arrange
        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(validCode));

        // Act
        CodeValidationResponseDto result = registrationCodeService.validateCode(codeValidationRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("valid", result.getValue());

        verify(registrationCodeRepository).findByCode(codeValidationRequestDto.getValue());
    }

    @Test
    void validateCode_Used() {
        // Arrange
        CodeValidationRequestDto usedCodeDto = CodeValidationRequestDto.builder()
                .value("usedCode456")
                .build();

        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(usedCode));

        // Act
        CodeValidationResponseDto result = registrationCodeService.validateCode(usedCodeDto);

        // Assert
        assertNotNull(result);
        assertEquals("invalid", result.getValue());

        verify(registrationCodeRepository).findByCode(usedCodeDto.getValue());
    }

    @Test
    void validateCode_Expired() {
        // Arrange
        CodeValidationRequestDto expiredCodeDto = CodeValidationRequestDto.builder()
                .value("expiredCode789")
                .build();

        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(expiredCode));

        // Act
        CodeValidationResponseDto result = registrationCodeService.validateCode(expiredCodeDto);

        // Assert
        assertNotNull(result);
        assertEquals("invalid", result.getValue());

        verify(registrationCodeRepository).findByCode(expiredCodeDto.getValue());
    }

    @Test
    void validateCode_NotFound() {
        // Arrange
        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act
        CodeValidationResponseDto result = registrationCodeService.validateCode(codeValidationRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("invalid", result.getValue());

        verify(registrationCodeRepository).findByCode(codeValidationRequestDto.getValue());
    }

    @Test
    void useCode_Success() {
        // Arrange
        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(validCode));
        when(registrationCodeRepository.save(any(RegistrationCode.class))).thenReturn(validCode);

        // Act
        User.Role result = registrationCodeService.useCode("validCode123");

        // Assert
        assertEquals(User.Role.DOCTOR, result);
        assertTrue(validCode.isUsed());

        verify(registrationCodeRepository).findByCode("validCode123");
        verify(registrationCodeRepository).save(validCode);
    }

    @Test
    void useCode_NotFound() {
        // Arrange
        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            registrationCodeService.useCode("invalidCode");
        });

        assertEquals("Invalid registration code", exception.getMessage());
        verify(registrationCodeRepository).findByCode("invalidCode");
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    void useCode_AlreadyUsed() {
        // Arrange
        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(usedCode));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationCodeService.useCode("usedCode456");
        });

        assertEquals("Registration code has already been used", exception.getMessage());
        verify(registrationCodeRepository).findByCode("usedCode456");
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    void useCode_Expired() {
        // Arrange
        when(registrationCodeRepository.findByCode(anyString())).thenReturn(Optional.of(expiredCode));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationCodeService.useCode("expiredCode789");
        });

        assertEquals("Registration code has expired", exception.getMessage());
        verify(registrationCodeRepository).findByCode("expiredCode789");
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }
}
