package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService, AdminRepository adminRepository,
                   DoctorRepository doctorRepository, PatientRepository patientRepository,
                   DoctorService doctorService, PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("error", "Token is expired or invalid");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin != null) {
                if (admin.getPassword().equals(receivedAdmin.getPassword())) {
                    String token = tokenService.generateToken(admin.getUsername());
                    response.put("token", token);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.put("error", "Invalid password");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            } else {
                response.put("error", "Admin not found");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        if (name != null && specialty != null && time != null)
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        if (name != null && time != null)
            return doctorService.filterDoctorByNameAndTime(name, time);
        if (name != null && specialty != null)
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        if (specialty != null && time != null)
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        if (name != null)
            return doctorService.findDoctorByName(name);
        if (specialty != null)
            return doctorService.filterDoctorBySpecility(specialty);
        if (time != null)
            return doctorService.filterDoctorsByTime(time);
        return doctorService.getDoctors();
    }

    public int validateAppointment(Appointment appointment) {
        try {
            var doctor = doctorRepository.findById(appointment.getDoctorId());
            if (doctor.isEmpty()) return -1;

            var availabilityMap = doctorService.getDoctorAvailability(
                appointment.getDoctorId(), 
                appointment.getAppointmentTime().toLocalDate()
            );
            var availableSlots = availabilityMap.get("availableSlots");
            if (availableSlots == null) return 0;

            String reqTime = appointment.getAppointmentTime().toLocalTime().toString();
            for (String slot : availableSlots) {
                if (slot != null && slot.startsWith(reqTime)) return 1;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient != null) {
                if (patient.getPassword().equals(login.getPassword())) {
                    String token = tokenService.generateToken(patient.getEmail());
                    response.put("token", token);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.put("error", "Invalid password");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            } else {
                response.put("error", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.extractUsername(token);

        if (condition != null && name != null) {
            response.put("appointments", patientService.filterByDoctorAndCondition(email, name, condition));
        } else if (condition != null) {
            response.put("appointments", patientService.filterByCondition(email, condition));
        } else if (name != null) {
            response.put("appointments", patientService.filterByDoctor(email, name));
        } else {
            response.put("appointments", patientService.getPatientAppointment(email));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}