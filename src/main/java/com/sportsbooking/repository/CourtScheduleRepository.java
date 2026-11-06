package com.sportsbooking.repository;

import com.sportsbooking.entity.CourtSchedule;
import com.sportsbooking.enums.ScheduleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CourtScheduleRepository extends JpaRepository<CourtSchedule, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from CourtSchedule s where s.id = :id")
    Optional<CourtSchedule> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        select s
        from CourtSchedule s
        join fetch s.court c
        join fetch s.timeSlot t
        where c.sportType.id = :sportTypeId
          and c.active = true
          and s.playDate = :playDate
          and s.status = :status
          and s.activeBooking is null
        order by c.code, t.startTime
        """)
    List<CourtSchedule> findAvailableSchedules(
        @Param("sportTypeId") Long sportTypeId,
        @Param("playDate") LocalDate playDate,
        @Param("status") ScheduleStatus status
    );

    List<CourtSchedule> findByActiveBooking_IdOrderByIdAsc(Long bookingId);
}
