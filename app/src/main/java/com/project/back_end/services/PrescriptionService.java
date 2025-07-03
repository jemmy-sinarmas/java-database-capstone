package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repositories.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            // Check if prescription already exists for the given appointment
            Prescription existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existing != null) {
                response.put("message", "Prescription already exists for this appointment");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Save the prescription
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Failed to save prescription due to server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription != null) {
                response.put("prescription", prescription);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "No prescription found for this appointment");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Failed to fetch prescription due to server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
