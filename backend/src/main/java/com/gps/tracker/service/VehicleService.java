package com.gps.tracker.service;

import com.gps.tracker.dto.*;
import com.gps.tracker.model.User;
import com.gps.tracker.model.Vehicle;
import com.gps.tracker.model.VehicleHistory;
import com.gps.tracker.repository.VehicleHistoryRepository;
import com.gps.tracker.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private VehicleHistoryRepository vehicleHistoryRepository;
    
    public PaginatedResponse<VehicleDto> getUserVehicles(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        Page<Vehicle> vehiclePage = vehicleRepository.findByUser(currentUser, pageable);
        Page<VehicleDto> vehicleDtoPage = vehiclePage.map(VehicleDto::from);
        
        return PaginatedResponse.from(vehicleDtoPage);
    }
    
    public List<VehicleDto> getAllUserVehicles() {
        User currentUser = getCurrentUser();
        List<Vehicle> vehicles = vehicleRepository.findByUser(currentUser);
        return vehicles.stream().map(VehicleDto::from).collect(Collectors.toList());
    }
    
    public VehicleDto getVehicleById(Long vehicleId) {
        User currentUser = getCurrentUser();
        Vehicle vehicle = vehicleRepository.findByIdAndUser(vehicleId, currentUser)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        
        return VehicleDto.from(vehicle);
    }
    
    public VehicleDto createVehicle(VehicleRequest request) {
        User currentUser = getCurrentUser();
        
        Vehicle vehicle = new Vehicle(
            request.getName(),
            request.getLatitude(),
            request.getLongitude(),
            request.getSpeed(),
            request.getAltitude(),
            request.getWeight(),
            request.getIsOnline(),
            currentUser
        );
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Create initial history entry
        createHistoryEntry(savedVehicle);
        
        return VehicleDto.from(savedVehicle);
    }
    
    public VehicleDto updateVehicle(Long vehicleId, VehicleRequest request) {
        User currentUser = getCurrentUser();
        Vehicle vehicle = vehicleRepository.findByIdAndUser(vehicleId, currentUser)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        
        // Store previous position for distance calculation
        double prevLat = vehicle.getLatitude();
        double prevLng = vehicle.getLongitude();
        
        vehicle.setName(request.getName());
        vehicle.setLatitude(request.getLatitude());
        vehicle.setLongitude(request.getLongitude());
        vehicle.setSpeed(request.getSpeed());
        vehicle.setAltitude(request.getAltitude());
        vehicle.setWeight(request.getWeight());
        vehicle.setIsOnline(request.getIsOnline());
        
        // Calculate and update total distance if position changed
        if (prevLat != request.getLatitude() || prevLng != request.getLongitude()) {
            double distance = calculateDistance(prevLat, prevLng, request.getLatitude(), request.getLongitude());
            vehicle.setTotalDistance(vehicle.getTotalDistance() + distance);
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Create history entry for the update
        createHistoryEntry(savedVehicle);
        
        return VehicleDto.from(savedVehicle);
    }
    
    public void deleteVehicle(Long vehicleId) {
        User currentUser = getCurrentUser();
        Vehicle vehicle = vehicleRepository.findByIdAndUser(vehicleId, currentUser)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        
        // Delete associated history first
        vehicleHistoryRepository.deleteByVehicle(vehicle);
        
        // Delete the vehicle
        vehicleRepository.delete(vehicle);
    }
    
    public List<VehicleHistoryDto> getVehicleHistory(Long vehicleId, int months) {
        User currentUser = getCurrentUser();
        Vehicle vehicle = vehicleRepository.findByIdAndUser(vehicleId, currentUser)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
        
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        List<VehicleHistory> history = vehicleHistoryRepository
                .findByVehicleAndTimestampAfterOrderByTimestampDesc(vehicle, startDate);
        
        return history.stream().map(VehicleHistoryDto::from).collect(Collectors.toList());
    }
    
    public void simulateVehicleMovement() {
        List<Vehicle> onlineVehicles = vehicleRepository.findAll().stream()
                .filter(Vehicle::getIsOnline)
                .collect(Collectors.toList());
        
        for (Vehicle vehicle : onlineVehicles) {
            // Simulate small random movement
            double latChange = (Math.random() - 0.5) * 0.001; // ~100m max change
            double lngChange = (Math.random() - 0.5) * 0.001;
            double speedChange = (Math.random() - 0.5) * 10; // ±5 km/h change
            
            double newLat = vehicle.getLatitude() + latChange;
            double newLng = vehicle.getLongitude() + lngChange;
            double newSpeed = Math.max(0, vehicle.getSpeed() + speedChange);
            
            // Calculate distance moved
            double distance = calculateDistance(vehicle.getLatitude(), vehicle.getLongitude(), newLat, newLng);
            
            vehicle.setLatitude(newLat);
            vehicle.setLongitude(newLng);
            vehicle.setSpeed(newSpeed);
            vehicle.setTotalDistance(vehicle.getTotalDistance() + distance);
            
            vehicleRepository.save(vehicle);
            
            // Create history entry
            createHistoryEntry(vehicle);
        }
    }
    
    private void createHistoryEntry(Vehicle vehicle) {
        VehicleHistory history = new VehicleHistory(
            LocalDateTime.now(),
            vehicle.getLatitude(),
            vehicle.getLongitude(),
            vehicle.getSpeed(),
            vehicle.getAltitude(),
            vehicle.getWeight(),
            vehicle
        );
        
        vehicleHistoryRepository.save(history);
    }
    
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Haversine formula to calculate distance between two points
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
}
