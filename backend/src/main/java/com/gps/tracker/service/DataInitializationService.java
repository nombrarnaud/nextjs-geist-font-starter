package com.gps.tracker.service;

import com.gps.tracker.model.BusinessUser;
import com.gps.tracker.model.SimpleUser;
import com.gps.tracker.model.Vehicle;
import com.gps.tracker.repository.UserRepository;
import com.gps.tracker.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            initializeData();
        }
    }
    
    private void initializeData() {
        // Create sample business user
        BusinessUser businessUser = new BusinessUser(
            "business@example.com",
            passwordEncoder.encode("password123"),
            "Jean Dupont",
            "+33123456789",
            "Transport Express SARL",
            "123456789",
            "Jean Dupont"
        );
        userRepository.save(businessUser);
        
        // Create sample simple user
        SimpleUser simpleUser = new SimpleUser(
            "simple@example.com",
            passwordEncoder.encode("password123"),
            "Marie Martin",
            "+33987654321",
            "1234567890123"
        );
        userRepository.save(simpleUser);
        
        // Create sample vehicles for business user
        Vehicle vehicle1 = new Vehicle(
            "Camion Livraison 001",
            48.8566, 2.3522, // Paris coordinates
            45.0, 35.0, 1200.0,
            true,
            businessUser
        );
        vehicle1.setTotalDistance(1250.0);
        vehicleRepository.save(vehicle1);
        
        Vehicle vehicle2 = new Vehicle(
            "Fourgon Express 002",
            48.8606, 2.3376, // Near Louvre
            0.0, 42.0, 800.0,
            true,
            businessUser
        );
        vehicle2.setTotalDistance(890.0);
        vehicleRepository.save(vehicle2);
        
        Vehicle vehicle3 = new Vehicle(
            "Utilitaire 003",
            48.8738, 2.2950, // Arc de Triomphe area
            60.0, 38.0, 1100.0,
            false,
            businessUser
        );
        vehicle3.setTotalDistance(2100.0);
        vehicleRepository.save(vehicle3);
        
        // Create sample vehicle for simple user
        Vehicle vehicle4 = new Vehicle(
            "Véhicule Personnel",
            48.8534, 2.3488, // Notre-Dame area
            30.0, 40.0, 1000.0,
            true,
            simpleUser
        );
        vehicle4.setTotalDistance(5600.0);
        vehicleRepository.save(vehicle4);
        
        System.out.println("Sample data initialized successfully!");
        System.out.println("Business User: business@example.com / password123");
        System.out.println("Simple User: simple@example.com / password123");
    }
}
