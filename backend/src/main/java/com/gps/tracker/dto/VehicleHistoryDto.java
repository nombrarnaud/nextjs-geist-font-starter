package com.gps.tracker.dto;

import com.gps.tracker.model.VehicleHistory;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VehicleHistoryDto {
    
    private Long id;
    private LocalDateTime timestamp;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double altitude;
    private Double weight;
    private Long vehicleId;
    
    public static VehicleHistoryDto from(VehicleHistory history) {
        return new VehicleHistoryDto(
            history.getId(),
            history.getTimestamp(),
            history.getLatitude(),
            history.getLongitude(),
            history.getSpeed(),
            history.getAltitude(),
            history.getWeight(),
            history.getVehicle().getId()
        );
    }
}
