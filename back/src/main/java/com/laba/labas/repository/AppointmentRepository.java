package com.laba.labas.repository;

import com.laba.labas.model.entity.Appointment;
import com.laba.labas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatient(User patient);
    
    List<Appointment> findByDoctor(User doctor);
    
    List<Appointment> findByStatus(Appointment.Status status);
    
    List<Appointment> findByAppointmentType(Appointment.AppointmentType appointmentType);
    
    List<Appointment> findByPatientAndAppointmentType(User patient, Appointment.AppointmentType appointmentType);
    
    List<Appointment> findByDoctorAndAppointmentType(User doctor, Appointment.AppointmentType appointmentType);
    
    List<Appointment> findByPatientAndStatus(User patient, Appointment.Status status);
    
    List<Appointment> findByDoctorAndStatus(User doctor, Appointment.Status status);
    
    List<Appointment> findByPatientAndAppointmentTypeAndStatus(User patient, Appointment.AppointmentType appointmentType, Appointment.Status status);
}