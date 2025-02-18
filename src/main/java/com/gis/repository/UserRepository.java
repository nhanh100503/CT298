package com.gis.repository;

import com.gis.enums.DriverStatus;
import com.gis.enums.ERole;
import com.gis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findByRoleAndDriverStatus(ERole role, DriverStatus driverStatus);
    List<User> findByDriverStatusNotIn(List<DriverStatus> status);
}
