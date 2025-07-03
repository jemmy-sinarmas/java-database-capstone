package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Slot;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();
        List<String> bookedSlots = appointmentRepository.findByDoctorIdAndDate(doctorId, date)
                .stream().map(app -> app.getStartTime()).toList();

        return doctor.getAvailableTimes().stream()
                .filter(slot -> slot.getDate().equals(date))
                .map(Slot::getStartTime)
                .filter(time -> !bookedSlots.contains(time))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        if (doctorRepository.findById(doctor.getId()).isEmpty()) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return -1;
        try {
            appointmentRepository.deleteByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(String email, String password) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Doctor not found"));
            }
            Doctor doctor = doctorOpt.get();
            if (!doctor.getPassword().equals(password)) {
                return ResponseEntity.status(401).body(Map.of("message", "Incorrect password"));
            }
            String token = tokenService.generateToken(doctor.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    // Additional filtering methods to be added here...
}