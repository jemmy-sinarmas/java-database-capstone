package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        List<String> availability = doctorService.getDoctorAvailability(doctorId, date);
        return ResponseEntity.ok(Map.of("availability", availability));
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<List<Doctor>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(doctors);
    }

    // 3. Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(@RequestBody Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor added to db"));
        } else if (result == -1) {
            return ResponseEntity.status(409).body(Map.of("message", "Doctor already exists"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.deleteDoctor(id);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found with id"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        Map<String, Object> filteredDoctors = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors);
    }
}
