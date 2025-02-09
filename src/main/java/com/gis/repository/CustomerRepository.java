package com.gis.repository;

import com.gis.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByEmailAndIsOutsideFalse(String email);
}
