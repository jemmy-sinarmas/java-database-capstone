package com.project.back_end.repo;

import com.project.back_end.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Find appointments for a doctor within a date/time range
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    // 2. Filter by doctorId and patient name (case-insensitive) in time range
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor LEFT JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%')) AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    // 3. Delete all appointments for a doctor
    @Transactional
    @Modifying
    void deleteAllByDoctorId(Long doctorId);

    // 4. Find appointments by patient ID
    List<Appointment> findByPatientId(Long patientId);

    // 5. Find appointments by patient ID and status ordered by time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // 6. Filter appointments by doctor name and patient ID
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    // 7. Filter appointments by doctor name, patient ID, and status
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);

    // 8. Update appointment status
    @Transactional
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status, long id);
}
