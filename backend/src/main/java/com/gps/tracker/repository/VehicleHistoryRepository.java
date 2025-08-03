package com.gps.tracker.repository;

import com.gps.tracker.model.Vehicle;
import com.gps.tracker.model.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {
    
    List<VehicleHistory> findByVehicleOrderByTimestampDesc(Vehicle vehicle);
    
    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle = :vehicle AND vh.timestamp >= :startDate ORDER BY vh.timestamp DESC")
    List<VehicleHistory> findByVehicleAndTimestampAfterOrderByTimestampDesc(
            @Param("vehicle") Vehicle vehicle, 
            @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT vh FROM VehicleHistory vh WHERE vh.vehicle = :vehicle AND vh.timestamp BETWEEN :startDate AND :endDate ORDER BY vh.timestamp DESC")
    List<VehicleHistory> findByVehicleAndTimestampBetweenOrderByTimestampDesc(
            @Param("vehicle") Vehicle vehicle, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(vh) FROM VehicleHistory vh WHERE vh.vehicle = :vehicle")
    long countByVehicle(@Param("vehicle") Vehicle vehicle);
    
    void deleteByVehicle(Vehicle vehicle);
}
