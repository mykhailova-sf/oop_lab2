package com.laba.labas.model.dto;

import com.laba.labas.model.entity.Consultation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponseDto {
    
    private Long id;
    private UserResponseDto patientDto;
    private UserResponseDto doctorDto;
    private Consultation.Status status;
    private String diagnosis;
    private String prescription;
    
    public static ConsultationResponseDto fromEntity(Consultation consultation) {
        return ConsultationResponseDto.builder()
                .id(consultation.getId())
                .patientDto(UserResponseDto.fromEntity(consultation.getPatient()))
                .doctorDto(UserResponseDto.fromEntity(consultation.getDoctor()))
                .status(consultation.getStatus())
                .diagnosis(consultation.getDiagnosis())
                .prescription(consultation.getPrescription())
                .build();
    }
}