package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    // 1. Get appointments by date and patientName - accessible only by doctors
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        boolean isValidToken = service.validateToken(token, "doctor");
        if (!isValidToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token for doctor access.");
        }

        List<Appointment> appointments = appointmentService.getAppointments(date, patientName);
        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No appointments found for the given date and patient.");
        }

        return ResponseEntity.ok(appointments);
    }

    // 2. Book new appointment - accessible only by patients
    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @PathVariable String token,
            @Validated @RequestBody Appointment appointment) {

        boolean isValidToken = service.validateToken(token, "patient");
        if (!isValidToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token for patient access.");
        }

        // Validate appointment details (doctor availability, time conflicts)
        String validationMsg = service.validateAppointment(appointment);
        if (!validationMsg.equals("valid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationMsg);
        }

        boolean booked = appointmentService.bookAppointment(appointment);
        if (booked) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Appointment successfully booked.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Doctor ID is invalid or slot already taken.");
        }
    }

    // 3. Update existing appointment - accessible only by patients
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String token,
            @Validated @RequestBody Appointment appointment) {

        boolean isValidToken = service.validateToken(token, "patient");
        if (!isValidToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token for patient access.");
        }

        boolean updated = appointmentService.updateAppointment(appointment);
        if (updated) {
            return ResponseEntity.ok("Appointment successfully updated.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Appointment not found or update failed.");
        }
    }

    // 4. Cancel appointment - accessible only by patients
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        boolean isValidToken = service.validateToken(token, "patient");
        if (!isValidToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token for patient access.");
        }

        boolean cancelled = appointmentService.cancelAppointment(id);
        if (cancelled) {
            return ResponseEntity.ok("Appointment successfully cancelled.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Appointment not found or cancellation failed.");
        }
    }
}
