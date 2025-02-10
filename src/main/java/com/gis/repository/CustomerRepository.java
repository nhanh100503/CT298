package com.gis.repository;

import com.gis.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByEmailAndIsOutsideFalse(String email);
    Optional<Customer> findByEmailAndIsOutsideFalse(String email);
    Optional<Customer> findByIsOutsideTrueAndProviderNameAndProviderId(String providerName, String providerId);
}
