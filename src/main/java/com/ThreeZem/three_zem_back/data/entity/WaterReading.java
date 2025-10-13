package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
public class WaterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(name = "value", nullable = false)
    private Float value;

    @Column(name = "reading_time", nullable = false)
    private LocalDateTime readingTime;

    public WaterReading() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }
}