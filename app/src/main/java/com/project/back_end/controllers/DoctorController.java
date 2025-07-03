package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private Service service;

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, user);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token for user: " + user);
        }

        Map<String, Object> availability = doctorService.getDoctorAvailability(doctorId, date);
        if (availability == null || availability.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No availability found for doctor with id: " + doctorId);
        }

        return ResponseEntity.ok(availability);
    }

    // 2. Get list of all doctors
    @GetMapping
    public ResponseEntity<?> getDoctor() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    // 3. Add new doctor - admin role required
    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(
            @Validated @RequestBody Doctor doctor,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, "admin");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Admin token required.");
        }

        boolean exists = doctorService.existsByEmail(doctor.getEmail());
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Doctor already exists.");
        }

        boolean saved = doctorService.saveDoctor(doctor);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Doctor added to db.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Some internal error occurred.");
        }
    }

    // 4. Doctor login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@Validated @RequestBody Login login) {
        Map<String, Object> loginResponse = doctorService.validateDoctor(login);
        if (loginResponse == null || loginResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password.");
        }
        return ResponseEntity.ok(loginResponse);
    }

    // 5. Update doctor - admin role required
    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @Validated @RequestBody Doctor doctor,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, "admin");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Admin token required.");
        }

        boolean updated = doctorService.updateDoctor(doctor);
        if (updated) {
            return ResponseEntity.ok("Doctor updated.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Doctor not found.");
        }
    }

    // 6. Delete doctor by ID - admin role required
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, "admin");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Admin token required.");
        }

        boolean deleted = doctorService.deleteDoctor(id);
        if (deleted) {
            return ResponseEntity.ok("Doctor deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Doctor not found with id: " + id);
        }
    }

    // 7. Filter doctors by name, time, and speciality
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        List<Doctor> filteredDoctors = service.filterDoctor(name, time, speciality);
        return ResponseEntity.ok(Map.of("doctors", filteredDoctors));
    }
}
