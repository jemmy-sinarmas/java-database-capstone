package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import com.project.back_end.service.Service;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    // Admin Dashboard Handler
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, String> validationResult = service.validateToken(token, "admin");

        if (validationResult.isEmpty()) {
            // Token valid – render admin dashboard view
            return "admin/adminDashboard";
        } else {
            // Token invalid – redirect to login
            return "redirect:/";
        }
    }

    // Doctor Dashboard Handler
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, String> validationResult = service.validateToken(token, "doctor");

        if (validationResult.isEmpty()) {
            // Token valid – render doctor dashboard view
            return "doctor/doctorDashboard";
        } else {
            // Token invalid – redirect to login
            return "redirect:/";
        }
    }
}
