package com.project.back_end.repo;

import com.project.back_end.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 1. Find by exact email (used for login)
    Doctor findByEmail(String email);

    // 2. Find doctors by partial name (case-sensitive)
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(String name);

    // 3. Find doctors by name and specialty (both case-insensitive)
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    // 4. Find doctors by specialty only (case-insensitive)
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
