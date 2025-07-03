package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // GET appointments by date and patient name, token used to validate doctor access
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        Map<String, Object> appointments = appointmentService.getAppointment(patientName, date, token);
        return ResponseEntity.ok(appointments);
    }

    // POST to book an appointment, only allowed for patient with valid token
    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        int valid = service.validateAppointment(appointment);
        if (valid != 1) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid appointment time or doctor"));
        }

        int booked = appointmentService.bookAppointment(appointment);
        if (booked == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to book appointment"));
        }
    }

    // PUT to update appointment, patient token required
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(@RequestBody Appointment appointment, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        return appointmentService.updateAppointment(appointment);
    }

    // DELETE to cancel appointment by id, patient token required
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
