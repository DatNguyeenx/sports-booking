package com.sportsbooking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "sport_types", schema = "dbo")
public class SportType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(
            name = "name",
            nullable = false,
            unique = true,
            length = 100
    )
    private String name;

    public SportType() {
    }

    public SportType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}