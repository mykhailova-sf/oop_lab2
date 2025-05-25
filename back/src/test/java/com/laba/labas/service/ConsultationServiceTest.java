package com.laba.labas.service;

import com.laba.labas.model.dto.ConsultationRequestDto;
import com.laba.labas.model.dto.ConsultationResponseDto;
import com.laba.labas.model.entity.Consultation;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.ConsultationRepository;
import com.laba.labas.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConsultationService consultationService;

    private User patient;
    private User doctor;
    private Consultation consultation;
    private ConsultationRequestDto consultationRequestDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        patient = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(User.Role.PATIENT)
                .build();

        doctor = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .role(User.Role.DOCTOR)
                .doctorSpecialty("Cardiology")
                .build();

        consultation = Consultation.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .status(Consultation.Status.pending)
                .diagnosis(null)
                .prescription(null)
                .build();

        consultationRequestDto = ConsultationRequestDto.builder()
                .patientId(1L)
                .doctorId(2L)
                .diagnosis(null)
                .prescription(null)
                .build();
    }

    @Test
    void createConsultation_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        // Act
        ConsultationResponseDto result = consultationService.createConsultation(consultationRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(consultation.getId(), result.getId());
        assertEquals(consultation.getPatient().getId(), result.getPatientDto().getId());
        assertEquals(consultation.getDoctor().getId(), result.getDoctorDto().getId());
        assertEquals(consultation.getStatus(), result.getStatus());

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void createConsultation_PatientNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            consultationService.createConsultation(consultationRequestDto);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(consultationRepository, never()).save(any(Consultation.class));
    }

    @Test
    void createConsultation_DoctorNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            consultationService.createConsultation(consultationRequestDto);
        });

        assertEquals("Doctor not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(consultationRepository, never()).save(any(Consultation.class));
    }

    @Test
    void createConsultation_UserIsNotDoctor() {
        // Arrange
        User notDoctor = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .role(User.Role.NURSE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(notDoctor));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            consultationService.createConsultation(consultationRequestDto);
        });

        assertEquals("Selected user is not a doctor", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(consultationRepository, never()).save(any(Consultation.class));
    }

    @Test
    void getConsultationById_Success() {
        // Arrange
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.of(consultation));

        // Act
        ConsultationResponseDto result = consultationService.getConsultationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(consultation.getId(), result.getId());
        assertEquals(consultation.getPatient().getId(), result.getPatientDto().getId());
        assertEquals(consultation.getDoctor().getId(), result.getDoctorDto().getId());
        assertEquals(consultation.getStatus(), result.getStatus());

        verify(consultationRepository).findById(1L);
    }

    @Test
    void getConsultationById_NotFound() {
        // Arrange
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            consultationService.getConsultationById(1L);
        });

        assertEquals("Consultation not found", exception.getMessage());
        verify(consultationRepository).findById(1L);
    }

    @Test
    void updateConsultationStatus_Success() {
        // Arrange
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.of(consultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        // Act
        ConsultationResponseDto result = consultationService.updateConsultationStatus(1L, Consultation.Status.completed);

        // Assert
        assertNotNull(result);
        assertEquals(Consultation.Status.completed, result.getStatus());

        verify(consultationRepository).findById(1L);
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void completeConsultationWithDetails_Success() {
        // Arrange
        String diagnosis = "Test diagnosis";
        String prescription = "Test prescription";
        
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.of(consultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        // Act
        ConsultationResponseDto result = consultationService.completeConsultationWithDetails(1L, diagnosis, prescription);

        // Assert
        assertNotNull(result);
        assertEquals(Consultation.Status.completed, result.getStatus());
        assertEquals(diagnosis, result.getDiagnosis());
        assertEquals(prescription, result.getPrescription());

        verify(consultationRepository).findById(1L);
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void getAllConsultations_Success() {
        // Arrange
        Consultation consultation2 = Consultation.builder()
                .id(2L)
                .patient(patient)
                .doctor(doctor)
                .status(Consultation.Status.completed)
                .diagnosis("Test diagnosis")
                .prescription("Test prescription")
                .build();

        when(consultationRepository.findAll()).thenReturn(Arrays.asList(consultation, consultation2));

        // Act
        List<ConsultationResponseDto> results = consultationService.getAllConsultations();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(consultation.getId(), results.get(0).getId());
        assertEquals(consultation2.getId(), results.get(1).getId());

        verify(consultationRepository).findAll();
    }
}