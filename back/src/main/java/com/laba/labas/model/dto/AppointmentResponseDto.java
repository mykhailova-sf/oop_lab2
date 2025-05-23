package com.laba.labas.model.dto;

import com.laba.labas.model.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    
    private Long id;
    private UserResponseDto patientDto;
    private UserResponseDto doctorDto;
    private Appointment.AppointmentType appointmentType;
    private Appointment.Status status;
    
    public static AppointmentResponseDto fromEntity(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .patientDto(UserResponseDto.fromEntity(appointment.getPatient()))
                .doctorDto(UserResponseDto.fromEntity(appointment.getDoctor()))
                .appointmentType(appointment.getAppointmentType())
                .status(appointment.getStatus())
                .build();
    }
}