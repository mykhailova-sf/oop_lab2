package com.laba.labas.service;

import com.laba.labas.model.dto.CodeGenerationRequestDto;
import com.laba.labas.model.dto.CodeGenerationResponseDto;
import com.laba.labas.model.dto.CodeValidationRequestDto;
import com.laba.labas.model.dto.CodeValidationResponseDto;
import com.laba.labas.model.entity.RegistrationCode;
import com.laba.labas.model.entity.User;
import com.laba.labas.repository.RegistrationCodeRepository;
import com.laba.labas.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationCodeService {

    private final RegistrationCodeRepository registrationCodeRepository;
    private final UserRepository userRepository;
    
    private static final int CODE_LENGTH = 10;
    private static final int CODE_EXPIRY_DAYS = 7;

    @Transactional
    public CodeGenerationResponseDto generateCode(CodeGenerationRequestDto requestDto, Long generatorId) {
        log.info("Generating registration code for role: {}", requestDto.getValue());
        
        User generator = userRepository.findById(generatorId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", generatorId);
                    return new EntityNotFoundException("User not found");
                });
        
        // Only doctors can generate codes
        if (generator.getRole() != User.Role.DOCTOR) {
            log.error("User with ID {} is not a doctor", generatorId);
            throw new IllegalArgumentException("Only doctors can generate registration codes");
        }
        
        // Only generate codes for doctors or nurses
        if (requestDto.getValue() != User.Role.DOCTOR && requestDto.getValue() != User.Role.NURSE) {
            log.error("Invalid role for code generation: {}", requestDto.getValue());
            throw new IllegalArgumentException("Registration codes can only be generated for doctors or nurses");
        }
        
        String code = generateUniqueCode();
        
        RegistrationCode registrationCode = RegistrationCode.builder()
                .code(code)
                .role(requestDto.getValue())
                .generatedBy(generator)
                .used(false)
                .expiresAt(LocalDateTime.now().plusDays(CODE_EXPIRY_DAYS))
                .build();
        
        registrationCodeRepository.save(registrationCode);
        log.info("Registration code generated successfully");
        
        return CodeGenerationResponseDto.builder()
                .value(code)
                .build();
    }
    
    @Transactional(readOnly = true)
    public CodeValidationResponseDto validateCode(CodeValidationRequestDto requestDto) {
        log.info("Validating registration code");
        
        Optional<RegistrationCode> codeOptional = registrationCodeRepository.findByCode(requestDto.getValue());
        
        if (codeOptional.isEmpty()) {
            log.info("Registration code not found");
            return CodeValidationResponseDto.builder()
                    .value("invalid")
                    .build();
        }
        
        RegistrationCode code = codeOptional.get();
        
        if (code.isUsed()) {
            log.info("Registration code has already been used");
            return CodeValidationResponseDto.builder()
                    .value("invalid")
                    .build();
        }
        
        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.info("Registration code has expired");
            return CodeValidationResponseDto.builder()
                    .value("invalid")
                    .build();
        }
        
        log.info("Registration code is valid");
        return CodeValidationResponseDto.builder()
                .value("valid")
                .build();
    }
    
    @Transactional
    public User.Role useCode(String code) {
        log.info("Using registration code");
        
        RegistrationCode registrationCode = registrationCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Registration code not found");
                    return new EntityNotFoundException("Invalid registration code");
                });
        
        if (registrationCode.isUsed()) {
            log.error("Registration code has already been used");
            throw new IllegalArgumentException("Registration code has already been used");
        }
        
        if (registrationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Registration code has expired");
            throw new IllegalArgumentException("Registration code has expired");
        }
        
        registrationCode.setUsed(true);
        registrationCodeRepository.save(registrationCode);
        
        log.info("Registration code used successfully");
        return registrationCode.getRole();
    }
    
    private String generateUniqueCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[CODE_LENGTH];
        
        String code;
        do {
            random.nextBytes(bytes);
            code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, CODE_LENGTH);
        } while (registrationCodeRepository.existsByCode(code));
        
        return code;
    }
}