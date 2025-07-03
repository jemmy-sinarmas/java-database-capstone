package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              Service service,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment newAppointment) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(newAppointment.getId());

        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Appointment not found"));
        }

        Appointment existingAppointment = optionalAppointment.get();

        if (!existingAppointment.getPatientId().equals(newAppointment.getPatientId())) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized update attempt"));
        }

        int isValidTime = service.validateAppointment(newAppointment);
        if (isValidTime != 1) {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid time slot"));
        }

        try {
            appointmentRepository.save(newAppointment);
            return ResponseEntity.ok(Map.of("message", "Appointment updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update appointment"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(Long appointmentId, Long patientId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Appointment not found"));
        }
        Appointment appointment = optionalAppointment.get();
        if (!appointment.getPatientId().equals(patientId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized cancel attempt"));
        }
        try {
            appointmentRepository.delete(appointment);
            return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error cancelling appointment"));
        }
    }

    @Transactional
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        if (patientName != null && !patientName.isEmpty()) {
            return appointmentRepository.findByDoctorIdAndDateAndPatientNameContainingIgnoreCase(doctorId, date, patientName);
        }
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> changeStatus(Long appointmentId, int newStatus) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Appointment not found"));
        }
        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(newStatus);
        try {
            appointmentRepository.save(appointment);
            return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error updating status"));
        }
    }
}