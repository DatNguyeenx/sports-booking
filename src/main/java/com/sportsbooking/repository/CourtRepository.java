package com.sportsbooking.repository;

import com.sportsbooking.entity.Court;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CourtRepository
        extends JpaRepository<Court, Long>, JpaSpecificationExecutor<Court> {

    boolean existsByCode(String code);

    @Override
    @EntityGraph(attributePaths = "sportType")
    Page<Court> findAll(Specification<Court> specification, Pageable pageable);

    @EntityGraph(attributePaths = "sportType")
    List<Court> findByActiveTrueOrderByCodeAsc();

    List<Court> findBySportType_IdAndActiveTrueOrderByCodeAsc(Long sportTypeId);
}
