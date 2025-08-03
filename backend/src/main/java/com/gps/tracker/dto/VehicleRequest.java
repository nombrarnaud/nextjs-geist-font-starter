package com.gps.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {
    
    @NotBlank
    private String name;
    
    @NotNull
    private Double latitude;
    
    @NotNull
    private Double longitude;
    
    @NotNull
    private Double speed;
    
    @NotNull
    private Double altitude;
    
    @NotNull
    private Double weight;
    
    private Boolean isOnline = true;
}
