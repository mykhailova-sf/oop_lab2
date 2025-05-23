package com.laba.labas.controller;

import com.laba.labas.model.dto.CodeGenerationRequestDto;
import com.laba.labas.model.dto.CodeGenerationResponseDto;
import com.laba.labas.model.dto.CodeValidationRequestDto;
import com.laba.labas.model.dto.CodeValidationResponseDto;
import com.laba.labas.service.RegistrationCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegistrationCodeController {

    private final RegistrationCodeService registrationCodeService;

    @PostMapping("/generate-code")
    public ResponseEntity<CodeGenerationResponseDto> generateCode(
            @Valid @RequestBody CodeGenerationRequestDto requestDto) {
        log.info("Received request to generate registration code for role: {}", requestDto.getValue());
        
        // In a real application, this would get the current user's ID from the security context
        // For now, we'll just use a mock user with ID 1
        Long currentUserId = 1L;
        
        CodeGenerationResponseDto responseDto = registrationCodeService.generateCode(requestDto, currentUserId);
        log.info("Registration code generated successfully");
        
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/validate-code")
    public ResponseEntity<CodeValidationResponseDto> validateCode(
            @Valid @RequestBody CodeValidationRequestDto requestDto) {
        log.info("Received request to validate registration code");
        
        CodeValidationResponseDto responseDto = registrationCodeService.validateCode(requestDto);
        log.info("Registration code validation completed with result: {}", responseDto.getValue());
        
        return ResponseEntity.ok(responseDto);
    }
}