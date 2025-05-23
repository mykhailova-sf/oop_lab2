package com.laba.labas.controller;

import com.laba.labas.model.dto.AppointmentResponseDto;
import com.laba.labas.model.dto.ConsultationResponseDto;
import com.laba.labas.model.dto.UserResponseDto;
import com.laba.labas.model.entity.Appointment;
import com.laba.labas.model.entity.User;
import com.laba.labas.service.AppointmentService;
import com.laba.labas.service.ConsultationService;
import com.laba.labas.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ConsultationService consultationService;
    private final AppointmentService appointmentService;

    @GetMapping("/current-user")
    public ResponseEntity<UserResponseDto> getCurrentUser(Principal principal) {
        log.info("Getting current user");
        // In a real application, this would get the current user's ID from the security context
        // For now, we'll just return a mock user with ID 1
        UserResponseDto userResponseDto = userService.getUserByEmail(principal.getName());
        log.info("Current user retrieved successfully with ID: {}", userResponseDto.getId());
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/current-user/consultations")
    public ResponseEntity<List<ConsultationResponseDto>> getCurrentUserConsultations(Principal principal) {
        log.info("Getting consultations for current user");
        // In a real application, this would get the current user's ID from the security context
        // For now, we'll just use a mock user with ID 1
        List<ConsultationResponseDto> consultations = consultationService.getConsultationsByPatient(principal.getName());
        log.info("Retrieved {} consultations for current user", consultations.size());
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/current-user/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getCurrentUserAppointments(
            @RequestParam(required = false) String type) {
        log.info("Getting appointments for current user with type: {}", type);
        // In a real application, this would get the current user's ID from the security context
        // For now, we'll just use a mock user with ID 1

        if (type != null) {
            try {
                // Handle the "syrgery" typo from the API spec
                if ("syrgery".equalsIgnoreCase(type)) {
                    type = "surgery";
                }

                Appointment.AppointmentType appointmentType = Appointment.AppointmentType.valueOf(type.toUpperCase());
                List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByPatientAndType(1L, appointmentType);
                log.info("Retrieved {} appointments for current user with type: {}", appointments.size(), type);
                return ResponseEntity.ok(appointments);
            } catch (IllegalArgumentException e) {
                log.error("Invalid appointment type: {}", type);
                return ResponseEntity.badRequest().build();
            }
        }

        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByPatient(1L);
        log.info("Retrieved {} appointments for current user", appointments.size());
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(required = false) String role) {
        log.info("Getting users with role: {}", role);

        if (role != null) {
            try {
                User.Role userRole = User.Role.valueOf(role.toUpperCase());
                List<UserResponseDto> users = userService.getUsersByRole(userRole);
                log.info("Retrieved {} users with role: {}", users.size(), role);
                return ResponseEntity.ok(users);
            } catch (IllegalArgumentException e) {
                log.error("Invalid user role: {}", role);
                return ResponseEntity.badRequest().build();
            }
        }

        List<UserResponseDto> users = userService.getAllUsers();
        log.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        UserResponseDto userResponseDto = userService.getUserById(id);
        log.info("User retrieved successfully with ID: {}", userResponseDto.getId());
        return ResponseEntity.ok(userResponseDto);
    }
}
