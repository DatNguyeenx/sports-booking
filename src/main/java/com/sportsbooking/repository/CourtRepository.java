package com.sportsbooking.repository;

import com.sportsbooking.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface CourtRepository
        extends JpaRepository<Court, Long> {

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = "sportType")
    List<Court> findByActiveTrueOrderByCodeAsc();

    List<Court> findBySportType_IdAndActiveTrueOrderByCodeAsc(
            Long sportTypeId
    );
}
