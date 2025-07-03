package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Service {
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService, DoctorRepository doctorRepository,
                   AdminRepository adminRepository, PatientRepository patientRepository,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        boolean isValid = tokenService.validateToken(token, user);
        if (!isValid) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }
        return null;
    }

    public ResponseEntity<Map<String, String>> validateAdmin(String username, String password) {
        try {
            var admin = adminRepository.findByUsername(username);
            if (admin == null || !admin.getPassword().equals(password)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
            }
            String token = tokenService.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    public List<Doctor> filterDoctor(String name, String time, String spec, List<Doctor> doctors) {
        return patientService.filterDoctors(name, time, spec, doctors);
    }

    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doc = doctorRepository.findById(appointment.getDoctorId());
        if (doc.isEmpty()) return -1;
        var slots = doc.get().getAvailableTimes().getOrDefault(appointment.getDate(), List.of());
        return slots.stream().anyMatch(slot -> slot.getStartTime().equals(appointment.getTime())) ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmail(patient.getEmail()).isEmpty()
                && patientRepository.findByPhone(patient.getPhone()).isEmpty();
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(String email, String password) {
        try {
            var patient = patientRepository.findByEmail(email);
            if (patient == null || !patient.getPassword().equals(password)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid login credentials"));
            }
            String token = tokenService.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    public List<?> filterPatient(String condition, String name, String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty()) return List.of();
        Patient patient = patientOpt.get();
        if (condition != null && name != null) {
            return patientService.filterByDoctorAndCondition(name, condition, patient.getId());
        } else if (condition != null) {
            return patientService.filterByCondition(condition, patient.getId());
        } else if (name != null) {
            return patientService.filterByDoctor(name, patient.getId());
        } else {
            return patientService.getPatientAppointment(patient.getId());
        }
    }
}
