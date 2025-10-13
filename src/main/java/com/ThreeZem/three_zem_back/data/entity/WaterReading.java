package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
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

    public WaterReading(Floor floor, LocalDateTime readingTime, float value) {
        this.floor = floor;
        this.readingTime = readingTime;
        this.value = value;
    }

}