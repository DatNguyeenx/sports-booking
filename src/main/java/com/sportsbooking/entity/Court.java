package com.sportsbooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "courts", schema = "dbo")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sport_type_id", nullable = false)
    private SportType sportType;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(
            name = "price_per_slot",
            nullable = false,
            precision = 18,
            scale = 2
    )
    private BigDecimal pricePerSlot;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Court() {
    }

    public Long getId() {
        return id;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPricePerSlot() {
        return pricePerSlot;
    }

    public void setPricePerSlot(BigDecimal pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}