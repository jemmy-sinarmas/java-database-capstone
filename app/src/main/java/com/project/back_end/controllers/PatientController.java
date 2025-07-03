package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 1. Get Patient Details
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return patientService.getPatientDetails(token);
    }

    // 2. Create New Patient
    @PostMapping()
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            return ResponseEntity.status(409).body(Map.of("message", "Patient with email id or phone no already exist"));
        }

        int created = patientService.createPatient(patient);
        if (created == 1) {
            return ResponseEntity.ok(Map.of("message", "Signup successful"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return patientService.getPatientAppointment(id, token);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return service.filterPatient(condition, name, token);
    }
}
