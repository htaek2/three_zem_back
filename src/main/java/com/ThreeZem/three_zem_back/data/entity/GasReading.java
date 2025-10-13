package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
public class GasReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "value", nullable = false)
    private Float value;

    @Column(name = "reading_time", nullable = false)
    private LocalDateTime readingTime;

    public GasReading() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }
}