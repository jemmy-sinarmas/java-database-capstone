package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private Service service;

    @Autowired
    private AppointmentService appointmentService;

    // 1. Save a new prescription (doctor only)
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @PathVariable String token,
            @Validated @RequestBody Prescription prescription) {

        boolean isValid = service.validateToken(token, "doctor");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }

        // Update appointment status (e.g., mark prescription as issued)
        boolean updated = appointmentService.updateAppointmentStatus(
                prescription.getAppointmentId(), "PRESCRIPTION_ISSUED");

        if (!updated) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update appointment status.");
        }

        boolean saved = prescriptionService.savePrescription(prescription);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Prescription saved successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save prescription.");
        }
    }

    // 2. Get prescription by appointment ID (doctor only)
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        boolean isValid = service.validateToken(token, "doctor");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }

        Prescription prescription = prescriptionService.getPrescription(appointmentId);
        if (prescription == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No prescription found for appointment id: " + appointmentId);
        }

        return ResponseEntity.ok(prescription);
    }
}
