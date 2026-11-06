package com.sportsbooking.entity;

import com.sportsbooking.enums.ScheduleStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "court_schedules",
        schema = "dbo",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_court_schedules_court_date_slot",
                        columnNames = {
                                "court_id",
                                "play_date",
                                "time_slot_id"
                        }
                )
        }
)
public class CourtSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ScheduleStatus status = ScheduleStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_booking_id")
    private Booking activeBooking;

    public CourtSchedule() {
    }

    public Long getId() {
        return id;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public LocalDate getPlayDate() {
        return playDate;
    }

    public void setPlayDate(LocalDate playDate) {
        this.playDate = playDate;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public Booking getActiveBooking() {
        return activeBooking;
    }

    public void setActiveBooking(Booking activeBooking) {
        this.activeBooking = activeBooking;
    }
}