package com.sportsbooking.repository;

import com.sportsbooking.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

    @Query("""
        select d
        from BookingDetail d
        join fetch d.schedule s
        join fetch s.court
        join fetch s.timeSlot
        where d.booking.id = :bookingId
        order by s.id
        """)
    List<BookingDetail> findDetailsByBookingId(@Param("bookingId") Long bookingId);
}
