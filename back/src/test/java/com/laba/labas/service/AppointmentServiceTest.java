package com.laba.labas.service;

import com.laba.labas.model.dto.AppointmentRequestDto;
import com.laba.labas.model.dto.AppointmentResponseDto;
import com.laba.labas.model.entity.Appointment;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.AppointmentRepository;
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
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User patient;
    private User doctor;
    private Appointment appointment;
    private AppointmentRequestDto appointmentRequestDto;

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

        appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .appointmentType(Appointment.AppointmentType.procedure)
                .status(Appointment.Status.pending)
                .build();

        appointmentRequestDto = AppointmentRequestDto.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentType(Appointment.AppointmentType.procedure)
                .build();
    }

    @Test
    void createAppointment_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponseDto result = appointmentService.createAppointment(appointmentRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
        assertEquals(appointment.getPatient().getId(), result.getPatientDto().getId());
        assertEquals(appointment.getDoctor().getId(), result.getDoctorDto().getId());
        assertEquals(appointment.getAppointmentType(), result.getAppointmentType());
        assertEquals(appointment.getStatus(), result.getStatus());

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_PatientNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.createAppointment(appointmentRequestDto);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_DoctorNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.createAppointment(appointmentRequestDto);
        });

        assertEquals("Doctor not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_UserIsNotDoctor() {
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
            appointmentService.createAppointment(appointmentRequestDto);
        });

        assertEquals("Selected user is not a doctor", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void getAppointmentById_Success() {
        // Arrange
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));

        // Act
        AppointmentResponseDto result = appointmentService.getAppointmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
        assertEquals(appointment.getPatient().getId(), result.getPatientDto().getId());
        assertEquals(appointment.getDoctor().getId(), result.getDoctorDto().getId());
        assertEquals(appointment.getAppointmentType(), result.getAppointmentType());
        assertEquals(appointment.getStatus(), result.getStatus());

        verify(appointmentRepository).findById(1L);
    }

    @Test
    void getAppointmentById_NotFound() {
        // Arrange
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.getAppointmentById(1L);
        });

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository).findById(1L);
    }

    @Test
    void updateAppointmentStatus_Success() {
        // Arrange
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponseDto result = appointmentService.updateAppointmentStatus(1L, Appointment.Status.completed);

        // Assert
        assertNotNull(result);
        assertEquals(Appointment.Status.completed, result.getStatus());

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void getAllAppointments_Success() {
        // Arrange
        Appointment appointment2 = Appointment.builder()
                .id(2L)
                .patient(patient)
                .doctor(doctor)
                .appointmentType(Appointment.AppointmentType.surgery)
                .status(Appointment.Status.pending)
                .build();

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment, appointment2));

        // Act
        List<AppointmentResponseDto> results = appointmentService.getAllAppointments();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(appointment.getId(), results.get(0).getId());
        assertEquals(appointment2.getId(), results.get(1).getId());

        verify(appointmentRepository).findAll();
    }
}
