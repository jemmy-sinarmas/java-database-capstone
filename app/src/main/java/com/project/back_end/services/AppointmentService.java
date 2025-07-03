package com.project.back_end.services;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.Service;
import com.project.back_end.utils.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service as SpringService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringService
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> res = new HashMap<>();
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointment.getId());
        if (!optionalAppointment.isPresent()) {
            res.put("message", "Appointment not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        Appointment existing = optionalAppointment.get();
        if (!existing.getPatient().getId().equals(appointment.getPatient().getId())) {
            res.put("message", "Unauthorized");
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        if (!service.validateAppointment(appointment)) {
            res.put("message", "Invalid appointment time");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        appointmentRepository.save(existing);

        res.put("message", "Appointment updated successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> res = new HashMap<>();
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (!optionalAppointment.isPresent()) {
            res.put("message", "Appointment not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        Appointment appointment = optionalAppointment.get();
        Long patientIdFromToken = tokenService.extractUserId(token);
        if (!appointment.getPatient().getId().equals(patientIdFromToken)) {
            res.put("message", "Unauthorized to cancel this appointment");
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        appointmentRepository.delete(appointment);
        res.put("message", "Appointment cancelled successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Long doctorId = tokenService.extractUserId(token);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        if (pname != null && !pname.equals("null")) {
            appointments = appointments.stream()
                    .filter(app -> app.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                    .collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("appointments", appointments);
        return result;
    }

    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        Optional<Appointment> optional = appointmentRepository.findById(appointmentId);
        if (optional.isPresent()) {
            Appointment appointment = optional.get();
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        }
    }
}
