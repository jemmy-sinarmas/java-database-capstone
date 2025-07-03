package com.project.back_end.DTO;

public class Login {

    private String email;
    private String password;

    // Default constructor
    public Login() {
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
