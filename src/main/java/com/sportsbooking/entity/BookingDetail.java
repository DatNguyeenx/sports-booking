package com.sportsbooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "booking_details",
        schema = "dbo",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_booking_details_booking_schedule",
                        columnNames = {
                                "booking_id",
                                "schedule_id"
                        }
                )
        }
)
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private CourtSchedule schedule;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 18,
            scale = 2
    )
    private BigDecimal unitPrice;

    public BookingDetail() {
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public CourtSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(CourtSchedule schedule) {
        this.schedule = schedule;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}