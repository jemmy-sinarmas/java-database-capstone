package com.project.back_end.repo;

import com.project.back_end.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Custom method to find Admin by username
    Admin findByUsername(String username);
}
