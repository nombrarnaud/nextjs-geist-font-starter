package com.gps.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.vehicle.simulation.enabled", havingValue = "true")
public class VehicleSimulationService {
    
    @Autowired
    private VehicleService vehicleService;
    
    @Scheduled(fixedDelayString = "${app.vehicle.simulation.interval}")
    public void simulateVehicleMovements() {
        try {
            vehicleService.simulateVehicleMovement();
        } catch (Exception e) {
            // Log error but don't stop the scheduler
            System.err.println("Error during vehicle simulation: " + e.getMessage());
        }
    }
}
