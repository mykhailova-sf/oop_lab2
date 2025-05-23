package com.laba.labas.service;

import com.laba.labas.model.dto.AppointmentRequestDto;
import com.laba.labas.model.dto.AppointmentResponseDto;
import com.laba.labas.model.entity.Appointment;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.AppointmentRepository;
import com.laba.labas.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDto) {
        log.info("Creating appointment for patient ID: {} and doctor ID: {}, type: {}", 
                appointmentRequestDto.getPatientId(), appointmentRequestDto.getDoctorId(), 
                appointmentRequestDto.getAppointmentType());
        
        User patient = userRepository.findById(appointmentRequestDto.getPatientId())
                .orElseThrow(() -> {
                    log.error("Patient with ID {} not found", appointmentRequestDto.getPatientId());
                    return new EntityNotFoundException("Patient not found");
                });
        
        User doctor = userRepository.findById(appointmentRequestDto.getDoctorId())
                .orElseThrow(() -> {
                    log.error("Doctor with ID {} not found", appointmentRequestDto.getDoctorId());
                    return new EntityNotFoundException("Doctor not found");
                });
        
        if (doctor.getRole() != User.Role.DOCTOR) {
            log.error("User with ID {} is not a doctor", appointmentRequestDto.getDoctorId());
            throw new IllegalArgumentException("Selected user is not a doctor");
        }
        
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentType(appointmentRequestDto.getAppointmentType())
                .status(Appointment.Status.PENDING)
                .build();
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        
        return AppointmentResponseDto.fromEntity(savedAppointment);
    }
    
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        log.info("Getting appointment by ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Appointment with ID {} not found", id);
                    return new EntityNotFoundException("Appointment not found");
                });
        
        return AppointmentResponseDto.fromEntity(appointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointments() {
        log.info("Getting all appointments");
        
        return appointmentRepository.findAll().stream()
                .map(AppointmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByType(Appointment.AppointmentType appointmentType) {
        log.info("Getting appointments by type: {}", appointmentType);
        
        return appointmentRepository.findByAppointmentType(appointmentType).stream()
                .map(AppointmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByPatient(Long patientId) {
        log.info("Getting appointments for patient ID: {}", patientId);
        
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient with ID {} not found", patientId);
                    return new EntityNotFoundException("Patient not found");
                });
        
        return appointmentRepository.findByPatient(patient).stream()
                .map(AppointmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByPatientAndType(Long patientId, Appointment.AppointmentType appointmentType) {
        log.info("Getting appointments for patient ID: {} and type: {}", patientId, appointmentType);
        
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient with ID {} not found", patientId);
                    return new EntityNotFoundException("Patient not found");
                });
        
        return appointmentRepository.findByPatientAndAppointmentType(patient, appointmentType).stream()
                .map(AppointmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByDoctor(Long doctorId) {
        log.info("Getting appointments for doctor ID: {}", doctorId);
        
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor with ID {} not found", doctorId);
                    return new EntityNotFoundException("Doctor not found");
                });
        
        return appointmentRepository.findByDoctor(doctor).stream()
                .map(AppointmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AppointmentResponseDto updateAppointmentStatus(Long id, Appointment.Status status) {
        log.info("Updating appointment status to {} for ID: {}", status, id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Appointment with ID {} not found", id);
                    return new EntityNotFoundException("Appointment not found");
                });
        
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment status updated successfully");
        
        return AppointmentResponseDto.fromEntity(updatedAppointment);
    }
}