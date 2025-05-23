package com.laba.labas.service;

import com.laba.labas.model.dto.ConsultationRequestDto;
import com.laba.labas.model.dto.ConsultationResponseDto;
import com.laba.labas.model.entity.Consultation;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.ConsultationRepository;
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
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;

    @Transactional
    public ConsultationResponseDto createConsultation(ConsultationRequestDto consultationRequestDto) {
        log.info("Creating consultation for patient ID: {} and doctor ID: {}", 
                consultationRequestDto.getPatientId(), consultationRequestDto.getDoctorId());
        
        User patient = userRepository.findById(consultationRequestDto.getPatientId())
                .orElseThrow(() -> {
                    log.error("Patient with ID {} not found", consultationRequestDto.getPatientId());
                    return new EntityNotFoundException("Patient not found");
                });
        
        User doctor = userRepository.findById(consultationRequestDto.getDoctorId())
                .orElseThrow(() -> {
                    log.error("Doctor with ID {} not found", consultationRequestDto.getDoctorId());
                    return new EntityNotFoundException("Doctor not found");
                });
        
        if (doctor.getRole() != User.Role.DOCTOR) {
            log.error("User with ID {} is not a doctor", consultationRequestDto.getDoctorId());
            throw new IllegalArgumentException("Selected user is not a doctor");
        }
        
        Consultation consultation = Consultation.builder()
                .patient(patient)
                .doctor(doctor)
                .status(Consultation.Status.PENDING)
//                .diagnosis(consultationRequestDto.getDiagnosis())
//                .prescription(consultationRequestDto.getPrescription())
                .build();
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Consultation created successfully with ID: {}", savedConsultation.getId());
        
        return ConsultationResponseDto.fromEntity(savedConsultation);
    }
    
    @Transactional(readOnly = true)
    public ConsultationResponseDto getConsultationById(Long id) {
        log.info("Getting consultation by ID: {}", id);
        
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Consultation with ID {} not found", id);
                    return new EntityNotFoundException("Consultation not found");
                });
        
        return ConsultationResponseDto.fromEntity(consultation);
    }
    
    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> getAllConsultations() {
        log.info("Getting all consultations");
        
        return consultationRepository.findAll().stream()
                .map(ConsultationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> getConsultationsByPatient(String patientId) {
        log.info("Getting consultations for patient ID: {}", patientId);
        
        User patient = userRepository.findByEmail(patientId)
                .orElseThrow(() -> {
                    log.error("Patient with ID {} not found", patientId);
                    return new EntityNotFoundException("Patient not found");
                });
        
        return consultationRepository.findByPatient(patient).stream()
                .map(ConsultationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> getConsultationsByDoctor(Long doctorId) {
        log.info("Getting consultations for doctor ID: {}", doctorId);
        
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor with ID {} not found", doctorId);
                    return new EntityNotFoundException("Doctor not found");
                });
        
        return consultationRepository.findByDoctor(doctor).stream()
                .map(ConsultationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ConsultationResponseDto updateConsultationStatus(Long id, Consultation.Status status) {
        log.info("Updating consultation status to {} for ID: {}", status, id);
        
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Consultation with ID {} not found", id);
                    return new EntityNotFoundException("Consultation not found");
                });
        
        consultation.setStatus(status);
        Consultation updatedConsultation = consultationRepository.save(consultation);
        log.info("Consultation status updated successfully");
        
        return ConsultationResponseDto.fromEntity(updatedConsultation);
    }
    
    @Transactional
    public ConsultationResponseDto updateConsultation(Long id, ConsultationRequestDto consultationRequestDto) {
        log.info("Updating consultation with ID: {}", id);
        
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Consultation with ID {} not found", id);
                    return new EntityNotFoundException("Consultation not found");
                });
        
//        if (consultationRequestDto.getDiagnosis() != null) {
//            consultation.setDiagnosis(consultationRequestDto.getDiagnosis());
//        }
//
//        if (consultationRequestDto.getPrescription() != null) {
//            consultation.setPrescription(consultationRequestDto.getPrescription());
//        }
        
        Consultation updatedConsultation = consultationRepository.save(consultation);
        log.info("Consultation updated successfully");
        
        return ConsultationResponseDto.fromEntity(updatedConsultation);
    }
}