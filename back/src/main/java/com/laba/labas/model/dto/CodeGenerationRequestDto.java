package com.laba.labas.model.dto;

import com.laba.labas.model.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeGenerationRequestDto {
    
    @NotNull(message = "Role is required")
    private User.Role value;
}