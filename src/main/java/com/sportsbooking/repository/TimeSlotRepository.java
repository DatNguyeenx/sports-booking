package com.sportsbooking.repository;

import com.sportsbooking.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findAllByOrderByStartTimeAsc();

    boolean existsByStartTime(LocalTime startTime);
}
