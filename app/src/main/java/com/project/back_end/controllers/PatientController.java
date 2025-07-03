package com.project.back_end.controllers;

import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    // 1. Get Patient Details by token
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        boolean isValid = service.validateToken(token, "patient");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }

        Patient patient = patientService.getPatientDetails(token);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Patient not found.");
        }

        return ResponseEntity.ok(patient);
    }

    // 2. Create a New Patient
    @PostMapping
    public ResponseEntity<?> createPatient(@Validated @RequestBody Patient patient) {
        boolean exists = service.patientExists(patient.getEmail(), patient.getPhoneNumber());
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Patient with email or phone number already exists.");
        }

        boolean created = patientService.createPatient(patient);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Signup successful.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error.");
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody Login login) {
        String token = service.validatePatientLogin(login);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password.");
        }
        return ResponseEntity.ok(token);
    }

    // 4. Get Patient Appointments by patient id and token
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, "patient");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }

        List<Appointment> appointments = patientService.getPatientAppointment(id);
        if (appointments == null || appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No appointments found for patient.");
        }

        return ResponseEntity.ok(appointments);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, "patient");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }

        List<Appointment> filteredAppointments = service.filterPatient(condition, name, token);
        return ResponseEntity.ok(filteredAppointments);
    }
}
