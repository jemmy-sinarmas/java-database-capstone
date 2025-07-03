package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.repository.DoctorRepository;
import com.project.back_end.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> availableSlots = new ArrayList<>(Arrays.asList(
                "08:00", "09:00", "10:00", "11:00", "12:00",
                "14:00", "15:00", "16:00", "17:00"
        ));

        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );

        Set<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toSet());

        return availableSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) return -1;
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

    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Optional<Doctor> doctor = doctorRepository.findByEmail(login.getEmail());
        Map<String, String> response = new HashMap<>();
        if (doctor.isPresent() && doctor.get().getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken("doctor", doctor.get().getId());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorRepository.findByNameLike("%" + name + "%"));
        return map;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);
        doctors = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", doctors);
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes().stream().anyMatch(time -> {
                    try {
                        LocalTime localTime = LocalTime.parse(time);
                        return "AM".equalsIgnoreCase(amOrPm) ? localTime.isBefore(LocalTime.NOON) : localTime.isAfter(LocalTime.NOON);
                    } catch (Exception e) {
                        return false;
                    }
                }))
                .collect(Collectors.toList());
    }
}
