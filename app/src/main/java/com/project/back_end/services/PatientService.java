package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public List<AppointmentDTO> filterByCondition(String condition, Long patientId) {
        int status = condition.equalsIgnoreCase("past") ? 1 : 0;
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(patientId, status);
        return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
    }

    public List<AppointmentDTO> filterByDoctor(String name, Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name);
        return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
    }

    public List<AppointmentDTO> filterByDoctorAndCondition(String name, String condition, Long patientId) {
        int status = condition.equalsIgnoreCase("past") ? 1 : 0;
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, name, status);
        return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
    }

    public ResponseEntity<Patient> getPatientDetails(String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        return patientOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    public List<com.project.back_end.models.Doctor> filterDoctorsByNameSpecilityandTime(String name, String spec, String time) {
        // Implementation delegated to DoctorService or reused from DoctorRepository
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> filterDoctorByTimeAndSpecility(String spec, String time) {
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> filterDoctorByNameAndSpecility(String name, String spec) {
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> filterDoctorByNameAndTime(String name, String time) {
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> filterDoctorBySpecility(String spec) {
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> filterDoctorsByTime(String time) {
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> findDoctorByName(String name) {
        return List.of();
    }

    public List<com.project.back_end.models.Doctor> getDoctors() {
        return List.of();
    }
}