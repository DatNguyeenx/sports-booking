package com.sportsbooking.repository;

import com.sportsbooking.entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportTypeRepository
        extends JpaRepository<SportType, Long> {

    boolean existsByName(String name);
}