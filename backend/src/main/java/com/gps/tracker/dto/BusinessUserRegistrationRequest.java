package com.gps.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BusinessUserRegistrationRequest {
    
    @NotBlank
    private String companyName;
    
    @NotBlank
    private String registrationNumber;
    
    @NotBlank
    private String phone;
    
    @NotBlank
    private String managerName;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
}
