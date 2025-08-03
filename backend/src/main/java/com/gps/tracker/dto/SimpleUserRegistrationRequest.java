package com.gps.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SimpleUserRegistrationRequest {
    
    @NotBlank
    private String phone;
    
    @NotBlank
    private String identityCardNumber;
    
    @NotBlank
    private String fullName;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
}
