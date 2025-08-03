package com.gps.tracker.dto;

import com.gps.tracker.model.User;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private UserDto user;
    
    public static AuthResponse from(String token, User user) {
        return new AuthResponse(token, UserDto.from(user));
    }
}
