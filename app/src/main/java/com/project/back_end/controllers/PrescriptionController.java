package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    // 1. Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@RequestBody Prescription prescription, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return prescriptionService.savePrescription(prescription);
    }

    // 2. Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        return prescriptionService.getPrescription(appointmentId);
    }
}
