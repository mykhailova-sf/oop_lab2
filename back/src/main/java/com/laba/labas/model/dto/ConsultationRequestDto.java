package com.laba.labas.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequestDto {
    
//    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
//    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    private String status;
//
//    private String diagnosis;
//
//    private String prescription;
}