package com.gps.tracker.repository;

import com.gps.tracker.model.Vehicle;
import com.gps.tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    Page<Vehicle> findByUser(User user, Pageable pageable);
    
    List<Vehicle> findByUser(User user);
    
    Optional<Vehicle> findByIdAndUser(Long id, User user);
    
    @Query("SELECT v FROM Vehicle v WHERE v.user = :user AND v.isOnline = true")
    List<Vehicle> findOnlineVehiclesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.user = :user AND v.isOnline = true")
    long countOnlineVehiclesByUser(@Param("user") User user);
    
    @Query("SELECT AVG(v.speed) FROM Vehicle v WHERE v.user = :user AND v.speed > 0")
    Double getAverageSpeedByUser(@Param("user") User user);
    
    @Query("SELECT SUM(v.totalDistance) FROM Vehicle v WHERE v.user = :user")
    Double getTotalDistanceByUser(@Param("user") User user);
}
