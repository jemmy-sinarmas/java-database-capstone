package com.project.back_end.repo;

import com.project.back_end.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find a patient by email
    Patient findByEmail(String email);

    // Find a patient by email OR phone number
    Patient findByEmailOrPhone(String email, String phone);
}
