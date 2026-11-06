package com.sportsbooking.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots", schema = "dbo")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "start_time",
            nullable = false,
            unique = true,
            columnDefinition = "TIME(0)"
    )
    private LocalTime startTime;

    @Column(
            name = "end_time",
            nullable = false,
            columnDefinition = "TIME(0)"
    )
    private LocalTime endTime;

    public TimeSlot() {
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}