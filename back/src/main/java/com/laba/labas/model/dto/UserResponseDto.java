package com.laba.labas.model.dto;

import com.laba.labas.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private User.Role role;
    private String doctorSpecialty;
    private String diagnosis;
    
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .doctorSpecialty(user.getDoctorSpecialty())
                .diagnosis(user.getDiagnosis())
                .build();
    }
}