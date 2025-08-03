package com.gps.tracker.dto;

import com.gps.tracker.model.Vehicle;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VehicleDto {
    
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double altitude;
    private Double weight;
    private Boolean isOnline;
    private Double totalDistance;
    private LocalDateTime lastUpdate;
    private Long userId;
    
    public static VehicleDto from(Vehicle vehicle) {
        return new VehicleDto(
            vehicle.getId(),
            vehicle.getName(),
            vehicle.getLatitude(),
            vehicle.getLongitude(),
            vehicle.getSpeed(),
            vehicle.getAltitude(),
            vehicle.getWeight(),
            vehicle.getIsOnline(),
            vehicle.getTotalDistance(),
            vehicle.getLastUpdate(),
            vehicle.getUser().getId()
        );
    }
}
