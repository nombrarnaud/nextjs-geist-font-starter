package com.gps.tracker.controller;

import com.gps.tracker.dto.*;
import com.gps.tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erreur: Email ou mot de passe incorrect!"));
        }
    }
    
    @PostMapping("/register/business")
    public ResponseEntity<?> registerBusinessUser(@Valid @RequestBody BusinessUserRegistrationRequest signUpRequest) {
        try {
            authService.registerBusinessUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("Compte entreprise créé avec succès!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/register/simple")
    public ResponseEntity<?> registerSimpleUser(@Valid @RequestBody SimpleUserRegistrationRequest signUpRequest) {
        try {
            authService.registerSimpleUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("Compte personnel créé avec succès!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/user/profile")
    public ResponseEntity<?> getCurrentUser() {
        try {
            UserDto user = authService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/user/profile")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = authService.updateUserProfile(userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Helper class for response messages
    public static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
