package com.laba.labas.repository;

import com.laba.labas.model.entity.RegistrationCode;
import com.laba.labas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationCodeRepository extends JpaRepository<RegistrationCode, Long> {
    
    Optional<RegistrationCode> findByCode(String code);
    
    List<RegistrationCode> findByGeneratedBy(User generatedBy);
    
    List<RegistrationCode> findByRole(User.Role role);
    
    List<RegistrationCode> findByUsed(boolean used);
    
    List<RegistrationCode> findByExpiresAtBefore(LocalDateTime dateTime);
    
    boolean existsByCode(String code);
}