package com.laba.labas.controller;

import com.laba.labas.model.dto.ConsultationRequestDto;
import com.laba.labas.model.dto.ConsultationResponseDto;
import com.laba.labas.model.entity.Consultation;
import com.laba.labas.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultations")
@RequiredArgsConstructor
@Slf4j
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<ConsultationResponseDto> createConsultation(@Valid @RequestBody ConsultationRequestDto consultationRequestDto) {
        log.info("Creating consultation for patient ID: {} and doctor ID: {}", 
                consultationRequestDto.getPatientId(), consultationRequestDto.getDoctorId());
        ConsultationResponseDto consultationResponseDto = consultationService.createConsultation(consultationRequestDto);
        log.info("Consultation created successfully with ID: {}", consultationResponseDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<ConsultationResponseDto>> getAllConsultations() {
        log.info("Getting all consultations");
        List<ConsultationResponseDto> consultations = consultationService.getAllConsultations();
        log.info("Retrieved {} consultations", consultations.size());
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationResponseDto> getConsultationById(@PathVariable Long id) {
        log.info("Getting consultation by ID: {}", id);
        ConsultationResponseDto consultationResponseDto = consultationService.getConsultationById(id);
        log.info("Consultation retrieved successfully with ID: {}", consultationResponseDto.getId());
        return ResponseEntity.ok(consultationResponseDto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ConsultationResponseDto> updateConsultationStatus(
            @PathVariable Long id, 
            @RequestParam Consultation.Status status) {
        log.info("Updating consultation status to {} for ID: {}", status, id);
        ConsultationResponseDto consultationResponseDto = consultationService.updateConsultationStatus(id, status);
        log.info("Consultation status updated successfully");
        return ResponseEntity.ok(consultationResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationResponseDto> updateConsultation(
            @PathVariable Long id, 
            @Valid @RequestBody ConsultationRequestDto consultationRequestDto) {
        log.info("Updating consultation with ID: {}", id);
        ConsultationResponseDto consultationResponseDto = consultationService.updateConsultation(id, consultationRequestDto);
        log.info("Consultation updated successfully");
        return ResponseEntity.ok(consultationResponseDto);
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<ConsultationResponseDto> declineConsultation(@PathVariable Long id) {
        log.info("Declining consultation with ID: {}", id);
        ConsultationResponseDto consultationResponseDto = consultationService.updateConsultationStatus(id, Consultation.Status.declined);
        log.info("Consultation declined successfully");
        return ResponseEntity.ok(consultationResponseDto);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ConsultationResponseDto> completeConsultation(
            @PathVariable Long id,
            @RequestBody(required = false) ConsultationRequestDto consultationRequestDto) {
        String diagnosis = null;
        String prescription = null;

        if (consultationRequestDto != null) {
            diagnosis = consultationRequestDto.getDiagnosis();
            prescription = consultationRequestDto.getPrescription();
        }

        log.info("Completing consultation with ID: {} and diagnosis: {}, prescription: {}", id, diagnosis, prescription);

        // Always use the diagnosis and prescription from the request body
        ConsultationResponseDto consultationResponseDto = consultationService.completeConsultationWithDetails(id, diagnosis, prescription);

        log.info("Consultation completed successfully");
        return ResponseEntity.ok(consultationResponseDto);
    }
}
