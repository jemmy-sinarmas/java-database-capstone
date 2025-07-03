package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Optional<Patient> patientOpt = patientRepository.findByEmail(email);

            if (patientOpt.isEmpty() || !patientOpt.get().getId().equals(id)) {
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> appointmentDTOS = appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = switch (condition.toLowerCase()) {
                case "past" -> 1;
                case "future" -> 0;
                default -> -1;
            };

            if (status == -1) {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<AppointmentDTO> dtos = appointmentRepository.findByPatientIdAndStatus(id, status)
                    .stream().map(AppointmentDTO::new).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> dtos = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name)
                    .stream().map(AppointmentDTO::new).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments by doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = switch (condition.toLowerCase()) {
                case "past" -> 1;
                case "future" -> 0;
                default -> -1;
            };

            if (status == -1) {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<AppointmentDTO> dtos = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, name, status)
                    .stream().map(AppointmentDTO::new).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments by doctor and condition");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Optional<Patient> patient = patientRepository.findByEmail(email);
            if (patient.isEmpty()) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("patient", patient.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving patient details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
