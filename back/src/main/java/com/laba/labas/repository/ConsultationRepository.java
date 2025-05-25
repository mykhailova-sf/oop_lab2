package com.laba.labas.repository;

import com.laba.labas.model.entity.Consultation;
import com.laba.labas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    
    List<Consultation> findByPatient(User patient);
    
    List<Consultation> findByDoctor(User doctor);
    
    List<Consultation> findByStatus(Consultation.Status status);
    
    List<Consultation> findByPatientAndStatus(User patient, Consultation.Status status);
    
    List<Consultation> findByDoctorAndStatus(User doctor, Consultation.Status status);
}