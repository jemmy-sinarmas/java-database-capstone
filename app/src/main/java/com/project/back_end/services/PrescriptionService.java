package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Optional<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Prescription already exists for this appointment"));
        }
        try {
            prescriptionRepository.save(prescription);
            return ResponseEntity.status(201).body(Map.of("message", "Prescription saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        try {
            Optional<Prescription> presOpt = prescriptionRepository.findByAppointmentId(appointmentId);
            if (presOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Prescription not found"));
            }
            return ResponseEntity.ok(Map.of("prescription", presOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving prescription"));
        }
    }
}
