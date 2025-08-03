package com.gps.tracker.service;

import com.gps.tracker.dto.*;
import com.gps.tracker.model.BusinessUser;
import com.gps.tracker.model.SimpleUser;
import com.gps.tracker.model.User;
import com.gps.tracker.repository.UserRepository;
import com.gps.tracker.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken((User) authentication.getPrincipal(), loginRequest.isRememberMe());
        
        User user = (User) authentication.getPrincipal();
        return AuthResponse.from(jwt, user);
    }
    
    public void registerBusinessUser(BusinessUserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Erreur: L'email est déjà utilisé!");
        }
        
        BusinessUser user = new BusinessUser(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getManagerName(), // Using managerName as fullName for business users
            request.getPhone(),
            request.getCompanyName(),
            request.getRegistrationNumber(),
            request.getManagerName()
        );
        
        userRepository.save(user);
    }
    
    public void registerSimpleUser(SimpleUserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Erreur: L'email est déjà utilisé!");
        }
        
        SimpleUser user = new SimpleUser(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFullName(),
            request.getPhone(),
            request.getIdentityCardNumber()
        );
        
        userRepository.save(user);
    }
    
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return UserDto.from(user);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
    
    public UserDto updateUserProfile(UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
            
            currentUser.setFullName(userDto.getFullName());
            currentUser.setPhone(userDto.getPhone());
            
            if (currentUser instanceof BusinessUser businessUser && userDto.getCompanyName() != null) {
                businessUser.setCompanyName(userDto.getCompanyName());
                businessUser.setRegistrationNumber(userDto.getRegistrationNumber());
                businessUser.setManagerName(userDto.getManagerName());
            } else if (currentUser instanceof SimpleUser simpleUser && userDto.getIdentityCardNumber() != null) {
                simpleUser.setIdentityCardNumber(userDto.getIdentityCardNumber());
            }
            
            User savedUser = userRepository.save(currentUser);
            return UserDto.from(savedUser);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
}
