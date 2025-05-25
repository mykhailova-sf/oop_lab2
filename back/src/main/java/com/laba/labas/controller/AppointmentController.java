package com.laba.labas.controller;

import com.laba.labas.model.dto.AppointmentRequestDto;
import com.laba.labas.model.dto.AppointmentResponseDto;
import com.laba.labas.model.entity.Appointment;
import com.laba.labas.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentRequestDto appointmentRequestDto) {
        log.info("Creating appointment for patient ID: {} and doctor ID: {}, type: {}", 
                appointmentRequestDto.getPatientId(), appointmentRequestDto.getDoctorId(), 
                appointmentRequestDto.getAppointmentType());
        AppointmentResponseDto appointmentResponseDto = appointmentService.createAppointment(appointmentRequestDto);
        log.info("Appointment created successfully with ID: {}", appointmentResponseDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAppointments(
            @RequestParam(required = false) String type) {
        log.info("Getting appointments with type: {}", type);

        if (type != null) {
            try {
                Appointment.AppointmentType appointmentType = Appointment.AppointmentType.valueOf(type.toLowerCase());
                List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByType(appointmentType);
                log.info("Retrieved {} appointments with type: {}", appointments.size(), type);
                return ResponseEntity.ok(appointments);
            } catch (IllegalArgumentException e) {
                log.error("Invalid appointment type: {}", type);
                return ResponseEntity.badRequest().build();
            }
        }

        List<AppointmentResponseDto> appointments = appointmentService.getAllAppointments();
        log.info("Retrieved {} appointments", appointments.size());
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable Long id) {
        log.info("Getting appointment by ID: {}", id);
        AppointmentResponseDto appointmentResponseDto = appointmentService.getAppointmentById(id);
        log.info("Appointment retrieved successfully with ID: {}", appointmentResponseDto.getId());
        return ResponseEntity.ok(appointmentResponseDto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDto> updateAppointmentStatus(
            @PathVariable Long id, 
            @RequestParam Appointment.Status status) {
        log.info("Updating appointment status to {} for ID: {}", status, id);
        AppointmentResponseDto appointmentResponseDto = appointmentService.updateAppointmentStatus(id, status);
        log.info("Appointment status updated successfully");
        return ResponseEntity.ok(appointmentResponseDto);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(@PathVariable Long id) {
        log.info("Completing appointment with ID: {}", id);
        AppointmentResponseDto appointmentResponseDto = appointmentService.updateAppointmentStatus(id, Appointment.Status.completed);
        log.info("Appointment completed successfully");
        return ResponseEntity.ok(appointmentResponseDto);
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<AppointmentResponseDto> declineAppointment(@PathVariable Long id) {
        log.info("Declining appointment with ID: {}", id);
        AppointmentResponseDto appointmentResponseDto = appointmentService.updateAppointmentStatus(id, Appointment.Status.declined);
        log.info("Appointment declined successfully");
        return ResponseEntity.ok(appointmentResponseDto);
    }
}
